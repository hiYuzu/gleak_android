package com.hb712.gleak_android.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import com.hb712.gleak_android.entity.SeriesInfo;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.internal.DaoConfig;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/10/22 9:52
 */
public class SeriesInfoDao extends AbstractDao<SeriesInfo, Long> {
    public static final String TABLENAME = "series_info";
    private static final String TAG = SeriesInfoDao.class.getSimpleName();

    public SeriesInfoDao(DaoConfig daoConfig) {
        super(daoConfig);
    }

    SeriesInfoDao(DaoConfig daoConfig, DaoSession daoSession) {
        super(daoConfig, daoSession);
    }

    public static void createTable(Database database) {
        String sql = "CREATE TABLE IF NOT EXISTS " +
                "\"" + TABLENAME + "\" (\"id\" INTEGER PRIMARY KEY AUTOINCREMENT ,\"series_name\" TEXT,\"std_series\" INTEGER NOT NULL );";
        database.execSQL(sql);
    }

    public static void dropTable(Database database) {
        String sql = "DROP TABLE IF EXISTS " +
                "\"" + TABLENAME + "\"";
        database.execSQL(sql);
    }

    @Override
    protected void bindValues(SQLiteStatement stmt, SeriesInfo entity) {
        stmt.clearBindings();
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        String seriesName = entity.getSeriesName();
        if (seriesName != null) {
            stmt.bindString(2, seriesName);
        }
        if (entity.isStdSeries()) {
            stmt.bindLong(3, 1);
        } else {
            stmt.bindLong(3, 0);
        }
    }

    @Override
    protected void bindValues(DatabaseStatement stmt, SeriesInfo entity) {
        stmt.clearBindings();
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        String seriesName = entity.getSeriesName();
        if (seriesName != null) {
            stmt.bindString(2, seriesName);
        }
        if (entity.isStdSeries()) {
            stmt.bindLong(3, 1);
        } else {
            stmt.bindLong(3, 0);
        }
    }

    @Override
    protected Long getKey(SeriesInfo entity) {
        return entity != null ? entity.getId() : null;
    }

    @Override
    protected boolean hasKey(SeriesInfo entity) {
        return entity.getId() != null;
    }

    @Override
    protected boolean isEntityUpdateable() {
        return true;
    }

    @Override
    protected SeriesInfo readEntity(Cursor cursor, int offset) {
        Long id = null;
        if (!cursor.isNull(offset)) {
            id = cursor.getLong(offset);
        }
        String seriesName = null;
        if (!cursor.isNull(offset + 1)) {
            seriesName = cursor.getString(offset + 1);
        }
        boolean stdSeries;
        stdSeries = cursor.getShort(offset + 2) != 0;
        return new SeriesInfo(id, seriesName, stdSeries);
    }


    @Override
    protected void readEntity(Cursor cursor, SeriesInfo entity, int offset) {
        Long id = null;
        if (!cursor.isNull(offset)) {
            id = cursor.getLong(offset);
        }
        entity.setId(id);

        String seriesName = null;
        if (!cursor.isNull(offset + 1)) {
            seriesName = cursor.getString(offset + 1);
        }
        entity.setSeriesName(seriesName);

        entity.setStdSeries(cursor.getShort(offset + 2) != 0);
    }

    @Override
    protected Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset) ? null : cursor.getLong(offset);
    }

    @Override
    protected Long updateKeyAfterInsert(SeriesInfo entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }

    public static class Properties {
        public static final Property seriesName = new Property(1, String.class, "seriesName", false, "series_name");
        public static final Property stdSeries = new Property(2, Boolean.TYPE, "stdSeries", false, "std_series");
        public static final Property id = new Property(0, Long.class, "id", true, "id");
    }
}
