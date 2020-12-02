package com.hb712.gleak_android.message.net;

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
    private Double longitude;
    private Double latitude;
    private String period;
    private String time;

    public InitLeakData(String id, String name, String code, Double longitude, Double latitude) {
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

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
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

    @Override
    public String toString() {
        return "InitLeakData{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", period='" + period + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
