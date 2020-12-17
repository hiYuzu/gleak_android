package com.hb712.gleak_android.service;

import android.os.Bundle;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.hb712.gleak_android.MainApplication;
import com.hb712.gleak_android.base.BaseActivity;
import com.hb712.gleak_android.interfaceabs.OKHttpListener;
import com.hb712.gleak_android.message.net.MyPosition;
import com.hb712.gleak_android.util.GPSUtil;
import com.hb712.gleak_android.util.GlobalParam;
import com.hb712.gleak_android.util.HttpUtils;
import com.hb712.gleak_android.util.LogUtil;
import com.hb712.gleak_android.util.SPUtil;
import com.hb712.gleak_android.util.ThreadPoolUtil;

import java.util.Objects;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/12/3 15:35
 */
public class UploadPositionService {
    private static final String TAG = UploadPositionService.class.getSimpleName();
    private final LocationClient locationClient;
    private final MainApplication mainApp;
    private double lng = -1;
    private double lat = -1;
    MyPosition myPosition;
    private LatLng oldLocation = null;

    private Future<?> future;

    private UploadPositionService() {
        locationClient = new LocationClient(BaseActivity.baseActivity);
        mainApp = MainApplication.getInstance();
        myPosition = new MyPosition();
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
                lng = bdLocation.getLongitude();
                lat = bdLocation.getLatitude();
            }
        }
    };

    public void sendPosition() {
        future = ThreadPoolUtil.getInstance().scheduledCommonExecute(() -> {
            if (!(boolean) SPUtil.get(mainApp, GlobalParam.UPLOAD_POSITION_KEY, GlobalParam.IS_UPLOAD_POSITION)) {
                return;
            }
            if (lng == -1 && lat == -1) {
                return;
            }
            if (!isOverMinDistance(lng, lat)) {
                return;
            }
            myPosition.setUserId(mainApp.getUserId())
                    .setLongitude(lng)
                    .setLatitude(lat);
            try {
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
            } catch (NullPointerException npe) {
                LogUtil.errorOut(TAG, npe, null);
            }
        }, 0, (int) SPUtil.get(MainApplication.getInstance(), GlobalParam.UPLOAD_DELAY_KEY, GlobalParam.UPLOAD_DELAY), TimeUnit.SECONDS);
    }

    private boolean isOverMinDistance(double lng, double lat) {
        if (oldLocation == null) {
            oldLocation = new LatLng(lat, lng);
            return true;
        }
        double distance = DistanceUtil.getDistance(new LatLng(lat, lng), oldLocation);
        return distance > GlobalParam.MIN_DISTANCE;
    }

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
        sendPosition();
    }

    public void restartUploadPosition() {
        if (locationClient.isStarted()) {
            locationClient.unRegisterLocationListener(locationListener);
            locationClient.stop();
        }
        if (future != null) {
            future.cancel(true);
        }
        uploadPosition();
    }
}
