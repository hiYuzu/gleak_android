package com.hb712.gleak_android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.os.Process;

import com.alibaba.fastjson.JSONObject;
import com.hb712.gleak_android.base.BaseActivity;
import com.hb712.gleak_android.dialog.CommonDialog;
import com.hb712.gleak_android.interfaceabs.HttpInterface;
import com.hb712.gleak_android.interfaceabs.OKHttpListener;
import com.hb712.gleak_android.message.net.InitLeakData;
import com.hb712.gleak_android.util.GlobalParam;
import com.hb712.gleak_android.util.HttpUtils;
import com.hb712.gleak_android.util.LogUtil;
import com.hb712.gleak_android.util.ToastUtil;

import java.util.List;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/9/22 10:20
 */
public class MainActivity extends BaseActivity implements HttpInterface {
    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String EXTRA_USERNAME = "username";
    public static final String EXTRA_PASSWORD = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getAllMonitor();
    }

    private void getAllMonitor() {
        if (!MainApplication.getInstance().isLogin()) {
            return;
        }
        HttpUtils.get(this, MainApplication.getInstance().baseUrl + "/api/monitor/selectAllMonitor ", new OKHttpListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(Bundle bundle) {
                String result = bundle.getString(HttpUtils.MESSAGE);
                JSONObject json = JSONObject.parseObject(result);
                if (json.getBoolean("status")) {
                    GlobalParam.initLeakData = (List<InitLeakData>) JSONObject.parseArray(json.getString("data"), InitLeakData.class);
                } else {
                    LogUtil.infoOut(TAG, json.getString("msg"));
                }
            }

            @Override
            public void onServiceError(Bundle bundle) {
                ToastUtil.shortInstanceToast(bundle.getString(HttpUtils.MESSAGE));
            }

            @Override
            public void onNetworkError(Bundle bundle) {
                ToastUtil.shortInstanceToast(bundle.getString(HttpUtils.MESSAGE));
            }
        });
    }

    public void detectClick(View view) {
        Intent intent = new Intent(MainActivity.this, DetectActivity.class);
        startActivity(intent);
    }

    public void calibrateClick(View view) {
        Intent intent = new Intent(MainActivity.this, CalibrateActivity.class);
        startActivity(intent);
    }

    public void historyClick(View view) {
        Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
        startActivity(intent);
    }

    public void settingsClick(View view) {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    public void onBackPressed() {
        new CommonDialog(this, "提示", "确定退出？", () -> Process.killProcess(Process.myPid())).show();
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
