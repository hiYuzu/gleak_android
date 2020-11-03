package com.hb712.gleak_android.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.hb712.gleak_android.util.LogUtil;

import org.greenrobot.greendao.AbstractDaoMaster;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseOpenHelper;
import org.greenrobot.greendao.database.StandardDatabase;
import org.greenrobot.greendao.identityscope.IdentityScopeType;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/10/21 14:27
 */
public class DaoMaster extends AbstractDaoMaster {
    private static final int SCHEMA_VERSION = 3;

    DaoMaster(SQLiteDatabase paramSQLiteDatabase) {
        this(new StandardDatabase(paramSQLiteDatabase));
    }

    private DaoMaster(Database paramDatabase) {
        super(paramDatabase, SCHEMA_VERSION);
        registerDaoClass(FactorCoefficientInfoDao.class);
        registerDaoClass(SeriesInfoDao.class);
        registerDaoClass(CalibrationInfoDao.class);
        registerDaoClass(SeriesLimitInfoDao.class);
    }

    private static void createAllTables(Database paramDatabase, boolean paramBoolean) {
        FactorCoefficientInfoDao.createTable(paramDatabase, paramBoolean);
        SeriesInfoDao.createTable(paramDatabase, paramBoolean);
        CalibrationInfoDao.createTable(paramDatabase, paramBoolean);
        SeriesLimitInfoDao.createTable(paramDatabase, paramBoolean);
    }

    private static void dropAllTables(Database paramDatabase, boolean paramBoolean) {
        FactorCoefficientInfoDao.dropTable(paramDatabase, paramBoolean);
        SeriesInfoDao.dropTable(paramDatabase, paramBoolean);
        CalibrationInfoDao.dropTable(paramDatabase, paramBoolean);
        SeriesLimitInfoDao.dropTable(paramDatabase, paramBoolean);
    }

    public static DaoSession newDevSession(Context paramContext, String paramString) {
        return new DaoMaster(new DevOpenHelper(paramContext, paramString).getWritableDb()).newSession();
    }

    public DaoSession newSession() {
        return new DaoSession(this.db, IdentityScopeType.Session, this.daoConfigMap);
    }

    public DaoSession newSession(IdentityScopeType paramIdentityScopeType) {
        return new DaoSession(this.db, paramIdentityScopeType, this.daoConfigMap);
    }

    public static class DevOpenHelper extends DaoMaster.OpenHelper {
        private static final String TAG = DevOpenHelper.class.getSimpleName();

        DevOpenHelper(Context paramContext, String paramString) {
            super(paramContext, paramString);
        }

        DevOpenHelper(Context paramContext, String paramString, SQLiteDatabase.CursorFactory paramCursorFactory) {
            super(paramContext, paramString, paramCursorFactory);
        }

        public void onUpgrade(Database paramDatabase, int paramInt1, int paramInt2) {
            LogUtil.infoOut(null, TAG, null, "删除所有表，更新schema版本:" + paramInt1 + " -> " + paramInt2);
            DaoMaster.dropAllTables(paramDatabase, true);
            onCreate(paramDatabase);
        }
    }

    public static abstract class OpenHelper extends DatabaseOpenHelper {
        private static final String TAG = OpenHelper.class.getSimpleName();

        OpenHelper(Context paramContext, String paramString) {
            super(paramContext, paramString, SCHEMA_VERSION);
        }

        OpenHelper(Context paramContext, String paramString, SQLiteDatabase.CursorFactory paramCursorFactory) {
            super(paramContext, paramString, paramCursorFactory, SCHEMA_VERSION);
        }

        public void onCreate(Database paramDatabase) {
            LogUtil.infoOut(null, TAG, null, "已创建表");
            DaoMaster.createAllTables(paramDatabase, false);
        }
    }
}
