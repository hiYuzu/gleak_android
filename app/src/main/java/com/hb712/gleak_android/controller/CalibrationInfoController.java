package com.hb712.gleak_android.controller;

import com.hb712.gleak_android.dao.CalibrationInfoDao;
import com.hb712.gleak_android.dao.DBManager;
import com.hb712.gleak_android.dao.SeriesInfoDao;
import com.hb712.gleak_android.entity.CalibrationInfo;
import com.hb712.gleak_android.entity.FactorCoefficientInfo;
import com.hb712.gleak_android.entity.SeriesInfo;
import com.hb712.gleak_android.util.LogUtil;
import com.hb712.gleak_android.util.ToastUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.Collections;
import java.util.List;

/**
 * 校准控制、校准信息
 *
 * @author hiYuzu
 * @version V1.0
 * @date 2020/10/21 9:57
 */
public class CalibrationInfoController {
    private static final String TAG = CalibrationInfoController.class.getSimpleName();

    private SeriesInfo currentSeries = null;
    private FactorCoefficientInfo factor;
    private List<CalibrationInfo> calibrationInfoList;

    private static CalibrationInfoController instance;

    public static synchronized CalibrationInfoController getInstance() {
        if (instance == null) {
            instance = new CalibrationInfoController();
        }
        return instance;
    }

    public synchronized void saveAll() {
        calculateKb();
        for (CalibrationInfo temp : calibrationInfoList) {
            try {
                DBManager.getInstance().getWritableSession().getCalibrationInfoDao().save(temp);
            } catch (Exception e) {
                ToastUtil.toastWithoutLog("本地数据库发生错误！");
                LogUtil.assertOut(TAG, e, "CalibrationInfoDao");
            }
        }
    }

    private void calculateKb() {
        Collections.sort(calibrationInfoList);
        int i = 0;
        while (i < calibrationInfoList.size() - 1) {
            double d1 = calibrationInfoList.get(i + 1).getSignalValue() - calibrationInfoList.get(i).getSignalValue();
            if (d1 != 0.0D) {
                d1 = (calibrationInfoList.get(i + 1).getStandardValue() - calibrationInfoList.get(i).getStandardValue()) / d1;
                double d2 = calibrationInfoList.get(i).getStandardValue();
                double d3 = calibrationInfoList.get(i).getSignalValue();
                calibrationInfoList.get(i).setKValue(d1);
                calibrationInfoList.get(i).setBValue(d2 - d3 * d1);
            } else {
                calibrationInfoList.get(i).setKValue(1.0D);
                calibrationInfoList.get(i).setBValue(0.0D);
            }
            i += 1;
        }
    }

    /**
     * 计算ppm
     *
     * @param microCurrent 微电流
     * @return
     */
    public double getValueBySignal(double microCurrent) {
        double ppm = 0.0D;
        int minSize = 2;
        if (calibrationInfoList != null) {
            if (calibrationInfoList.size() < minSize) {
                return 0.0D;
            }
            int k = -1;
            int i = 0;
            int j;
            for (; ; ) {
                j = k;
                if (i >= calibrationInfoList.size() - 1) {
                    break;
                }
                if (calibrationInfoList.get(i + 1).getSignalValue() >= microCurrent) {
                    j = i;
                    break;
                }
                i += 1;
            }
            if (j == -1) {
                j = calibrationInfoList.size() - 2;
            }
            ppm = calibrationInfoList.get(j).getKValue() * microCurrent + calibrationInfoList.get(j).getBValue();
            if (factor != null) {
                ppm *= factor.getCoefficient();
            }
        }
        return Math.max(ppm, 0.0D);
    }

    public void updateCalibrateInfo(List<CalibrationInfo> calibrationInfos) {
        int k = 0;
        for (int i = 0; i < calibrationInfos.size(); i++) {
            for (int j = 0; j < calibrationInfoList.size(); j++) {
                if (Double.compare(calibrationInfoList.get(j).getStandardValue(), calibrationInfos.get(i).getStandardValue()) == 0) {
                    calibrationInfoList.get(j).setValueFromCalibrationInfo(calibrationInfos.get(i));
                    k = 1;
                    break;
                }
            }
            if (k == 0) {
                calibrationInfoList.add(calibrationInfos.get(i));
            }
        }
        calculateKb();
        saveAll();
    }

    public void deleteCalibrateInfo(int paramInt) {
        if (calibrationInfoList.size() > paramInt) {
            calibrationInfoList.get(paramInt).delete();
            calibrationInfoList.remove(paramInt);
            saveAll();
        }
    }

    public SeriesInfo getCurrentSeries() {
        return currentSeries;
    }

    public void setCurrentSeries(SeriesInfo seriesInfo) {
        if (seriesInfo != null) {
            currentSeries = seriesInfo;
            try {
                calibrationInfoList = DBManager.getInstance().getReadableSession().getCalibrationInfoDao().queryBuilder().where(CalibrationInfoDao.Properties.SERIES_ID.eq(seriesInfo.getId()), new WhereCondition[0]).list();
                Collections.sort(calibrationInfoList);
            } catch (Exception e) {
                ToastUtil.toastWithoutLog("本地数据库发生错误！");
                LogUtil.assertOut(TAG, e, "CalibrationInfoDao");
            }
        }
    }

    public void setCurrentSeries(String seriesName) {
        List<SeriesInfo> seriesInfoList = null;
        SeriesInfoController.checkStd();
        try {
            seriesInfoList = DBManager.getInstance().getReadableSession().getSeriesInfoDao().queryBuilder().where(SeriesInfoDao.Properties.SERIES_NAME.eq(seriesName), new WhereCondition[0]).list();
        } catch (Exception e) {
            ToastUtil.toastWithoutLog("本地数据库发生错误！");
            LogUtil.assertOut(TAG, e, "SeriesInfoDao");
        }
        if (seriesInfoList != null && seriesInfoList.size() > 0) {
            setCurrentSeries(seriesInfoList.get(0));
        }
    }

    public FactorCoefficientInfo getFactor() {
        return factor;
    }

    public void setFactor(FactorCoefficientInfo factor) {
        this.factor = factor;
    }

    public List<CalibrationInfo> getCalibrationInfoList() {
        return calibrationInfoList;
    }

    public void setCalibrationInfoList(List<CalibrationInfo> calibrationInfoList) {
        this.calibrationInfoList = calibrationInfoList;
    }
}
