package com.hb712.gleak_android.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;

public class GPSUtil {
    private static final String TAG = GPSUtil.class.getSimpleName();
    //权限数组（申请定位）
    private static final String[] PERMISSIONS = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS};

    private static LocationManager lm;

    /**
     * 通过 {@link GPSUtil#getLocation()} 获取GPS坐标，再转换为百度地图坐标
     *
     * @return 百度地图坐标
     * @see LatLng
     */
    public static LatLng getBDLocation(Activity mContext) {
        requestLocationPower(mContext);
        Location location = getLocation();
        if (location == null) {
            ToastUtil.shortInstanceToast("无法获取定位");
            return null;
        }
        CoordinateConverter cc = new CoordinateConverter();
        cc.from(CoordinateConverter.CoordType.GPS);
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        cc.coord(latLng);
        return cc.convert();
    }

    /**
     * 获取地理位置，先根据GPS获取，再根据网络获取
     *
     * @return GPS定位
     * @see Location
     */
    @SuppressLint("MissingPermission")
    private static Location getLocation() {
        Location location = null;
        try {
            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location == null) {
                location = getLocationByNetwork();
            }
        } catch (Exception e) {
            LogUtil.errorOut(TAG, e, "");
        }
        return location;
    }

    /**
     * 获取地理位置，先根据GPS获取，再根据网络获取
     *
     * @return android.location.Location
     * @see Location
     */
    @SuppressLint("MissingPermission")
    private static Location getLocationByNetwork() {
        try {
            return lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        } catch (SecurityException se) {
            String message = "权限不足";
            ToastUtil.shortInstanceToast(message);
            LogUtil.errorOut(TAG, se, message);
        } catch (IllegalArgumentException iae) {
            String message = "provider 不存在或未实例化";
            ToastUtil.shortInstanceToast(message);
            LogUtil.errorOut(TAG, iae, message);
        }
        return null;
    }

    /**
     * GPS权限请求
     *
     * @param context 调用此方法的Activity
     */
    public static void requestLocationPower(Activity context) {
        try {
            lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
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

}
