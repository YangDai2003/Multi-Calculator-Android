package com.example.calc.compass;

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

import java.util.List;
import java.util.Locale;

/**
 * 获取经纬度、位置工具类
 *
 * @author 30415
 */
public class LocationUtils {

    @SuppressLint("StaticFieldLeak")
    private volatile static LocationUtils uniqueInstance;
    private Location location;
    private LocationManager locationManager;
    private final Context mContext;

    private LocationUtils(Context context, LocationListener locationListener) {
        mContext = context.getApplicationContext();
        getLocation(locationListener);
    }

    /**
     * 采用Double CheckLock(DCL)实现单例
     */
    public static LocationUtils getInstance(Context context, LocationListener locationListener) {
        if (null == uniqueInstance) {
            synchronized (LocationUtils.class) {
                if (uniqueInstance == null) {
                    uniqueInstance = new LocationUtils(context, locationListener);
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
        //1.获取位置管理器
        if (null == locationManager) {
            locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        }
        //2.获取位置提供器，GPS或是NetWork
        List<String> providers = locationManager.getProviders(true);
        String locationProvider;
        if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            //如果是网络定位
            Log.d("TAG", "如果是网络定位");
            locationProvider = LocationManager.NETWORK_PROVIDER;
        } else if (providers.contains(LocationManager.GPS_PROVIDER)) {
            //如果是GPS定位
            Log.d("TAG", "如果是GPS定位");
            locationProvider = LocationManager.GPS_PROVIDER;
        } else {
            Log.d("TAG", "没有可用的位置提供器");
            return;
        }

        // 需要检查权限,否则编译报错,想抽取成方法都不行,还是会报错。只能这样重复 code 了。
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        //3.获取上次的位置，一般第一次运行，此值为 null
        location = locationManager.getLastKnownLocation(locationProvider);
        if (location != null) {
            setLocation(location);
        }
        // 监视地理位置变化，第二个和第三个参数分别为更新的最短时间 minTime和最短距离 minDistance
        locationManager.requestLocationUpdates(locationProvider, 500, 10, locationListener);
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
    public static String getAddress(Context context, Location location) {
        List<Address> result;
        String address = null;
        try {
            if (location != null) {
                Geocoder gc = new Geocoder(context, Locale.getDefault());
                result = gc.getFromLocation(location.getLatitude(),
                        location.getLongitude(), 1);
                address = result.isEmpty() ? "" : result.get(0).getAddressLine(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return address;
    }
}
