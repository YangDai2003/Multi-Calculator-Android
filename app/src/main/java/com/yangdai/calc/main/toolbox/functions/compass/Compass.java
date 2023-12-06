package com.yangdai.calc.main.toolbox.functions.compass;

import static com.yangdai.calc.main.toolbox.functions.compass.CompassHelper.convertToDeg;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.icu.math.BigDecimal;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.yangdai.calc.R;
import com.yangdai.calc.main.toolbox.functions.BaseFunctionActivity;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author 30415
 */
public class Compass extends BaseFunctionActivity implements SensorEventListener {
    private SensorManager sensorManager;
    Sensor accelerationSensor, magneticFieldSensor, pressureSensor;
    private final float[] accelerometerValues = new float[3];
    private final float[] magneticValues = new float[3];

    private float heading, longitude, latitude, altitude, magneticDeclination;
    private boolean isLocationRetrieved = false;
    private TextView tvTrueHeading, tvMagneticDeclination, tvDegree, tvLocation;
    private ImageView imageViewCompass;

    private String magneticFieldStrengthStr;
    private String addressStr;
    private String pressureStr;
    private Location location;
    int isGoogleAvailable;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
    boolean permissionChecked = false;
    boolean updated = false;
    private Geocoder geocoder;
    private final HandlerThread handlerThread = new HandlerThread("LocationThread");
    private Handler handler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());

        tvDegree = findViewById(R.id.degree);
        tvLocation = findViewById(R.id.location);
        tvTrueHeading = findViewById(R.id.text_view_true_heading);
        tvMagneticDeclination = findViewById(R.id.text_view_magnetic_declination);
        imageViewCompass = findViewById(R.id.image_compass);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magneticFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        isGoogleAvailable = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        geocoder = new Geocoder(this, Locale.getDefault());

        @SuppressLint("MissingPermission") ActivityResultLauncher<String[]> locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                    boolean fineLocationGranted = Boolean.TRUE.equals(result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false));
                    boolean coarseLocationGranted = Boolean.TRUE.equals(result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false));
                    if (fineLocationGranted || coarseLocationGranted) {
                        permissionChecked = true;
                        getLocation();
                    } else {
                        // 用户拒绝了权限，可以根据需求进行相应的处理，例如显示一个提示信息或者禁用相关功能
                        Toast.makeText(getApplicationContext(), getString(R.string.permission), Toast.LENGTH_LONG).show();
                        finish();
                    }
                });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            //请求权限
            locationPermissionRequest.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        } else {
            permissionChecked = true;
            getLocation();
        }

        // 默认值为N/A。如果已检索到位置，则文本将更新为相应的值
        tvTrueHeading.setText(R.string.not_available);
        tvMagneticDeclination.setText(R.string.not_available);
    }

    @Override
    protected void setRootView() {
        setContentView(R.layout.compass);
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        if (isGoogleAvailable == ConnectionResult.SUCCESS) {
            handler.post(() -> {
                locationRequest = new LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 1000).build();
                locationCallback = new LocationCallback() {
                    @Override
                    public void onLocationResult(@NonNull LocationResult locationResult) {
                        update(locationResult.getLastLocation());
                    }
                };
                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                        .addLocationRequest(locationRequest);
                SettingsClient client = LocationServices.getSettingsClient(this);
                Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
                task.addOnFailureListener(this, e -> {
                    if (e instanceof ResolvableApiException) {
                        try {
                            ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                            resolvableApiException.startResolutionForResult(Compass.this, 2048);
                        } catch (IntentSender.SendIntentException ignored) {

                        }
                    }
                });

                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
                startLocationUpdates();
                updated = true;
            });

        } else {
            Toast.makeText(getApplicationContext(), "Not available on this device.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, handlerThread.getLooper());
    }

    private void stopLocationUpdates() {
        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }

    @SuppressLint("SetTextI18n")
    private void update(Location location) {
        if (location != null) {
            this.location = location;
            getAddress(location);
            String coordinateStr = getString(R.string.latitude) + ": " + convertToDeg(location.getLatitude())
                    + " " + getString(R.string.longitude) + ": " + convertToDeg(location.getLongitude());
            String altitudeStr = getString(R.string.altitude) + ": "
                    + BigDecimal.valueOf(location.getAltitude())
                    .setScale(2, BigDecimal.ROUND_HALF_UP) + " m";
            String speedStr = getString(R.string.speed) + ": "
                    + BigDecimal.valueOf(location.getSpeed())
                    .setScale(2, BigDecimal.ROUND_HALF_UP) + " m/s";
            int mv = (int) Math.sqrt(Math.pow(magneticValues[0], 2) +
                    Math.pow(magneticValues[1], 2) + Math.pow(magneticValues[2], 2));
            isLocationRetrieved = true;
            latitude = (float) location.getLatitude();
            longitude = (float) location.getLongitude();
            altitude = (float) location.getAltitude();
            magneticDeclination = CompassHelper.calculateMagneticDeclination(latitude, longitude, altitude);

            runOnUiThread(() -> {
                tvLocation.setText(coordinateStr + "\n"
                        + (addressStr == null ? "" : addressStr) + "\n"
                        + altitudeStr + "\n"
                        + speedStr + "\n"
                        + (magneticFieldStrengthStr == null ? getString(R.string.magnetic) + ": " : magneticFieldStrengthStr) + "\n"
                        + (pressureStr == null ? getString(R.string.air_pressure) + ": " : pressureStr));

                if (mv > 20 && mv < 60) {
                    findViewById(R.id.abnormal).setVisibility(View.INVISIBLE);
                } else {
                    findViewById(R.id.abnormal).setVisibility(View.VISIBLE);
                }
                tvMagneticDeclination.setText(getString(R.string.magnetic_declination, magneticDeclination));
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (permissionChecked && updated) {
            startLocationUpdates();
        }

        if (accelerationSensor != null) {
            sensorManager.registerListener(this, accelerationSensor, SensorManager.SENSOR_DELAY_GAME);
        }

        if (magneticFieldSensor != null) {
            sensorManager.registerListener(this, magneticFieldSensor, SensorManager.SENSOR_DELAY_GAME);
        }

        if (pressureSensor != null) {
            sensorManager.registerListener(this, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (permissionChecked && updated) {
            stopLocationUpdates();
        }
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handlerThread.quitSafely();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // 使用低通滤波器使传感器读数更平滑
            CompassHelper.lowPassFilter(event.values.clone(), accelerometerValues);
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            // 使用低通滤波器使传感器读数更平滑
            magneticFieldStrengthStr = getString(R.string.magnetic) + ": " + (int) Math.sqrt(
                    Math.pow(magneticValues[0], 2) +
                            Math.pow(magneticValues[1], 2) +
                            Math.pow(magneticValues[2], 2)) + " μT";
            CompassHelper.lowPassFilter(event.values.clone(), magneticValues);
        } else if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {
            pressureStr = getString(R.string.air_pressure) + ": "
                    + BigDecimal.valueOf(event.values[0]).setScale(2, BigDecimal.ROUND_HALF_UP) + " hPa";
        }
        update(location);
        updateHeading();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    private void updateHeading() {
        // 旧的heading值用于图像旋转动画
        float oldHeading = heading;

        heading = CompassHelper.calculateHeading(accelerometerValues, magneticValues);
        heading = CompassHelper.convertRadioDeg(heading);
        heading = CompassHelper.map180to360(heading);

        int angle = (int) heading;
        String direction;
        if (angle > 358 || angle < 2) {
            direction = getString(R.string.north);
        } else if (angle <= 88) {
            direction = getString(R.string.northeast) + " " + angle + "°";
        } else if (angle < 92) {
            direction = getString(R.string.east);
        } else if (angle <= 178) {
            direction = getString(R.string.southeast) + " " + (180 - angle) + "°";
        } else if (angle < 182) {
            direction = getString(R.string.south);
        } else if (angle <= 268) {
            direction = getString(R.string.southwest) + " " + (angle - 180) + "°";
        } else if (angle < 272) {
            direction = getString(R.string.west);
        } else {
            direction = getString(R.string.northwest) + " " + (360 - angle) + "°";
        }

        runOnUiThread(() -> {
            tvDegree.setText(direction);

            if (isLocationRetrieved) {
                float trueHeading = heading + magneticDeclination;
                if (trueHeading > 360) {
                    // 如果trueHeading为362度，例如，它应该调整为2度
                    trueHeading = trueHeading - 360;
                }
                tvTrueHeading.setText(getString(R.string.true_heading, (int) trueHeading));
                magneticDeclination = CompassHelper.calculateMagneticDeclination(latitude, longitude, altitude);
                tvMagneticDeclination.setText(getString(R.string.magnetic_declination, magneticDeclination));
            }

            RotateAnimation rotateAnimation
                    = new RotateAnimation(-oldHeading, -heading,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setDuration(500);
            rotateAnimation.setFillAfter(true);
            imageViewCompass.startAnimation(rotateAnimation);
        });
    }

    /**
     * 获取地址信息: 城市、街道等信息
     */
    private void getAddress(Location location) {
        if (location == null) {
            return;
        }

        EXECUTOR.execute(() -> {
            try {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    geocoder.getFromLocation(location.getLatitude(),
                            location.getLongitude(),
                            1,
                            list -> {
                                if (!list.isEmpty()) {
                                    Compass.this.addressStr = list.get(0).getAddressLine(0);
                                }
                            });
                } else {
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),
                            location.getLongitude(),
                            1);
                    if (addresses != null && !addresses.isEmpty()) {
                        Compass.this.addressStr = addresses.get(0).getAddressLine(0);
                    }
                }
            } catch (Exception e) {
                Log.e("Address", e.toString());
            }
        });
    }
}
