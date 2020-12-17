package com.hb712.gleak_android.message.net;

public class MonitorData {

    private String monitorTime;
    private double monitorValue;
    // 数据状态（正常：1，超标：0）
    private int monitorStatus;

    public MonitorData() {
    }

    public MonitorData(String monitorTime, double monitorValue, int monitorStatus) {
        this.monitorTime = monitorTime;
        this.monitorValue = monitorValue;
        this.monitorStatus = monitorStatus;
    }

    public String getMonitorTime() {
        return monitorTime;
    }

    public void setMonitorTime(String monitorTime) {
        this.monitorTime = monitorTime;
    }

    public double getMonitorValue() {
        return monitorValue;
    }

    public void setMonitorValue(double monitorValue) {
        this.monitorValue = monitorValue;
    }

    public int getMonitorStatus() {
        return monitorStatus;
    }

    public void setMonitorStatus(int monitorStatus) {
        this.monitorStatus = monitorStatus;
    }

    @Override
    public String toString() {
        return "{\"monitorTime\":\"" + monitorTime + "\", \"monitorValue\":" + monitorValue + ", \"monitorStatus\":" + monitorStatus + "}";
    }
}
