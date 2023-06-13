package com.example.calc.compass;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.icu.math.BigDecimal;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.example.calc.R;
import com.google.android.material.elevation.SurfaceColors;

import java.util.Objects;

/**
 * @author 30415
 */
public class Compass extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;

    private final float[] accelerometerValues = new float[3];
    private final float[] magneticValues = new float[3];

    private float heading;
    private float longitude;
    private float latitude;
    private float altitude;
    private float magneticDeclination;

    private boolean isLocationRetrieved = false;

    private TextView textViewTrueHeading, textViewMagneticDeclination, degree, locationView;
    private ImageView imageViewCompass;
    private static final int PERMISSION_REQUEST_CODE = 1024;
    String magneticFieldStrength, local, address, altitudeStr, speed;
    Location location;
    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(SurfaceColors.SURFACE_0.getColor(this));
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(SurfaceColors.SURFACE_0.getColor(this)));
        Objects.requireNonNull(getSupportActionBar()).setElevation(0f);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.compass);
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        if (settings.getBoolean("screen", false)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        degree = findViewById(R.id.degree);
        locationView = findViewById(R.id.location);
        textViewTrueHeading = findViewById(R.id.text_view_true_heading);
        textViewMagneticDeclination = findViewById(R.id.text_view_magnetic_declination);
        imageViewCompass = findViewById(R.id.image_compass);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // 加速度感应器
        Sensor magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        // 地磁感应器
        Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            //请求权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
        } else {
            location = LocationUtils.getInstance(this, locationListener).getLocation();
            update(location);
        }

        // 默认值为N/A。如果已检索到位置，则文本将更新为相应的值
        textViewTrueHeading.setText(R.string.not_available);
        textViewMagneticDeclination.setText(R.string.not_available);
    }

    @SuppressLint("SetTextI18n")
    private void update(Location location) {
        if (location != null) {
            local = getString(R.string.latitude) + ": " + location.getLatitude()
                    + " " + getString(R.string.longitude) + ": " + location.getLongitude();
            address = LocationUtils.getAddress(this, location);
            altitudeStr = getString(R.string.altitude) + ": "
                    + BigDecimal.valueOf(location.getAltitude())
                    .setScale(settings.getInt("scale", 10), BigDecimal.ROUND_HALF_UP) + " m";
            speed = getString(R.string.speed) + ": "
                    + BigDecimal.valueOf(location.getSpeed())
                    .setScale(settings.getInt("scale", 10), BigDecimal.ROUND_HALF_UP) + " m/s";
            magneticFieldStrength = getString(R.string.magnetic) + ": " + (int) Math.sqrt(
                    Math.pow(magneticValues[0], 2) +
                            Math.pow(magneticValues[1], 2) +
                            Math.pow(magneticValues[2], 2)
            ) + " μT";
            locationView
                    .setText(local + "\n" + address + "\n" + altitudeStr + "\n" + speed + "\n" + magneticFieldStrength);

            isLocationRetrieved = true;
            latitude = (float) location.getLatitude();
            longitude = (float) location.getLongitude();
            altitude = (float) location.getAltitude();
            magneticDeclination = CompassHelper.calculateMagneticDeclination(latitude, longitude, altitude);
            textViewMagneticDeclination.setText(getString(R.string.magnetic_declination, magneticDeclination));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer,
                    SensorManager.SENSOR_DELAY_GAME, SensorManager.SENSOR_DELAY_GAME);
        }

        Sensor magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (magneticField != null) {
            sensorManager.registerListener(this, magneticField,
                    SensorManager.SENSOR_DELAY_GAME, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // 使用低通滤波器使传感器读数更平滑
            CompassHelper.lowPassFilter(event.values.clone(), accelerometerValues);
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            // 使用低通滤波器使传感器读数更平滑
            magneticFieldStrength = getString(R.string.magnetic) + ": " + (int) Math.sqrt(
                    Math.pow(magneticValues[0], 2) +
                            Math.pow(magneticValues[1], 2) +
                            Math.pow(magneticValues[2], 2)
            ) + " μT";
            locationView
                    .setText(local + "\n" + address + "\n" + altitudeStr + "\n" + speed + "\n" + magneticFieldStrength);
            CompassHelper.lowPassFilter(event.values.clone(), magneticValues);
        }
        updateHeading();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void updateHeading() {
        // 旧的heading值用于图像旋转动画
        float oldHeading = heading;

        heading = CompassHelper.calculateHeading(accelerometerValues, magneticValues);
        heading = CompassHelper.convertRadioDeg(heading);
        heading = CompassHelper.map180to360(heading);

        int angle = (int) heading;
        String direction;
        if (angle > 358 && angle < 2) {
            direction = getString(R.string.north);
        } else if (angle >= 2 && angle <= 88) {
            direction = getString(R.string.northeast) + " " + angle + "°";
        } else if (angle > 88 && angle < 92) {
            direction = getString(R.string.east);
        } else if (angle >= 92 && angle <= 178) {
            direction = getString(R.string.southeast) + " " + (180 - angle) + "°";
        } else if (angle > 178 && angle < 182) {
            direction = getString(R.string.south);
        } else if (angle >= 182 && angle <= 268) {
            direction = getString(R.string.southwest) + " " + (angle - 180) + "°";
        } else if (angle > 268 && angle < 272) {
            direction = getString(R.string.west);
        } else {
            direction = getString(R.string.northwest) + " " + (360 - angle) + "°";
        }
        degree.setText(direction);

        if (isLocationRetrieved) {
            float trueHeading = heading + magneticDeclination;
            if (trueHeading > 360) {
                // 如果trueHeading为362度，例如，它应该调整为2度
                trueHeading = trueHeading - 360;
            }
            textViewTrueHeading.setText(getString(R.string.true_heading, (int) trueHeading));
            magneticDeclination = CompassHelper.calculateMagneticDeclination(latitude, longitude, altitude);
            textViewMagneticDeclination.setText(getString(R.string.magnetic_declination, magneticDeclination));
        }

        RotateAnimation rotateAnimation
                = new RotateAnimation(-oldHeading, -heading,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(500);
        rotateAnimation.setFillAfter(true);
        imageViewCompass.startAnimation(rotateAnimation);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            // 检查权限授予结果
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 用户授予了权限，进行定位操作
                location = LocationUtils.getInstance(this, locationListener).getLocation();
                update(location);
            } else {
                // 用户拒绝了权限，可以根据需求进行相应的处理，例如显示一个提示信息或者禁用相关功能
                Toast.makeText(this, getString(R.string.permission), Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * 当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
     */
    public LocationListener locationListener = location -> {
        Compass.this.location = location;
        update(location);
    };
}
