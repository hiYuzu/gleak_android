package com.hb712.gleak_android.pojo;

import android.support.annotation.NonNull;

import com.hb712.gleak_android.dao.CalibrationInfoDao;
import com.hb712.gleak_android.dao.DaoSession;
import com.hb712.gleak_android.util.LogUtil;

import org.greenrobot.greendao.DaoException;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/10/22 8:30
 */
public class CalibrationInfo implements Comparable<CalibrationInfo> {
    private static final String TAG = CalibrationInfo.class.getSimpleName();
    private Long id;
    private String calibrateTime;
    private String deviceName;
    private double bValue;
    private double kValue;
    private double signalValue;
    private double standardValue;
    private SeriesInfo seriesInfo;
    private transient Long seriesInfoResolvedKey;
    private long seriesId;
    private transient DaoSession daoSession;
    private transient CalibrationInfoDao calibrationInfoDao;

    public CalibrationInfo() {
    }

    public CalibrationInfo(Long id, String deviceName, long seriesId, String calibrateTime, double signalValue, double standardValue, double kValue, double bValue) {
        this.id = id;
        this.deviceName = deviceName;
        this.seriesId = seriesId;
        this.calibrateTime = calibrateTime;
        this.signalValue = signalValue;
        this.standardValue = standardValue;
        this.kValue = kValue;
        this.bValue = bValue;
    }

    public int compareTo(@NonNull CalibrationInfo calibrationInfo) {
        return Double.compare(signalValue, calibrationInfo.signalValue);
    }

    public void setValueFromCalibrationInfo(CalibrationInfo calibrationInfo) {
        if (calibrationInfo != null) {
            bValue = calibrationInfo.getbValue();
            kValue = calibrationInfo.getkValue();
            seriesId = calibrationInfo.getSeriesId();
            deviceName = calibrationInfo.getDeviceName();
            calibrateTime = calibrationInfo.getCalibrateTime();
            signalValue = calibrationInfo.getStandardValue();
            signalValue = calibrationInfo.getSignalValue();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCalibrateTime() {
        return calibrateTime;
    }

    public void setCalibrateTime(String calibrateTime) {
        this.calibrateTime = calibrateTime;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public double getbValue() {
        return bValue;
    }

    public void setbValue(double bValue) {
        this.bValue = bValue;
    }

    public double getkValue() {
        return kValue;
    }

    public void setkValue(double kValue) {
        this.kValue = kValue;
    }

    public double getSignalValue() {
        return signalValue;
    }

    public void setSignalValue(double signalValue) {
        this.signalValue = signalValue;
    }

    public double getStandardValue() {
        return standardValue;
    }

    public void setStandardValue(double standardValue) {
        this.standardValue = standardValue;
    }

    public SeriesInfo getSeriesInfo() {
        if (seriesInfoResolvedKey != null && seriesInfoResolvedKey.equals(seriesId)) {
            return seriesInfo;
        }
        if (daoSession != null) {
            try {
                this.seriesInfo = daoSession.getSeriesInfoDao().load(seriesId);
                seriesInfoResolvedKey = seriesId;
                return this.seriesInfo;
            } catch (DaoException e) {
                LogUtil.warnOut(TAG, e, "Entity is detached from DAO context");
            }
        }
        return seriesInfo;
    }

    public void setSeriesInfo(SeriesInfo seriesInfo) {
        this.seriesInfo = seriesInfo;
    }

    public Long getSeriesInfoResolvedKey() {
        return seriesInfoResolvedKey;
    }

    public void setSeriesInfoResolvedKey(Long seriesInfoResolvedKey) {
        this.seriesInfoResolvedKey = seriesInfoResolvedKey;
    }

    public long getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(long seriesId) {
        this.seriesId = seriesId;
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public void setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        setCalibrationInfoDao(daoSession != null ? daoSession.getCalibrationInfoDao() : null);
    }

    public CalibrationInfoDao getCalibrationInfoDao() {
        return calibrationInfoDao;
    }

    public void setCalibrationInfoDao(CalibrationInfoDao calibrationInfoDao) {
        this.calibrationInfoDao = calibrationInfoDao;
    }
}
