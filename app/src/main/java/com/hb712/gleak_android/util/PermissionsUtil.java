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
    public static void requestWriteExternalStorage(Context context, Activity activity) {
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (ActivityCompat.checkSelfPermission(context, permissions[0]) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, permissions, 1234);
        }
    }
}
