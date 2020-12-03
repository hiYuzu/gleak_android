package com.hb712.gleak_android.message.net;

import android.annotation.SuppressLint;

import com.hb712.gleak_android.base.BaseBean;

/**
 * 登录时从后台获取所有漏点数据
 *
 * @author hiYuzu
 * @version V1.0
 * @date 2020/12/1 8:52
 */
public class InitLeakData extends BaseBean {
    private String id;
    private String name;
    private String code;
    private double longitude;
    private double latitude;
    private String period;
    private String time;

    public InitLeakData() {
    }

    public InitLeakData(String id, String name, String code, double longitude, double latitude) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public String toString() {
        return "InitLeakData{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", longitude='" + String.format("%.6f", longitude) + '\'' +
                ", latitude='" + String.format("%.6f", latitude) + '\'' +
                ", period='" + period + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
