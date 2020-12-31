package com.hb712.gleak_android.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.hb712.gleak_android.base.BaseActivity;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/12/14 11:23
 */
public class PermissionsUtil {
    private static final String TAG = PermissionsUtil.class.getSimpleName();

    /**
     * 请求定位权限
     * @param activity used in {@link ActivityCompat#requestPermissions}
     */
    public static void requestLocationPermission(Activity activity) {
        String[] locationPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS};
        try {
            if (activity == null) {
                activity = BaseActivity.baseActivity;
            }
            LocationManager lm = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
            if (lm == null) {
                throw new Exception("手机无定位功能");
            }
            // 判断手机的GPS是否开启
            if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                //判断是否为android6.0系统版本，如果是，需要动态添加权限
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    boolean isGranted = true;
                    for (String permission : locationPermissions) {
                        if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                            isGranted = false;
                            break;
                        }
                    }
                    if (!isGranted) {
                        ActivityCompat.requestPermissions(activity, locationPermissions, GlobalParam.REQUEST_LOCATION_PERMISSION);
                    }
                }
            } else {
                ToastUtil.toastWithoutLog("未开启GPS定位服务");
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                activity.startActivity(intent);
            }
        } catch (Exception e) {
            LogUtil.errorOut(TAG, e, null);
        }
    }

    /**
     * 请求读写权限
     * @param activity used in {@link ActivityCompat#requestPermissions}
     */
    public static void requestRWPermission(Activity activity) {
        String[] rwPermissions = new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        try {
            if (activity == null) {
                activity = BaseActivity.baseActivity;
            }
            //判断是否为android6.0系统版本，如果是，需要动态添加权限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                boolean isGranted = true;
                for (String permission : rwPermissions) {
                    if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                        isGranted = false;
                        break;
                    }
                }
                if (!isGranted) {
                    ActivityCompat.requestPermissions(activity, rwPermissions, GlobalParam.REQUEST_RW_PERMISSION);
                }
            }
        } catch (Exception e) {
            LogUtil.errorOut(TAG, e, null);
        }
    }
}
