package com.hb712.gleak_android.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/10/23 13:47
 */
@Entity
public class DetectInfo {
    @Id
    private Long id;
    private String leakName;
    private double monitorValue;
    private String monitorTime;
    private boolean standard;
    private String videoPath;
    private Long optUser;

    @Generated
    public DetectInfo() {
    }

    @Generated
    public DetectInfo(Long id, String leakName, double monitorValue, String monitorTime, boolean standard, String videoPath, Long optUser) {
        this.id = id;
        this.leakName = leakName;
        this.monitorValue = monitorValue;
        this.monitorTime = monitorTime;
        this.standard = standard;
        this.videoPath = videoPath;
        this.optUser = optUser;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLeakName() {
        return leakName;
    }

    public void setLeakName(String leakName) {
        this.leakName = leakName;
    }

    public double getMonitorValue() {
        return monitorValue;
    }

    public void setMonitorValue(double monitorValue) {
        this.monitorValue = monitorValue;
    }

    public String getMonitorTime() {
        return monitorTime;
    }

    public void setMonitorTime(String monitorTime) {
        this.monitorTime = monitorTime;
    }

    public boolean isStandard() {
        return standard;
    }

    public void setStandard(boolean standard) {
        this.standard = standard;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public Long getOptUser() {
        return optUser;
    }

    public void setOptUser(Long optUser) {
        this.optUser = optUser;
    }
}
