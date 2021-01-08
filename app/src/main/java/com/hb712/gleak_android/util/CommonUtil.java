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
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.hb712.gleak_android.MainApplication;
import com.hb712.gleak_android.dialog.CommonDialog;
import com.hb712.gleak_android.interfaceabs.HttpInterface;
import com.hb712.gleak_android.interfaceabs.OKHttpListener;

import java.util.Objects;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2021/1/8 8:48
 */
public class CommonUtil implements HttpInterface {
    private final String TAG = CommonUtil.class.getSimpleName();
    private final Activity activity;
    private final DownloadManager downloadManager;
    private long downloadReferenceId;

    public CommonUtil(@NonNull Activity activity) throws NullPointerException {
        this.activity = activity;
        downloadManager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
        if (downloadManager == null) {
            throw new NullPointerException("无法获取 DownloadManager");
        }
        BroadcastReceiver updateCompleteReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (referenceId != downloadReferenceId) {
                    return;
                }
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(referenceId);
                Cursor c = downloadManager.query(query);
                if (c.moveToFirst()) {
                    int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                        String fileUriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                        if (fileUriString != null) {
                            LogUtil.debugOut(TAG, "Update Download App Complete: " + fileUriString);
                            Intent installIntent = new Intent(Intent.ACTION_VIEW).setDataAndType(Uri.parse(fileUriString), "application/vnd.android.package-archive");
                            installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            activity.startActivity(installIntent);
                        }
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        activity.registerReceiver(updateCompleteReceiver, filter);
    }

    public void checkUpdate() {
        String url = MainApplication.getInstance().baseUrl + "/" + activity.getPackageName();
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
            Uri uri = Uri.parse(url);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION);
            downloadReferenceId = downloadManager.enqueue(request);
        });
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
