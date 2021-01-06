package com.hb712.gleak_android.util;

import android.widget.Toast;

import com.hb712.gleak_android.base.BaseApplication;

/**
 *
 * @author hiYuzu
 * @version V1.0
 * @date 2020/11/19 14:58
 */
public class ToastUtil {
    private static final Toast M_LONG_TOAST = Toast.makeText(BaseApplication.baseApplication, "", Toast.LENGTH_LONG);
    private static final Toast M_SHORT_TOAST = Toast.makeText(BaseApplication.baseApplication, "", Toast.LENGTH_SHORT);

    public static void longToastShow(CharSequence cs) {
        M_LONG_TOAST.setText(cs);
        M_LONG_TOAST.show();
    }

    public static void shortToastShow(CharSequence cs) {
        M_SHORT_TOAST.setText(cs);
        M_SHORT_TOAST.show();
    }
}
