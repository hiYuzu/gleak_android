package com.hb712.gleak_android.util;

import android.widget.Toast;

import com.hb712.gleak_android.base.BaseApplication;

public class CommonUtils {

    //null判断主要是view视图看不到，此处根本不可能为null
    private static Toast mToast = BaseApplication.baseApplication == null ? null : Toast.makeText(BaseApplication.baseApplication, "", Toast.LENGTH_SHORT);

    public static void toast(CharSequence cs) {
        Toast.makeText(BaseApplication.baseApplication, cs, Toast.LENGTH_SHORT).show();
    }

    /**
     * 单例的吐司,只会展示一个
     */
    public static void toastInstance(CharSequence cs) {
        mToast.setText(cs);
        mToast.show();
    }
}
