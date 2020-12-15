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
 * 保存数据库对象（SQLiteDatabase）并管理特定模式的Dao类
 * 静态方法创建或删除表
 * 内部类OpenHelper和DevOpenHelper是SQLite数据库的SQLiteOpenHelper实现
 *
 * @author hiYuzu
 * @version V1.0
 * @date 2020/10/21 14:27
 */
public class DaoMaster extends AbstractDaoMaster {
    private static final int SCHEMA_VERSION = 3;

    DaoMaster(SQLiteDatabase sqliteDatabase) {
        this(new StandardDatabase(sqliteDatabase));
    }

    private DaoMaster(Database database) {
        super(database, SCHEMA_VERSION);
        registerDaoClass(FactorCoefficientInfoDao.class);
        registerDaoClass(SeriesInfoDao.class);
        registerDaoClass(CalibrationInfoDao.class);
        registerDaoClass(SeriesLimitInfoDao.class);
    }

    private static void createAllTables(Database database) {
        FactorCoefficientInfoDao.createTable(database);
        SeriesInfoDao.createTable(database);
        CalibrationInfoDao.createTable(database);
        SeriesLimitInfoDao.createTable(database);
    }

    private static void dropAllTables(Database database) {
        FactorCoefficientInfoDao.dropTable(database);
        SeriesInfoDao.dropTable(database);
        CalibrationInfoDao.dropTable(database);
        SeriesLimitInfoDao.dropTable(database);
    }

    public static DaoSession newDevSession(Context context, String dbName) {
        return new DaoMaster(new DevOpenHelper(context, dbName).getWritableDb()).newSession();
    }

    @Override
    public DaoSession newSession() {
        return new DaoSession(db, IdentityScopeType.Session, daoConfigMap);
    }

    @Override
    public DaoSession newSession(IdentityScopeType identityScopeType) {
        return new DaoSession(db, identityScopeType, daoConfigMap);
    }

    public static class DevOpenHelper extends DaoMaster.OpenHelper {
        private static final String TAG = DevOpenHelper.class.getSimpleName();

        DevOpenHelper(Context context, String dbName) {
            super(context, dbName);
        }

        DevOpenHelper(Context context, String dbName, SQLiteDatabase.CursorFactory cursorFactory) {
            super(context, dbName, cursorFactory);
        }

        @Override
        public void onUpgrade(Database db, int oldVersion, int newVersion) {
            LogUtil.infoOut(TAG, "删除所有表，更新schema版本:" + oldVersion + " -> " + newVersion);
            DaoMaster.dropAllTables(db);
            onCreate(db);
        }
    }

    public static abstract class OpenHelper extends DatabaseOpenHelper {
        private static final String TAG = OpenHelper.class.getSimpleName();

        OpenHelper(Context context, String dbName) {
            super(context, dbName, SCHEMA_VERSION);
        }

        OpenHelper(Context context, String dbName, SQLiteDatabase.CursorFactory cursorFactory) {
            super(context, dbName, cursorFactory, SCHEMA_VERSION);
        }

        @Override
        public void onCreate(Database db) {
            DaoMaster.createAllTables(db);
            LogUtil.infoOut(TAG, "已创建表");
        }
    }
}
