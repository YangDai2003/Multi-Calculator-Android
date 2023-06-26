package com.yangdai.calc.compass;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;
import android.Manifest;

import androidx.core.content.ContextCompat;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 获取经纬度、位置工具类
 *
 * @author 30415
 */
public class LocationUtils {

    @SuppressLint("StaticFieldLeak")
    private static volatile LocationUtils uniqueInstance;
    private Location location;
    private LocationManager locationManager;
    private final WeakReference<Context> mContext;
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    private LocationUtils(Context context) {
        mContext = new WeakReference<>(context.getApplicationContext());
    }

    /**
     * 采用静态内部类单例模式实现
     */
    public static LocationUtils getInstance(Context context, LocationListener locationListener) {
        if (null == uniqueInstance) {
            synchronized (LocationUtils.class) {
                if (uniqueInstance == null) {
                    uniqueInstance = new LocationUtils(context);
                }
            }
        }
        uniqueInstance.getLocation(locationListener);
        return uniqueInstance;
    }

    /**
     * 获取经纬度,location
     */
    private void getLocation(LocationListener locationListener) {
        Context context = mContext.get();
        if (context == null) {
            return;
        }
        // 需要检查权限,否则编译报错,想抽取成方法都不行,还是会报错。只能这样重复 code 了。
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //1.获取位置管理器
        if (null == locationManager) {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }
        //2.获取位置提供器，GPS或是NetWork
        List<String> providers = locationManager.getProviders(true);
        if (providers.isEmpty()) {
            return;
        }
        String locationProvider;
        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            // 如果 GPS 提供器可用，优先选择使用 GPS
            locationProvider = LocationManager.GPS_PROVIDER;
        } else {
            // 如果 GPS 提供器不可用，但还有其他位置提供器可用，则选择第一个位置提供器
            locationProvider = providers.get(0);
        }

        if (locationProvider == null) {
            Log.d("TAG", "没有可用的位置提供器");
            return;
        }

        //3.获取上次的位置，一般第一次运行，此值为 null
        location = locationManager.getLastKnownLocation(locationProvider);
        if (location != null) {
            setLocation(location);
        }

        // 监视地理位置变化，第二个和第三个参数分别为更新的最短时间 minTime和最短距离 minDistance
        locationManager.requestLocationUpdates(locationProvider, 1000, 1, locationListener);
    }

    private void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    /**
     * 获取地址信息: 城市、街道等信息
     */
    public static void getAddress(Context context, Location location
            , OnAddressFetchedListener listener) {
        if (location == null || listener == null) {
            return;
        }
        EXECUTOR.execute(() -> {
            String address = "";
            try {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (!addresses.isEmpty()) {
                    address = addresses.get(0).getAddressLine(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            listener.onAddressFetched(address);
        });
    }

    /**
     * 地址获取回调接口
     */
    public interface OnAddressFetchedListener {
        void onAddressFetched(String address);
    }
}
