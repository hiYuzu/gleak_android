package com.hb712.gleak_android.util;

import android.widget.Toast;

import com.hb712.gleak_android.base.BaseApplication;

public class ToastUtil {
    private static final Toast M_TOAST = Toast.makeText(BaseApplication.baseApplication, "", Toast.LENGTH_LONG);

    public static void toastWithoutLog(CharSequence cs) {
        M_TOAST.setText(cs);
        M_TOAST.show();
    }
}
