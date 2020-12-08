package com.hb712.gleak_android.util;

import com.hb712.gleak_android.entity.FactorCoefficientInfo;
import com.hb712.gleak_android.message.blue.CalibrationInfo;
import com.hb712.gleak_android.message.blue.CurveInfo;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class CalibrateInfoManager {
    static CalibrateInfoManager _mng = new CalibrateInfoManager();
    private List<CalibrationInfo> _CalibrationInfos = null;
    FactorCoefficientInfo _Factor = null;
    private CurveInfo currentCurve = null;

    private void calculateKB() {
        Collections.sort(this._CalibrationInfos);
        int i = 0;
        while (i < this._CalibrationInfos.size() - 1) {
            double d1 = ((CalibrationInfo) this._CalibrationInfos.get(i + 1)).getSignalValue() - ((CalibrationInfo) this._CalibrationInfos.get(i)).getSignalValue();
            if (d1 != 0.0D) {
                d1 = (((CalibrationInfo) this._CalibrationInfos.get(i + 1)).getStandardValue() - ((CalibrationInfo) this._CalibrationInfos.get(i)).getStandardValue()) / d1;
                double d2 = ((CalibrationInfo) this._CalibrationInfos.get(i)).getStandardValue();
                double d3 = ((CalibrationInfo) this._CalibrationInfos.get(i)).getSignalValue();
                ((CalibrationInfo) this._CalibrationInfos.get(i)).setKValue(d1);
                ((CalibrationInfo) this._CalibrationInfos.get(i)).setBValue(d2 - d3 * d1);
            } else {
                ((CalibrationInfo) this._CalibrationInfos.get(i)).setKValue(1.0D);
                ((CalibrationInfo) this._CalibrationInfos.get(i)).setBValue(0.0D);
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
//            this._CalibrationInfos = DBManager.getInstance().getReadableSession().getCalibrationInfoDao().queryBuilder().where(CalibrationInfoDao.Properties.Curve_id.eq(paramCurveInfo.get_id()), new WhereCondition[0]).list();
//            Collections.sort(this._CalibrationInfos);
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
//        if (this._CalibrationInfos.size() > paramInt) {
//            ((CalibrationInfo) this._CalibrationInfos.get(paramInt)).delete();
//            this._CalibrationInfos.remove(paramInt);
//        }
//        saveAll();
//    }

    public CurveInfo getCurrentCurve() {
        return this.currentCurve;
    }

    public double getValueBySignal(double paramDouble) {
        double d = paramDouble;
        Object localObject = this._CalibrationInfos;
        if (localObject != null) {
            if (((List) localObject).size() < 2) {
                return 0.0D;
            }
            int k = -1;
            int i = 0;
            int j;
            for (; ; ) {
                j = k;
                if (i >= this._CalibrationInfos.size() - 1) {
                    break;
                }
                if (((CalibrationInfo) this._CalibrationInfos.get(i + 1)).getSignalValue() >= paramDouble) {
                    j = i;
                    break;
                }
                i += 1;
            }
            if (j == -1) {
                j = this._CalibrationInfos.size() - 2;
            }
            paramDouble = ((CalibrationInfo) this._CalibrationInfos.get(j)).getKValue() * paramDouble + ((CalibrationInfo) this._CalibrationInfos.get(j)).getBValue();
            localObject = this._Factor;
            if (localObject != null) {
                paramDouble *= ((FactorCoefficientInfo) localObject).getCoefficient();
            }
        } else {
            paramDouble = d;
        }
        if (paramDouble < 0.0D) {
            return 0.0D;
        }
        return paramDouble;
    }

    public List<CalibrationInfo> get_CalibrationInfos() {
        return this._CalibrationInfos;
    }

    public FactorCoefficientInfo get_Factor() {
        return this._Factor;
    }

//    public void saveAll() {
//        calculateKB();
//        Iterator localIterator = this._CalibrationInfos.iterator();
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
                if (j >= this._CalibrationInfos.size()) {
                    break;
                }
                if (Double.compare(((CalibrationInfo) this._CalibrationInfos.get(j)).getStandardValue(), ((CalibrationInfo) paramList.get(i)).getStandardValue()) == 0) {
                    ((CalibrationInfo) this._CalibrationInfos.get(j)).setValueFromCalibrateInfo((CalibrationInfo) paramList.get(i));
                    k = 1;
                    break;
                }
                j += 1;
            }
            if (k == 0) {
                this._CalibrationInfos.add(paramList.get(i));
            }
            i += 1;
        }
//        saveAll();
    }
}
