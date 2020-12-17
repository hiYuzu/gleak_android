package com.hb712.gleak_android.controller;

import com.hb712.gleak_android.dao.DBManager;
import com.hb712.gleak_android.dao.SeriesInfoDao;
import com.hb712.gleak_android.entity.SeriesInfo;
import com.hb712.gleak_android.util.LogUtil;
import com.hb712.gleak_android.util.ToastUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;
import java.util.List;

/**
 * 曲线控制、曲线信息
 *
 * @author hiYuzu
 * @version V1.0
 * @date 2020/10/21 11:11
 */
public class SeriesInfoController {
    private static final String TAG = SeriesInfoController.class.getSimpleName();
    private static final String STD_SERIES_NAME = "标准曲线";
    private static boolean checkedStd = false;

    static void checkStd() {
        if (!checkedStd) {
            try {
                List<SeriesInfo> seriesInfoList = DBManager.getInstance().getReadableSession().getSeriesInfoDao().queryBuilder().where(SeriesInfoDao.Properties.STD_SERIES.eq(Boolean.TRUE), new WhereCondition[0]).list();
                if (seriesInfoList != null && seriesInfoList.size() >= 1) {
                    checkedStd = true;
                    return;
                }
                SeriesInfo seriesInfo = new SeriesInfo();
                seriesInfo.setSeriesName(STD_SERIES_NAME);
                seriesInfo.setStdSeries(true);
                DBManager.getInstance().getWritableSession().getSeriesInfoDao().save(seriesInfo);
            } catch (Exception e) {
                ToastUtil.toastWithoutLog("本地数据库发生错误！");
                LogUtil.assertOut(TAG, e, "SeriesInfoDao");
            }
        }
    }

    public static List<SeriesInfo> getAll() {
        checkStd();
        try {
            return DBManager.getInstance().getReadableSession().getSeriesInfoDao().queryBuilder().list();
        } catch (Exception e) {
            ToastUtil.toastWithoutLog("本地数据库发生错误！");
            LogUtil.assertOut(TAG, e, "SeriesInfoDao");
            return new ArrayList<>();
        }
    }

    public static List<SeriesInfo> getAllEdits() {
        checkStd();
        try {
            return DBManager.getInstance().getReadableSession().getSeriesInfoDao().queryBuilder().where(SeriesInfoDao.Properties.STD_SERIES.eq(Boolean.FALSE), new WhereCondition[0]).list();
        } catch (Exception e) {
            ToastUtil.toastWithoutLog("本地数据库发生错误！");
            LogUtil.assertOut(TAG, e, "SeriesInfoDao");
            return new ArrayList<>();
        }
    }
}
