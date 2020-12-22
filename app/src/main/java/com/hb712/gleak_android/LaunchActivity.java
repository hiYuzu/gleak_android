package com.hb712.gleak_android;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.hb712.gleak_android.base.BaseActivity;
import com.hb712.gleak_android.interfaceabs.HttpInterface;
import com.hb712.gleak_android.interfaceabs.OKHttpListener;
import com.hb712.gleak_android.util.GlobalParam;
import com.hb712.gleak_android.util.HttpUtils;
import com.hb712.gleak_android.util.LogUtil;
import com.hb712.gleak_android.util.PermissionsUtil;
import com.hb712.gleak_android.util.ToastUtil;

import java.util.Objects;

public class LaunchActivity extends BaseActivity implements HttpInterface {

    private static final String TAG = LaunchActivity.class.getSimpleName();
    private static final int LauncherDelay = 2000;
    private TextView mMessage;

    private final Handler mLoginHandle = new Handler();
    private final Runnable mLoginAction = () -> {

        String username = MainApplication.getInstance().getLocUsername();
        String password = MainApplication.getInstance().getLocPassword();

        if (username.isEmpty() || password.isEmpty()) {
            gotoLoginActivity();
            return;
        }
        GlobalParam.rememberPwd = true;
        login(username, password);

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        View mContentView = findViewById(R.id.fullscreen_content);
        PermissionsUtil.requestPermission(this, this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        mMessage = findViewById(R.id.launch_message);
        TextView mVersion = findViewById(R.id.version_text);
        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
            String versionStr = info.versionName;
            if (!versionStr.isEmpty()) {
                mVersion.setText(String.format("Version: %s", versionStr));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        doLoginAction();
    }

    private void doLoginAction() {
        mLoginHandle.removeCallbacks(mLoginAction);
        mLoginHandle.postDelayed(mLoginAction, LauncherDelay);
    }

    private void gotoLoginActivity() {
        Intent intent = new Intent(LaunchActivity.this, LoginActivity.class);
        startActivity(intent);
        LaunchActivity.this.finish();
    }

    private void login(String username, String password) {
        try {
            HttpUtils.get(this, MainApplication.getInstance().baseUrl + "/api/login?name=" + username + "&password=" + password, new OKHttpListener() {
                @Override
                public void onStart() {
                    mMessage.setText("正在登录...");
                }

                @Override
                public void onSuccess(Bundle bundle) {
                    String result = bundle.getString(HttpUtils.MESSAGE);
                    JSONObject json = JSONObject.parseObject(result);
                    if (json.getBoolean("status")) {
                        LogUtil.debugOut(TAG, "用户" + json.getJSONObject("data").getString("userId") + "登录成功!");

                        MainApplication.getInstance().setUserId(json.getJSONObject("data").getString("userId"));
                        MainApplication.getInstance().setToken(json.getJSONObject("data").getString("token"));

                        gotoMainActivity(username, password);
                    } else {
                        LogUtil.debugOut(TAG, "自动登录失败:" + bundle.getString(HttpUtils.MESSAGE));
                        gotoLoginActivity();
                    }
                }

                @Override
                public void onServiceError(Bundle bundle) {
                    ToastUtil.toastWithLog(Objects.requireNonNull(bundle.getString(HttpUtils.MESSAGE)));
                    gotoLoginActivity();
                }

                @Override
                public void onNetworkError(Bundle bundle) {
                    ToastUtil.toastWithLog(Objects.requireNonNull(bundle.getString(HttpUtils.MESSAGE)));
                    gotoLoginActivity();
                }
            });
        } catch (NullPointerException npe) {
            LogUtil.errorOut(TAG, npe, null);
        }
    }

    private void gotoMainActivity(String username, String password) {
        Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_USERNAME, username);
        intent.putExtra(MainActivity.EXTRA_PASSWORD, password);
        startActivity(intent);
        LaunchActivity.this.finish();
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public boolean isDiscardHttp() {
        return isFinishing();
    }
}
