package com.hb712.gleak_android.message.net;

public class LeakData {

    private String leakId;
    private String userId;
    private MonitorData monitorData;

    public LeakData() {
    }

    public LeakData(String leakId, String userId, MonitorData monitorData) {
        this.leakId = leakId;
        this.userId = userId;
        this.monitorData = monitorData;
    }

    public String getLeakId() {
        return leakId;
    }

    public void setLeakId(String leakId) {
        this.leakId = leakId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public MonitorData getMonitorData() {
        return monitorData;
    }

    public void setMonitorData(MonitorData monitorData) {
        this.monitorData = monitorData;
    }

    @Override
    public String toString() {
        return "leakId:" + leakId + "userId:" + userId + "monitorData:{" + monitorData.toString() + "}";
    }
}
