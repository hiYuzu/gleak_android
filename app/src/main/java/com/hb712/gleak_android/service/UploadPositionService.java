package com.hb712.gleak_android.service;

import android.os.Bundle;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.hb712.gleak_android.MainApplication;
import com.hb712.gleak_android.base.BaseActivity;
import com.hb712.gleak_android.interfaceabs.OKHttpListener;
import com.hb712.gleak_android.message.net.MyPosition;
import com.hb712.gleak_android.util.GPSUtil;
import com.hb712.gleak_android.util.GlobalParam;
import com.hb712.gleak_android.util.HttpUtils;
import com.hb712.gleak_android.util.LogUtil;
import com.hb712.gleak_android.util.SPUtil;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/12/3 15:35
 */
public class UploadPositionService {
    private static final String TAG = UploadPositionService.class.getSimpleName();
    private final LocationClient locationClient;
    private final MainApplication mainApp;

    private UploadPositionService() {
        locationClient = new LocationClient(BaseActivity.baseActivity);
        mainApp = MainApplication.getInstance();
    }

    private static class SingletonHolder {
        private static final UploadPositionService INSTANCE = new UploadPositionService();
    }

    public static UploadPositionService getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private final BDAbstractLocationListener locationListener = new BDAbstractLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation != null) {
                MyPosition myPosition = new MyPosition(mainApp.getUserId(), bdLocation.getLongitude(), bdLocation.getLatitude());
                HttpUtils.post(null, mainApp.baseUrl + "/api/location/insert", myPosition.toMap(), new OKHttpListener() {
                    @Override
                    public void onStart() {
                        LogUtil.debugOut(TAG, "上传定位...");
                    }

                    @Override
                    public void onSuccess(Bundle bundle) {
                        LogUtil.debugOut(TAG, "定位上传成功");
                    }

                    @Override
                    public void onServiceError(Bundle bundle) {
                        LogUtil.debugOut(TAG, Objects.requireNonNull(bundle.getString(HttpUtils.MESSAGE)));
                    }

                    @Override
                    public void onNetworkError(Bundle bundle) {
                        LogUtil.debugOut(TAG, Objects.requireNonNull(bundle.getString(HttpUtils.MESSAGE)));
                    }
                });
            }
        }
    };

    public void uploadPosition() {
        if (!mainApp.isLogin()) {
            return;
        }
        boolean isUpload = (boolean) SPUtil.get(mainApp, GlobalParam.UPLOAD_POSITION_KEY, GlobalParam.IS_UPLOAD_POSITION);
        if (!isUpload) {
            if (locationClient.isStarted()) {
                locationClient.unRegisterLocationListener(locationListener);
                locationClient.stop();
            }
            return;
        }
        GPSUtil.getBDLocation(locationClient, locationListener);
    }

    public void restartUploadPosition() {
        if (locationClient.isStarted()) {
            locationClient.unRegisterLocationListener(locationListener);
            locationClient.stop();
        }
        uploadPosition();
    }
}
