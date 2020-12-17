package com.hb712.gleak_android.message.net;

import android.annotation.SuppressLint;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/12/3 17:04
 */
public class MyPosition {
    private String userId;
    private double longitude;
    private double latitude;

    public MyPosition() {
    }

    public MyPosition(String userId, double longitude, double latitude) {
        this.userId = userId;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getUserId() {
        return userId;
    }

    public MyPosition setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public double getLongitude() {
        return longitude;
    }

    public MyPosition setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    public double getLatitude() {
        return latitude;
    }

    public MyPosition setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public String toString() {
        return "{userId:" + userId + ", longitude:" + String.format("%.6f", longitude) + ", latitude:" + String.format("%.6f", latitude) + "}";
    }

    @SuppressLint("DefaultLocale")
    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>(5);
        map.put("userId", userId);
        map.put("longitude", String.format("%.6f", longitude));
        map.put("latitude", String.format("%.6f", latitude));
        return map;
    }
}
