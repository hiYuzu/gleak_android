package com.hb712.gleak_android.util;

import com.hb712.gleak_android.entity.FactorCoefficientInfo;
import com.hb712.gleak_android.message.blue.CalibrationInfo;
import com.hb712.gleak_android.message.blue.CurveInfo;

import java.util.Collections;
import java.util.List;

public class CalibrateInfoManager {
    static CalibrateInfoManager _mng = new CalibrateInfoManager();
    private List<CalibrationInfo> calibrationInfos = null;
    FactorCoefficientInfo _Factor = null;
    private CurveInfo currentCurve = null;

    private void calculateKB() {
        Collections.sort(this.calibrationInfos);
        int i = 0;
        while (i < this.calibrationInfos.size() - 1) {
            double d1 = this.calibrationInfos.get(i + 1).getSignalValue() - this.calibrationInfos.get(i).getSignalValue();
            if (d1 != 0.0D) {
                d1 = (this.calibrationInfos.get(i + 1).getStandardValue() - this.calibrationInfos.get(i).getStandardValue()) / d1;
                double d2 = this.calibrationInfos.get(i).getStandardValue();
                double d3 = this.calibrationInfos.get(i).getSignalValue();
                this.calibrationInfos.get(i).setKValue(d1);
                this.calibrationInfos.get(i).setBValue(d2 - d3 * d1);
            } else {
                this.calibrationInfos.get(i).setKValue(1.0D);
                this.calibrationInfos.get(i).setBValue(0.0D);
            }
            i += 1;
        }
    }

    public static CalibrateInfoManager get_mng() {
        return _mng;
    }

//    public void changeCurve(CurveInfo paramCurveInfo) {
//        if (paramCurveInfo != null) {
//            this.currentCurve = paramCurveInfo;
//            this.calibrationInfos = DBManager.getInstance().getReadableSession().getCalibrationInfoDao().queryBuilder().where(CalibrationInfoDao.Properties.Curve_id.eq(paramCurveInfo.get_id()), new WhereCondition[0]).list();
//            Collections.sort(this.calibrationInfos);
//            return;
//        }
//    }

//    public void changeCurve(String paramString) {
//        CurveInfoManager.checkStd();
//        paramString = DBManager.getInstance().getReadableSession().getCurveInfoDao().queryBuilder().where(CurveInfoDao.Properties.CurveName.eq(paramString), new WhereCondition[0]).list();
//        if ((paramString != null) && (paramString.size() > 0)) {
//            changeCurve((CurveInfo) paramString.get(0));
//            return;
//        }
//    }

//    public void deleteCalibrateInfo(int paramInt) {
//        if (this.calibrationInfos.size() > paramInt) {
//            ((CalibrationInfo) this.calibrationInfos.get(paramInt)).delete();
//            this.calibrationInfos.remove(paramInt);
//        }
//        saveAll();
//    }

    public CurveInfo getCurrentCurve() {
        return this.currentCurve;
    }

    public double getValueBySignal(double paramDouble) {
        double d = paramDouble;
        List<CalibrationInfo> calibrationInfos = this.calibrationInfos;
        if (calibrationInfos != null) {
            if (calibrationInfos.size() < 2) {
                return 0.0D;
            }
            int k = -1;
            int i = 0;
            int j;
            for (; ; ) {
                j = k;
                if (i >= this.calibrationInfos.size() - 1) {
                    break;
                }
                if (this.calibrationInfos.get(i + 1).getSignalValue() >= paramDouble) {
                    j = i;
                    break;
                }
                i += 1;
            }
            if (j == -1) {
                j = this.calibrationInfos.size() - 2;
            }
            paramDouble = this.calibrationInfos.get(j).getKValue() * paramDouble + this.calibrationInfos.get(j).getBValue();
            FactorCoefficientInfo factor = this._Factor;
            if (factor != null) {
                paramDouble *= factor.getCoefficient();
            }
        }
        return Math.max(paramDouble, 0.0D);
    }

    public List<CalibrationInfo> get_CalibrationInfos() {
        return this.calibrationInfos;
    }

    public FactorCoefficientInfo get_Factor() {
        return this._Factor;
    }

//    public void saveAll() {
//        calculateKB();
//        Iterator localIterator = this.calibrationInfos.iterator();
//        while (localIterator.hasNext()) {
//            CalibrationInfo localCalibrationInfo = (CalibrationInfo) localIterator.next();
//            DBManager.getInstance().getWritableSession().getCalibrationInfoDao().save(localCalibrationInfo);
//        }
//    }

    public void setResponseFactor(FactorCoefficientInfo paramFactorCoefficientInfo) {
        this._Factor = paramFactorCoefficientInfo;
    }

    public void updateCalibrateInfo(List<CalibrationInfo> paramList) {
        int i = 0;
        while (i < paramList.size()) {
            int m = 0;
            int j = 0;
            int k;
            for (; ; ) {
                k = m;
                if (j >= this.calibrationInfos.size()) {
                    break;
                }
                if (Double.compare(this.calibrationInfos.get(j).getStandardValue(), paramList.get(i).getStandardValue()) == 0) {
                    this.calibrationInfos.get(j).setValueFromCalibrateInfo(paramList.get(i));
                    k = 1;
                    break;
                }
                j += 1;
            }
            if (k == 0) {
                this.calibrationInfos.add(paramList.get(i));
            }
            i += 1;
        }
//        saveAll();
    }
}
