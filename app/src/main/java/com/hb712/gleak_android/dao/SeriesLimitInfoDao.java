package com.hb712.gleak_android.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import com.hb712.gleak_android.entity.SeriesLimitInfo;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.internal.DaoConfig;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/10/22 11:19
 */
public class SeriesLimitInfoDao extends AbstractDao<SeriesLimitInfo, Long> {
    public static final String TABLENAME = "series_limit_info";
    private DaoSession daoSession;
    private String selectDeep;

    public SeriesLimitInfoDao(DaoConfig daoConfig) {
        super(daoConfig);
    }

    SeriesLimitInfoDao(DaoConfig daoConfig, DaoSession daoSession) {
        super(daoConfig, daoSession);
        this.daoSession = daoSession;
    }

    static void createTable(Database database) {
        String sql = "CREATE TABLE IF NOT EXISTS " +
                "\"" + TABLENAME + "\" (\"id\" INTEGER PRIMARY KEY AUTOINCREMENT ,\"series_id\" INTEGER NOT NULL ,\"max_value\" REAL NOT NULL );";
        database.execSQL(sql);
    }

    static void dropTable(Database database) {
        String sql = "DROP TABLE IF EXISTS " +
                "\"" + TABLENAME + "\"";
        database.execSQL(sql);
    }

    @Override
    protected void attachEntity(SeriesLimitInfo entity) {
        super.attachEntity(entity);
        entity.setDaoSession(daoSession);
    }

    @Override
    protected void bindValues(SQLiteStatement stmt, SeriesLimitInfo entity) {
        stmt.clearBindings();
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getSeriesId());
        stmt.bindDouble(3, entity.getMaxValue());
    }

    @Override
    protected void bindValues(DatabaseStatement stmt, SeriesLimitInfo entity) {
        stmt.clearBindings();
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getSeriesId());
        stmt.bindDouble(3, entity.getMaxValue());
    }

    @Override
    protected Long getKey(SeriesLimitInfo entity) {
        return entity != null ? entity.getId() : null;
    }

    @Override
    protected boolean hasKey(SeriesLimitInfo entity) {
        return entity.getId() != null;
    }

    @Override
    protected boolean isEntityUpdateable() {
        return true;
    }

    @Override
    protected SeriesLimitInfo readEntity(Cursor cursor, int offset) {
        Long id = null;
        if (!cursor.isNull(offset)) {
            id = cursor.getLong(offset);
        }
        return new SeriesLimitInfo(id, cursor.getLong(offset + 1), cursor.getDouble(offset + 2));
    }

    @Override
    protected void readEntity(Cursor cursor, SeriesLimitInfo entity, int offset) {
        Long id = null;
        if (!cursor.isNull(offset)) {
            id = cursor.getLong(offset);
        }
        entity.setId(id);
        entity.setSeriesId(cursor.getLong(offset + 1));
        entity.setMaxValue(cursor.getDouble(offset + 2));
    }

    @Override
    protected Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset) ? null : cursor.getLong(offset);
    }

    @Override
    protected Long updateKeyAfterInsert(SeriesLimitInfo entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }

    public static class Properties {
        public static final Property SERIES_ID = new Property(1, Long.TYPE, "seriesId", false, "series_id");
        public static final Property MAX_VALUE = new Property(2, Double.TYPE, "maxValue", false, "max_value");
        public static final Property ID = new Property(0, Long.class, "id", true, "id");
    }

}
