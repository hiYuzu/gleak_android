package com.hb712.gleak_android;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import com.hb712.gleak_android.service.WebServiceClient;


/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/9/22 9:57
 */
public class MainApplication extends Application {
    private static final String TAG = MainApplication.class.getSimpleName();
    public static final String appIP = "127.0.0.1";
    public static final String appPort = "";
    public static final String appAddress = appIP + (appPort.isEmpty()? "" : ":" + appPort);
    public static final String baseUrl = "http://" + appAddress;


    private static final String SETTINGS_USERNAME = "staff_id";
    private static final String SETTINGS_PASSWORD = "password";
    private WebServiceClient mWebServiceClient;

    public static MainApplication getInstance(){
        return singleton;
    }

    private static MainApplication singleton;

    SharedPreferences mSettings;

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
        mSettings = this.getSharedPreferences(MainApplication.appAddress, 0);
        mWebServiceClient = new WebServiceClient();
    }

    public void saveUsername(String username) {
        Log.d(TAG, "saveUsername: " + username);
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(MainApplication.SETTINGS_USERNAME, username);
        editor.apply();
    }

    public void savePassword(String password){
        Log.d(TAG, "savePassword: " + password);
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(MainApplication.SETTINGS_PASSWORD, password);
        editor.apply();
    }

    public void removePassword() {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.remove(MainApplication.SETTINGS_PASSWORD);
        editor.apply();
    }

    public String getUsername() {
        return mSettings.getString(SETTINGS_USERNAME, "");
    }

    public String getPassword() {
        return mSettings.getString(SETTINGS_PASSWORD, "");
    }

    public WebServiceClient getWebServiceClient() {
        return mWebServiceClient;
    }
}
