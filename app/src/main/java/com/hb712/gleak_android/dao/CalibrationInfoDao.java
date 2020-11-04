package com.hb712.gleak_android.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import com.hb712.gleak_android.pojo.CalibrationInfo;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.internal.DaoConfig;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/10/22 8:58
 */
public class CalibrationInfoDao extends AbstractDao<CalibrationInfo, Long> {
    private static final String TABLE_NAME = "calibration_info";
    private static final String TAG = CalibrationInfoDao.class.getSimpleName();
    private DaoSession daoSession;

    public CalibrationInfoDao(DaoConfig daoConfig) {
        super(daoConfig);
    }

    CalibrationInfoDao(DaoConfig daoConfig, DaoSession daoSession) {
        super(daoConfig, daoSession);
        this.daoSession = daoSession;
    }

    static void createTable(Database paramDatabase, boolean paramBoolean) {
        String str;
        if (paramBoolean) {
            str = "IF NOT EXISTS ";
        } else {
            str = "";
        }
        String sql = "CREATE TABLE " +
                str +
                "\"" + TABLE_NAME + "\" (\"id\" INTEGER PRIMARY KEY ,\"device_name\" TEXT,\"series_id\" INTEGER NOT NULL ,\"calibrate_time\" TEXT,\"signal_value\" REAL NOT NULL ,\"standard_value\" REAL NOT NULL ,\"k_value\" REAL NOT NULL ,\"b_value\" REAL NOT NULL );";
        paramDatabase.execSQL(sql);
    }

    static void dropTable(Database paramDatabase, boolean paramBoolean) {
        String str;
        if (paramBoolean) {
            str = "IF EXISTS ";
        } else {
            str = "";
        }
        String sql = "DROP TABLE " +
                str +
                "\"" + TABLE_NAME + "\"";
        paramDatabase.execSQL(sql);
    }

    protected final void attachEntity(CalibrationInfo calibrationInfo) {
        super.attachEntity(calibrationInfo);
        calibrationInfo.setDaoSession(daoSession);
    }

    protected final void bindValues(SQLiteStatement sqLiteStatement, CalibrationInfo calibrationInfo) {
        sqLiteStatement.clearBindings();
        Long id = calibrationInfo.getId();
        if (id != null) {
            sqLiteStatement.bindLong(1, id);
        }
        String deviceName = calibrationInfo.getDeviceName();
        if (deviceName != null) {
            sqLiteStatement.bindString(2, deviceName);
        }
        sqLiteStatement.bindLong(3, calibrationInfo.getSeriesId());
        String calibrateTime = calibrationInfo.getCalibrateTime();
        if (calibrateTime != null) {
            sqLiteStatement.bindString(4, calibrateTime);
        }
        sqLiteStatement.bindDouble(5, calibrationInfo.getSignalValue());
        sqLiteStatement.bindDouble(6, calibrationInfo.getStandardValue());
        sqLiteStatement.bindDouble(7, calibrationInfo.getkValue());
        sqLiteStatement.bindDouble(8, calibrationInfo.getbValue());
    }

    protected final void bindValues(DatabaseStatement databaseStatement, CalibrationInfo calibrationInfo) {
        databaseStatement.clearBindings();
        Long id = calibrationInfo.getId();
        if (id != null) {
            databaseStatement.bindLong(1, id);
        }
        String deviceName = calibrationInfo.getDeviceName();
        if (deviceName != null) {
            databaseStatement.bindString(2, deviceName);
        }
        databaseStatement.bindLong(3, calibrationInfo.getSeriesId());
        String calibrateTime = calibrationInfo.getCalibrateTime();
        if (calibrateTime != null) {
            databaseStatement.bindString(4, calibrateTime);
        }
        databaseStatement.bindDouble(5, calibrationInfo.getSignalValue());
        databaseStatement.bindDouble(6, calibrationInfo.getStandardValue());
        databaseStatement.bindDouble(7, calibrationInfo.getkValue());
        databaseStatement.bindDouble(8, calibrationInfo.getbValue());
    }

    public Long getKey(CalibrationInfo calibrationInfo) {
        return calibrationInfo != null ? calibrationInfo.getId() : null;
    }

    public boolean hasKey(CalibrationInfo calibrationInfo) {
        return calibrationInfo.getId() != null;
    }

    protected final boolean isEntityUpdateable() {
        return true;
    }

    public CalibrationInfo readEntity(Cursor cursor, int paramInt) {
        Long id = null;
        if (!cursor.isNull(paramInt)) {
            id = cursor.getLong(paramInt);
        }
        String deviceName = null;
        if (!cursor.isNull(paramInt + 1)) {
            deviceName = cursor.getString(paramInt + 1);
        }
        long seriesId = cursor.getLong(paramInt + 2);
        String calibrateTime = null;
        if (!cursor.isNull(paramInt + 3)) {
            calibrateTime = cursor.getString(paramInt + 3);
        }
        return new CalibrationInfo(id, deviceName, seriesId, calibrateTime, cursor.getDouble(paramInt + 4), cursor.getDouble(paramInt + 5), cursor.getDouble(paramInt + 6), cursor.getDouble(paramInt + 7));
    }


    public void readEntity(Cursor cursor, CalibrationInfo calibrationInfo, int offset) {
        if (!cursor.isNull(offset)) {
            calibrationInfo.setId(cursor.getLong(offset));
        }
        if (!cursor.isNull(offset + 1)) {
            calibrationInfo.setDeviceName(cursor.getString(offset + 1));
        }
        calibrationInfo.setSeriesId(cursor.getLong(offset + 2));
        if (!cursor.isNull(offset + 3)) {
            calibrationInfo.setCalibrateTime(cursor.getString(offset + 3));
        }
        calibrationInfo.setSignalValue(cursor.getDouble(offset + 4));
        calibrationInfo.setStandardValue(cursor.getDouble(offset + 5));
        calibrationInfo.setkValue(cursor.getDouble(offset + 6));
        calibrationInfo.setbValue(cursor.getDouble(offset + 7));
    }

    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset) ? null : cursor.getLong(offset);
    }

    protected final Long updateKeyAfterInsert(CalibrationInfo calibrationInfo, long rowId) {
        calibrationInfo.setId(rowId);
        return rowId;
    }

    public static class Properties {
        public static final Property bValue = new Property(7, Double.TYPE, "bValue", false, "b_value");
        public static final Property calibrateTime = new Property(3, String.class, "calibrateTime", false, "calibrate_time");
        public static final Property seriesId = new Property(2, Long.TYPE, "seriesId", false, "series_id");
        public static final Property deviceName = new Property(1, String.class, "deviceName", false, "device_name");
        public static final Property id = new Property(0, Long.class, "id", true, "id");
        public static final Property kValue= new Property(6, Double.TYPE, "kValue", false, "k_value");
        public static final Property signalValue = new Property(4, Double.TYPE, "signalValue", false, "signal_value");
        public static final Property standardValue = new Property(5, Double.TYPE, "standardValue", false, "standard_value");
    }
}
