package com.hb712.gleak_android.message.net;

public class Data {

    private String leakId;
    private String userId;
    private MonitorData monitorData;
    private FileData fileData;

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

    public FileData getFileData() {
        return fileData;
    }

    public void setFileData(FileData fileData) {
        this.fileData = fileData;
    }
}
