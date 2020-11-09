package com.hb712.gleak_android.base;

import android.app.Application;

public class BaseApplication extends Application {

    public static BaseApplication baseApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        baseApplication = this;
    }

}
