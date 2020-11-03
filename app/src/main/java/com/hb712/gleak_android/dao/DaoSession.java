package com.hb712.gleak_android.dao;

import com.hb712.gleak_android.pojo.CalibrationInfo;
import com.hb712.gleak_android.pojo.FactorCoefficientInfo;
import com.hb712.gleak_android.pojo.SeriesInfo;
import com.hb712.gleak_android.pojo.SeriesLimitInfo;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import java.util.Map;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/10/21 14:51
 */
public class DaoSession extends AbstractDaoSession {
    //响应因子
    private final FactorCoefficientInfoDao factorCoefficientInfoDao;
    private final DaoConfig factorCoefficientInfoDaoConfig;
    //曲线
    private final SeriesInfoDao seriesInfoDao;
    private final DaoConfig seriesInfoDaoConfig;
    //校准
    private final CalibrationInfoDao calibrationInfoDao;
    private final DaoConfig calibrationInfoDaoConfig;
    //曲线limit
    private final SeriesLimitInfoDao seriesLimitInfoDao;
    private final DaoConfig seriesLimitInfoDaoConfig;


    DaoSession(Database paramDatabase, IdentityScopeType paramIdentityScopeType, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig> paramMap) {
        super(paramDatabase);
        factorCoefficientInfoDaoConfig = paramMap.get(FactorCoefficientInfoDao.class).clone();
        factorCoefficientInfoDaoConfig.initIdentityScope(paramIdentityScopeType);
        factorCoefficientInfoDao = new FactorCoefficientInfoDao(factorCoefficientInfoDaoConfig, this);

        seriesInfoDaoConfig = paramMap.get(SeriesInfoDao.class).clone();
        seriesInfoDaoConfig.initIdentityScope(paramIdentityScopeType);
        seriesInfoDao = new SeriesInfoDao(seriesInfoDaoConfig, this);

        calibrationInfoDaoConfig = paramMap.get(CalibrationInfoDao.class).clone();
        calibrationInfoDaoConfig.initIdentityScope(paramIdentityScopeType);
        calibrationInfoDao = new CalibrationInfoDao(calibrationInfoDaoConfig, this);

        seriesLimitInfoDaoConfig = paramMap.get(SeriesLimitInfoDao.class).clone();
        seriesLimitInfoDaoConfig.initIdentityScope(paramIdentityScopeType);
        seriesLimitInfoDao = new SeriesLimitInfoDao(seriesLimitInfoDaoConfig, this);

        registerDao(FactorCoefficientInfo.class, factorCoefficientInfoDao);
        registerDao(SeriesInfo.class, seriesInfoDao);
        registerDao(CalibrationInfo.class, calibrationInfoDao);
        registerDao(SeriesLimitInfo.class, seriesLimitInfoDao);
    }

    public void clear() {
        factorCoefficientInfoDaoConfig.clearIdentityScope();
        seriesInfoDaoConfig.clearIdentityScope();
        calibrationInfoDaoConfig.clearIdentityScope();
        seriesLimitInfoDaoConfig.clearIdentityScope();
    }

    public FactorCoefficientInfoDao getFactorCoefficientInfoDao() {
        return factorCoefficientInfoDao;
    }

    public SeriesInfoDao getSeriesInfoDao() {
        return seriesInfoDao;
    }

    public CalibrationInfoDao getCalibrationInfoDao() {
        return calibrationInfoDao;
    }

    public SeriesLimitInfoDao getSeriesLimitInfoDao() {
        return seriesLimitInfoDao;
    }
}
