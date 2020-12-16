package com.hb712.gleak_android.entity;

import android.support.annotation.NonNull;

import com.hb712.gleak_android.dao.CalibrationInfoDao;
import com.hb712.gleak_android.dao.DaoSession;
import com.hb712.gleak_android.util.LogUtil;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/10/22 8:30
 */
@Entity
public class CalibrationInfo implements Comparable<CalibrationInfo> {
    private static final String TAG = CalibrationInfo.class.getSimpleName();
    @Id
    private Long id;
    private String deviceName;
    private String calibrateTime;
    private double bValue;
    private double kValue;
    private double signalValue;
    private double standardValue;
    private long seriesId;

    private transient DaoSession daoSession;
    private transient CalibrationInfoDao calibrationInfoDao;

    @Generated
    public CalibrationInfo() {
    }

    @Generated
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

    @Override
    public int compareTo(@NonNull CalibrationInfo calibrationInfo) {
        return Double.compare(standardValue, calibrationInfo.standardValue);
    }

    public void delete() {
        if (calibrationInfoDao != null) {
            calibrationInfoDao.delete(this);
            return;
        }
        throw new DaoException("Entity is detached from DAO context");
    }

    public void setValueFromCalibrationInfo(CalibrationInfo calibrationInfo) {
        if (calibrationInfo != null) {
            bValue = calibrationInfo.getBValue();
            kValue = calibrationInfo.getKValue();
            seriesId = calibrationInfo.getSeriesId();
            deviceName = calibrationInfo.getDeviceName();
            calibrateTime = calibrationInfo.getCalibrateTime();
            standardValue = calibrationInfo.getStandardValue();
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

    public double getBValue() {
        return bValue;
    }

    public void setBValue(double bValue) {
        this.bValue = bValue;
    }

    public double getKValue() {
        return kValue;
    }

    public void setKValue(double kValue) {
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
