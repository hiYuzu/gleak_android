package com.hb712.gleak_android.message.net;

import android.annotation.SuppressLint;

import com.hb712.gleak_android.base.BaseMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * 新增漏点
 */
public class NewLeak extends BaseMessage {

    private String name;
    private String code;
    private double longitude;
    private double latitude;
    // 巡检周期(天)
    private int period;

    public NewLeak() {
    }

    public NewLeak(String name, String code, double longitude, double latitude, int period) {
        this.name = name;
        this.code = code;
        this.longitude = longitude;
        this.latitude = latitude;
        this.period = period;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public String toString() {
        return "{name:" + name + ", code:" + code + ", longitude:" + String.format("%.6f", longitude) + ", latitude:" + String.format("%.6f", latitude) + ", period:" + period + "}";
    }

    @SuppressLint("DefaultLocale")
    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>(7);
        map.put("name", name);
        map.put("code", code);
        map.put("longitude", String.format("%.6f", longitude));
        map.put("latitude", String.format("%.6f", latitude));
        map.put("period", String.valueOf(period));
        return map;
    }
}
