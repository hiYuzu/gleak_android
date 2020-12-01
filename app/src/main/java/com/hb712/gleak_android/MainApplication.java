package com.hb712.gleak_android;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.hb712.gleak_android.base.BaseApplication;
import com.hb712.gleak_android.util.GlobalParam;
import com.hb712.gleak_android.util.SPUtil;


/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/9/22 9:57
 */
public class MainApplication extends BaseApplication {

    private final String TAG = MainApplication.class.getSimpleName();
    public final String appIP = "192.168.3.125";
    public final String appPort = "3000";
    public final String baseUrl = "http://" + appIP + ":" + appPort;
    public final String SETTINGS_USERNAME = "username";
    public final String SETTINGS_PASSWORD = "password";

    private String username;
    private String password;
    private String userId = "";
    private String token = "";

    public static MainApplication getInstance() {
        return singleton;
    }

    private static MainApplication singleton;

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(this);
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);
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

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    public boolean isLogin() {
        return !"".equals(userId) && !"".equals(token);
    }
}
