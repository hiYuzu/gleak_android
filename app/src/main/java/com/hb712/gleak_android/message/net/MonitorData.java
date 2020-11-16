package com.hb712.gleak_android.message.net;

public class MonitorData {

    private String monitorTime;
    private double monitorValue;
    private boolean monitorStatus;

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

    public boolean isMonitorStatus() {
        return monitorStatus;
    }

    public void setMonitorStatus(boolean monitorStatus) {
        this.monitorStatus = monitorStatus;
    }
}
