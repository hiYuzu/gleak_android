package com.hb712.gleak_android.util;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.alibaba.fastjson.JSONObject;
import com.hb712.gleak_android.MainApplication;
import com.hb712.gleak_android.dialog.CommonDialog;
import com.hb712.gleak_android.interfaceabs.HttpInterface;
import com.hb712.gleak_android.interfaceabs.OKHttpListener;

import java.io.File;
import java.util.Objects;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2021/1/8 8:48
 */
public class CommonUtil implements HttpInterface {
    private final String TAG = CommonUtil.class.getSimpleName();
    private final Activity activity;

    public CommonUtil(@NonNull Activity activity) throws NullPointerException {
        this.activity = activity;
    }

    public void checkUpdate() {
        PermissionsUtil.requestRWPermission(activity);
        String url = MainApplication.getInstance().baseUrl + "/api/app/selectAppByAppName?code=" + activity.getPackageName();
        HttpUtils.get(this, url, new OKHttpListener() {
            @Override
            public void onStart() {
                GlobalParam.updateTime = DateUtil.getDefaultTime();
            }

            @Override
            public void onSuccess(Bundle bundle) {
                PackageInfo info;
                try {
                    info = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
                    String versionName = info.versionName;
                    String result = bundle.getString(HttpUtils.MESSAGE);
                    JSONObject json = JSONObject.parseObject(result);
                    if (json.getBoolean("status")) {
                        JSONObject jsonObject = json.getJSONObject("data");
                        if (versionName.equals(jsonObject.getString("version"))) {
                            ToastUtil.longToastShow("当前已是最新版本");
                        } else {
                            downloadApk(jsonObject.getString("url"));
                        }
                    } else {
                        ToastUtil.longToastShow(json.getString("msg"));
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    ToastUtil.longToastShow("未找到包名，请检查app是否正常安装");
                    LogUtil.errorOut(TAG, e, null);
                }
            }

            @Override
            public void onServiceError(Bundle bundle) {
                ToastUtil.longToastShow(Objects.requireNonNull(bundle.getString(HttpUtils.MESSAGE)));
            }

            @Override
            public void onNetworkError(Bundle bundle) {
                ToastUtil.longToastShow(Objects.requireNonNull(bundle.getString(HttpUtils.MESSAGE)));
            }
        });
    }

    private void downloadApk(String url) {
        CommonDialog.getDialog(activity, "更新", "检测到新的版本，是否更新？", null, "更新", "暂不更新", () -> {
            Intent updateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            updateIntent.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
            activity.startActivity(updateIntent);
        }).show();
    }

    @Override
    public Activity getActivity() {
        return activity;
    }

    @Override
    public boolean isDiscardHttp() {
        return activity.isFinishing();
    }
}
