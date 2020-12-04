package com.hb712.gleak_android.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.hb712.gleak_android.base.BaseActivity;

public class GPSUtil {
    private static final String TAG = GPSUtil.class.getSimpleName();
    //权限数组（申请定位）
    private static final String[] PERMISSIONS = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS};

    /**
     * GPS权限请求
     */
    public static void requestLocationPower(Activity context) {
        try {
            if (context == null) {
                context = BaseActivity.baseActivity;
            }
            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (lm == null) {
                throw new Exception("手机无定位功能");
            }
            // 判断手机的GPS是否开启
            if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                //判断是否为android6.0系统版本，如果是，需要动态添加权限
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    boolean isGranted = true;
                    for (String permission : PERMISSIONS) {
                        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                            isGranted = false;
                            break;
                        }
                    }
                    if (!isGranted) {
                        ActivityCompat.requestPermissions(context, PERMISSIONS, GlobalParam.REQUEST_LOCATION_PERMISSION);
                    }
                }
            } else {
                ToastUtil.shortInstanceToast("未开启GPS定位服务");
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                context.startActivity(intent);
            }
        } catch (Exception e) {
            LogUtil.errorOut(TAG, e, "");
        }
    }

    public static void getBDLocation(LocationClient mLocationClient, BDAbstractLocationListener listener) {
        if (mLocationClient == null) {
            mLocationClient = new LocationClient(BaseActivity.baseActivity);
        }
        //通过LocationClientOption设置LocationClient相关参数
        LocationClientOption option = new LocationClientOption();
        // 打开gps
        option.setOpenGps(true);
        // 设置坐标类型
        option.setCoorType("bd09ll");
        option.setScanSpan(5000);
        //设置locationClientOption
        mLocationClient.setLocOption(option);
        //注册LocationListener监听器
        mLocationClient.registerLocationListener(listener);
        //开启地图定位
        mLocationClient.start();
    }
}
