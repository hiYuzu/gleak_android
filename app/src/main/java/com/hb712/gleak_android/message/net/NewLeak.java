package com.hb712.gleak_android.message.net;

import com.hb712.gleak_android.base.BaseMessage;

/**
 * 新增漏点
 */
public class NewLeak extends BaseMessage {

    private String name;
    private String code;
    private double longitude;
    private double latitude;
    // 巡检周期(天)
    private String period;

    public NewLeak() {
    }

    public NewLeak(String name, String code, double longitude, double latitude, String period) {
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

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    @Override
    public String toString() {
        return "{name:" + name + ", code:" + code + ", longitude:" + longitude + ", latitude:" + latitude + ", period:" + period + "}";
    }
}
