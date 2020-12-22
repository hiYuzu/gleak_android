package com.hb712.gleak_android.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/12/14 11:23
 */
public class PermissionsUtil {
    /**
     * 请求权限(multi)
     * @param context used in {@link ActivityCompat#checkSelfPermission}
     * @param activity used in {@link ActivityCompat#requestPermissions}
     * @param permissions {@link String} Array, type is {@link Manifest.permission}
     */
    public static void requestPermissions(Context context, Activity activity, String[] permissions) {
        for (int i = 0; i < permissions.length; i++) {
            if (ActivityCompat.checkSelfPermission(context, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, permissions, i);
            }
        }
    }

    /**
     * 请求权限(single)
     * @param context used in {@link ActivityCompat#checkSelfPermission}
     * @param activity used in {@link ActivityCompat#requestPermissions}
     * @param permission {@link Manifest.permission}
     */
    public static void requestPermission(Context context, Activity activity, String permission) {
        String[] permissions = new String[] {permission};
        requestPermissions(context, activity, permissions);
    }
}
