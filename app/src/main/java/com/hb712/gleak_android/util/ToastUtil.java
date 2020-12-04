package com.hb712.gleak_android.util;

import android.widget.Toast;

import com.hb712.gleak_android.base.BaseApplication;

public class ToastUtil {
    private static final String TAG = ToastUtil.class.getSimpleName();
    private static final Toast mToast = Toast.makeText(BaseApplication.baseApplication, "", Toast.LENGTH_SHORT);

    public static void shortInstanceToast(CharSequence cs) {
        LogUtil.debugOut(TAG, cs.toString());
        mToast.setText(cs);
        mToast.show();
    }

    public static void longInstanceToast(CharSequence cs) {
        LogUtil.debugOut(TAG, cs.toString());
        mToast.setText(cs);
        mToast.show();
    }
}
