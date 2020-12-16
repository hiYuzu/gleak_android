package com.hb712.gleak_android.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import com.hb712.gleak_android.entity.CalibrationInfo;

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
    public static final String TABLENAME = "calibration_info";
    private static final String TAG = CalibrationInfoDao.class.getSimpleName();
    private DaoSession daoSession;

    public CalibrationInfoDao(DaoConfig daoConfig) {
        super(daoConfig);
    }

    CalibrationInfoDao(DaoConfig daoConfig, DaoSession daoSession) {
        super(daoConfig, daoSession);
        this.daoSession = daoSession;
    }

    static void createTable(Database database) {
        String sql = "CREATE TABLE IF NOT EXISTS " +
                "\"" + TABLENAME + "\" (\"id\" INTEGER PRIMARY KEY ,\"device_name\" TEXT,\"series_id\" INTEGER NOT NULL ,\"calibrate_time\" TEXT,\"signal_value\" REAL NOT NULL ,\"standard_value\" REAL NOT NULL ,\"k_value\" REAL NOT NULL ,\"b_value\" REAL NOT NULL );";
        database.execSQL(sql);
    }

    static void dropTable(Database database) {
        String sql = "DROP TABLE IF EXISTS " +
                "\"" + TABLENAME + "\"";
        database.execSQL(sql);
    }

    @Override
    protected void attachEntity(CalibrationInfo entity) {
        super.attachEntity(entity);
        entity.setDaoSession(daoSession);
    }

    @Override
    protected void bindValues(SQLiteStatement stmt, CalibrationInfo entity) {
        stmt.clearBindings();
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        String deviceName = entity.getDeviceName();
        if (deviceName != null) {
            stmt.bindString(2, deviceName);
        }
        stmt.bindLong(3, entity.getSeriesId());
        String calibrateTime = entity.getCalibrateTime();
        if (calibrateTime != null) {
            stmt.bindString(4, calibrateTime);
        }
        stmt.bindDouble(5, entity.getSignalValue());
        stmt.bindDouble(6, entity.getStandardValue());
        stmt.bindDouble(7, entity.getKValue());
        stmt.bindDouble(8, entity.getBValue());
    }

    @Override
    protected void bindValues(DatabaseStatement stmt, CalibrationInfo entity) {
        stmt.clearBindings();
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        String deviceName = entity.getDeviceName();
        if (deviceName != null) {
            stmt.bindString(2, deviceName);
        }
        stmt.bindLong(3, entity.getSeriesId());
        String calibrateTime = entity.getCalibrateTime();
        if (calibrateTime != null) {
            stmt.bindString(4, calibrateTime);
        }
        stmt.bindDouble(5, entity.getSignalValue());
        stmt.bindDouble(6, entity.getStandardValue());
        stmt.bindDouble(7, entity.getKValue());
        stmt.bindDouble(8, entity.getBValue());
    }

    @Override
    protected Long getKey(CalibrationInfo entity) {
        return entity != null ? entity.getId() : null;
    }

    @Override
    protected boolean hasKey(CalibrationInfo entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }

    @Override
    protected CalibrationInfo readEntity(Cursor cursor, int paramInt) {
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

    @Override
    protected void readEntity(Cursor cursor, CalibrationInfo entity, int offset) {
        if (!cursor.isNull(offset)) {
            entity.setId(cursor.getLong(offset));
        }
        if (!cursor.isNull(offset + 1)) {
            entity.setDeviceName(cursor.getString(offset + 1));
        }
        entity.setSeriesId(cursor.getLong(offset + 2));
        if (!cursor.isNull(offset + 3)) {
            entity.setCalibrateTime(cursor.getString(offset + 3));
        }
        entity.setSignalValue(cursor.getDouble(offset + 4));
        entity.setStandardValue(cursor.getDouble(offset + 5));
        entity.setKValue(cursor.getDouble(offset + 6));
        entity.setBValue(cursor.getDouble(offset + 7));
    }

    @Override
    protected Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset) ? null : cursor.getLong(offset);
    }

    @Override
    protected Long updateKeyAfterInsert(CalibrationInfo entity, long rowId) {
        entity.setId(rowId);
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
