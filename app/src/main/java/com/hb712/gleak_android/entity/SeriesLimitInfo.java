package com.hb712.gleak_android.pojo;

import com.hb712.gleak_android.dao.DaoSession;
import com.hb712.gleak_android.dao.SeriesLimitInfoDao;
import com.hb712.gleak_android.util.LogUtil;

import org.greenrobot.greendao.DaoException;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/10/21 10:10
 */
public class SeriesLimitInfo {
    private static final String TAG = SeriesLimitInfo.class.getSimpleName();
    private double maxValue;
    private Long id;
    private long seriesId;
    private SeriesInfo seriesInfo;
    private transient Long seriesInfoResolvedKey;
    private transient DaoSession daoSession;
    private transient SeriesLimitInfoDao seriesLimitInfoDao;

    public SeriesLimitInfo() {
    }

    public SeriesLimitInfo(Long id, Long seriesId, double maxValue) {
        this.id = id;
        this.seriesId = seriesId;
        this.maxValue = maxValue;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(long seriesId) {
        this.seriesId = seriesId;
    }

    public SeriesInfo getSeriesInfo() {
        if (seriesInfoResolvedKey != null && seriesInfoResolvedKey.equals(seriesId)) {
            return seriesInfo;
        }
        if (daoSession != null) {
            SeriesInfo seriesInfo = daoSession.getSeriesInfoDao().load(seriesId);
            try{
                this.seriesInfo = seriesInfo;
                this.seriesInfoResolvedKey = seriesId;
                return seriesInfo;
            } catch (DaoException e) {
                LogUtil.warnOut(TAG, e, "Entity is detached from DAO context");
            }
        }
        return null;
    }

    public void setSeriesInfo(SeriesInfo seriesInfo) {
        if (seriesInfo != null) {
            try{
                this.seriesInfo = seriesInfo;
                seriesId = seriesInfo.getId();
                seriesInfoResolvedKey = seriesId;
            } catch (DaoException e) {
                LogUtil.warnOut(TAG, e, "To-one property 'curve_id' has not-null constraint; cannot set to-one to null");
            }

        }
    }

    public Long getSeriesInfoResolvedKey() {
        return seriesInfoResolvedKey;
    }

    public void setSeriesInfoResolvedKey(Long seriesInfoResolvedKey) {
        this.seriesInfoResolvedKey = seriesInfoResolvedKey;
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public void setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        setSeriesLimitInfoDao(daoSession != null ? daoSession.getSeriesLimitInfoDao() : null);
    }

    public SeriesLimitInfoDao getSeriesLimitInfoDao() {
        return seriesLimitInfoDao;
    }

    public void setSeriesLimitInfoDao(SeriesLimitInfoDao seriesLimitInfoDao) {
        this.seriesLimitInfoDao = seriesLimitInfoDao;
    }
}
