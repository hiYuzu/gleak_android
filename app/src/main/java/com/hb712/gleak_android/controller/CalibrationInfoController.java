package com.hb712.gleak_android.controller;

import com.hb712.gleak_android.entity.CalibrationInfo;
import com.hb712.gleak_android.entity.FactorCoefficientInfo;
import com.hb712.gleak_android.entity.SeriesInfo;

import java.util.List;

/**
 * 校准控制、校准信息
 *
 * @author hiYuzu
 * @version V1.0
 * @date 2020/10/21 9:57
 */
public class CalibrationInfoController {

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

    public SeriesInfo getCurrentSeries() {
        return currentSeries;
    }

    public void setCurrentSeries(SeriesInfo seriesInfo) {
        if (seriesInfo != null) {
            currentSeries = seriesInfo;
//            calibrationInfoList = DBManager.getInstance().getReadableSession().getCalibrationInfoDao().queryBuilder().where(CalibrationInfoDao.Properties.seriesId.eq(seriesInfo.getId()), new WhereCondition[0]).list();
//            Collections.sort(calibrationInfoList);
        }
    }

    public void setCurrentSeries(String seriesName) {
        // TODO: hiYuzu 2020/12/2 测试用，待删除
        setCurrentSeries(new SeriesInfo(1L, "测试曲线", true));
        /*
        SeriesInfoController.checkStd();
        List<SeriesInfo> seriesInfoList = DBManager.getInstance().getReadableSession().getSeriesInfoDao().queryBuilder().where(SeriesInfoDao.Properties.seriesName.eq(seriesName), new WhereCondition[0]).list();
        if (seriesInfoList != null && seriesInfoList.size() > 0)
        {
            setCurrentSeries(seriesInfoList.get(0));
        }*/
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
