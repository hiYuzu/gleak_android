package com.hb712.gleak_android;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.hb712.gleak_android.base.BaseActivity;
import com.hb712.gleak_android.dao.DBManager;
import com.hb712.gleak_android.dialog.CommonDialog;
import com.hb712.gleak_android.interfaceabs.HttpInterface;
import com.hb712.gleak_android.interfaceabs.OKHttpListener;
import com.hb712.gleak_android.message.net.InitLeakData;
import com.hb712.gleak_android.service.UploadPositionService;
import com.hb712.gleak_android.util.GPSUtil;
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
        LogUtil.initDirectory();
        DBManager.getInstance().init(this);
        GPSUtil.requestLocationPower(this);
        getAllMonitor();
        UploadPositionService.getInstance().uploadPosition();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResult) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResult);
        if (requestCode == GlobalParam.REQUEST_LOCATION_PERMISSION) {
            for (int grant : grantResult) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    CommonDialog.getDialog(this, "", "地图需要开启定位功能，请到 “应用信息 -> 权限” 中授予！", () -> {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        startActivity(intent);
                    }).show();
                }
            }
        }
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
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
