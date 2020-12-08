package com.hb712.gleak_android.message.blue;

import org.greenrobot.greendao.DaoException;

public class CalibrationInfo implements Comparable<CalibrationInfo> {
    private double BValue;
    private String CalibrateTime;
    private String DeviceName;
    private Long ID;
    private double KValue;
    private double SignalValue;
    private double StandardValue;
    private CurveInfo curveInfo;
    private transient Long curveInfo__resolvedKey;
    private long curve_id;
//    private transient DaoSession daoSession;
//    private transient CalibrationInfoDao myDao;

    public CalibrationInfo() {
    }

    public CalibrationInfo(Long paramLong, String paramString1, long paramLong1, String paramString2, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) {
        this.ID = paramLong;
        this.DeviceName = paramString1;
        this.curve_id = paramLong1;
        this.CalibrateTime = paramString2;
        this.SignalValue = paramDouble1;
        this.StandardValue = paramDouble2;
        this.KValue = paramDouble3;
        this.BValue = paramDouble4;
    }

//    public void __setDaoSession(DaoSession paramDaoSession)
//    {
//        this.daoSession = paramDaoSession;
//        if (paramDaoSession != null) {
//            paramDaoSession = paramDaoSession.getCalibrationInfoDao();
//        } else {
//            paramDaoSession = null;
//        }
//        this.myDao = paramDaoSession;
//    }

    public int compareTo(CalibrationInfo paramCalibrationInfo) {
        if (paramCalibrationInfo == null) {
            return 1;
        }
        return Double.compare(this.SignalValue, paramCalibrationInfo.SignalValue);
    }

//    public void delete()
//    {
//        CalibrationInfoDao localCalibrationInfoDao = this.myDao;
//        if (localCalibrationInfoDao != null)
//        {
//            localCalibrationInfoDao.delete(this);
//            return;
//        }
//        throw new DaoException("Entity is detached from DAO context");
//    }

    public double getBValue() {
        return this.BValue;
    }

    public String getCalibrateTime() {
        return this.CalibrateTime;
    }

//    public CurveInfo getCurveInfo()
//    {
//        long l = this.curve_id;
//        Object localObject1 = this.curveInfo__resolvedKey;
//        if ((localObject1 != null) && (((Long)localObject1).equals(Long.valueOf(l)))) {
//            break label72;
//        }
//        localObject1 = this.daoSession;
//        if (localObject1 != null)
//        {
//            localObject1 = (CurveInfo)((DaoSession)localObject1).getCurveInfoDao().load(Long.valueOf(l));
//            try
//            {
//                this.curveInfo = ((CurveInfo)localObject1);
//                this.curveInfo__resolvedKey = Long.valueOf(l);
//                label72:
//                return this.curveInfo;
//            }
//            finally {}
//        }
//        throw new DaoException("Entity is detached from DAO context");
//    }

    public long getCurve_id() {
        return this.curve_id;
    }

    public String getDeviceName() {
        return this.DeviceName;
    }

    public Long getID() {
        return this.ID;
    }

    public double getKValue() {
        return this.KValue;
    }

    public double getSignalValue() {
        return this.SignalValue;
    }

    public double getStandardValue() {
        return this.StandardValue;
    }

//    public void refresh()
//    {
//        CalibrationInfoDao localCalibrationInfoDao = this.myDao;
//        if (localCalibrationInfoDao != null)
//        {
//            localCalibrationInfoDao.refresh(this);
//            return;
//        }
//        throw new DaoException("Entity is detached from DAO context");
//    }

    public void setBValue(double paramDouble) {
        this.BValue = paramDouble;
    }

    public void setCalibrateTime(String paramString) {
        this.CalibrateTime = paramString;
    }

    public void setCurveInfo(CurveInfo paramCurveInfo) {
        if (paramCurveInfo != null) {
            try {
                this.curveInfo = paramCurveInfo;
                this.curve_id = paramCurveInfo.get_id().longValue();
                this.curveInfo__resolvedKey = Long.valueOf(this.curve_id);
                return;
            } finally {
            }
        }
        throw new DaoException("To-one property 'curve_id' has not-null constraint; cannot set to-one to null");
    }

    public void setCurve_id(long paramLong) {
        this.curve_id = paramLong;
    }

    public void setDeviceName(String paramString) {
        this.DeviceName = paramString;
    }

    public void setID(Long paramLong) {
        this.ID = paramLong;
    }

    public void setKValue(double paramDouble) {
        this.KValue = paramDouble;
    }

    public void setSignalValue(double paramDouble) {
        this.SignalValue = paramDouble;
    }

    public void setStandardValue(double paramDouble) {
        this.StandardValue = paramDouble;
    }

    public void setValueFromCalibrateInfo(CalibrationInfo paramCalibrationInfo) {
        if (paramCalibrationInfo != null) {
            this.BValue = paramCalibrationInfo.BValue;
            this.KValue = paramCalibrationInfo.KValue;
            this.curve_id = paramCalibrationInfo.curve_id;
            this.DeviceName = paramCalibrationInfo.DeviceName;
            this.CalibrateTime = paramCalibrationInfo.CalibrateTime;
            this.StandardValue = paramCalibrationInfo.StandardValue;
            this.SignalValue = paramCalibrationInfo.SignalValue;
            return;
        }
    }

//    public void update()
//    {
//        CalibrationInfoDao localCalibrationInfoDao = this.myDao;
//        if (localCalibrationInfoDao != null)
//        {
//            localCalibrationInfoDao.update(this);
//            return;
//        }
//        throw new DaoException("Entity is detached from DAO context");
//    }
}
