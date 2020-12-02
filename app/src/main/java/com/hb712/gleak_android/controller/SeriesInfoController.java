package com.hb712.gleak_android.controller;

import com.hb712.gleak_android.dao.DBManager;
import com.hb712.gleak_android.dao.SeriesInfoDao;
import com.hb712.gleak_android.entity.SeriesInfo;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;
import java.util.List;

/**
 * 曲线控制、曲线信息
 * @author hiYuzu
 * @version V1.0
 * @date 2020/10/21 11:11
 */
public class SeriesInfoController {
    private static final String STD_SERIES_NAME = "标准曲线";
    private static boolean checkedStd = false;

    static void checkStd() {
        if (!checkedStd) {
            List<SeriesInfo> seriesInfoList =  DBManager.getInstance().getReadableSession().getSeriesInfoDao().queryBuilder().where(SeriesInfoDao.Properties.stdSeries.eq(Boolean.TRUE), new WhereCondition[0]).list();
            if (seriesInfoList != null && seriesInfoList.size() >= 1) {
                checkedStd = true;
                return;
            }
            SeriesInfo seriesInfo = new SeriesInfo();
            seriesInfo.setSeriesName(STD_SERIES_NAME);
            seriesInfo.setStdSeries(true);
            DBManager.getInstance().getWritableSession().getSeriesInfoDao().save(seriesInfo);
        }
    }

    public static List<SeriesInfo> getAll()
    {
        //checkStd();
        //return DBManager.getInstance().getReadableSession().getSeriesInfoDao().queryBuilder().list();
        // TODO: hiYuzu 2020/12/2 测试用，待删除
        List<SeriesInfo> seriesInfoList = new ArrayList<>();
        seriesInfoList.add(new SeriesInfo(1L, "测试曲线", true));
        return seriesInfoList;
    }

    public static List<SeriesInfo> getAllEdits()
    {
        checkStd();
        return DBManager.getInstance().getReadableSession().getSeriesInfoDao().queryBuilder().where(SeriesInfoDao.Properties.stdSeries.eq(Boolean.FALSE), new WhereCondition[0]).list();
    }
}
