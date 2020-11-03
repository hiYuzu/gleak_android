package com.hb712.gleak_android;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import com.hb712.gleak_android.service.WebServiceClient;
import com.hb712.gleak_android.util.GlobalParam;
import com.hb712.gleak_android.util.LogUtil;
import com.hb712.gleak_android.util.SPUtil;


/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/9/22 9:57
 */
public class MainApplication extends Application {

    private final String TAG = MainApplication.class.getSimpleName();
    public String appIP = "127.0.0.1";
    public String appPort = "";
    public String baseUrl = "http://" + appIP + (appPort.isEmpty() ? "" : ":" + appPort);
    public final String SETTINGS_USERNAME = "username";
    public final String SETTINGS_PASSWORD = "password";

    private String username;
    private String password;

    private WebServiceClient mWebServiceClient;

    public static MainApplication getInstance() {
        return singleton;
    }

    private static MainApplication singleton;

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
        mWebServiceClient = new WebServiceClient();
    }

    public void saveUserPwd(String username, String password, boolean isSave) {
        this.username = username;
        this.password = password;
        if (isSave) {
            GlobalParam.rememberPwd = true;
            SPUtil.put(this, SETTINGS_USERNAME, username);
            SPUtil.put(this, SETTINGS_PASSWORD, password);
        }
    }

    public void saveUserPwd() {
        GlobalParam.rememberPwd = true;
        SPUtil.put(this, SETTINGS_USERNAME, username);
        SPUtil.put(this, SETTINGS_PASSWORD, password);
    }

    public void removeUserPwd() {
        GlobalParam.rememberPwd = false;
        SPUtil.remove(this, SETTINGS_USERNAME);
        SPUtil.remove(this, SETTINGS_PASSWORD);
    }

    public String getLocUsername() {
        return SPUtil.get(this, SETTINGS_USERNAME, "").toString();
    }

    public String getLocPassword() {
        return SPUtil.get(this, SETTINGS_PASSWORD, "").toString();
    }

    public WebServiceClient getWebServiceClient() {
        return mWebServiceClient;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
