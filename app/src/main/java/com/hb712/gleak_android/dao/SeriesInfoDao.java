package com.hb712.gleak_android.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import com.hb712.gleak_android.pojo.SeriesInfo;

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
    private static final String TABLE_NAME = "series_info";
    private static final String TAG = SeriesInfoDao.class.getSimpleName();

    public SeriesInfoDao(DaoConfig daoConfig) {
        super(daoConfig);
    }

    SeriesInfoDao(DaoConfig daoConfig, DaoSession daoSession) {
        super(daoConfig, daoSession);
    }

    public static void createTable(Database database, boolean paramBoolean) {
        String str;
        if (paramBoolean) {
            str = "IF NOT EXISTS ";
        } else {
            str = "";
        }
        String sql = "CREATE TABLE " +
                str +
                "\"" + TABLE_NAME + "\" (\"id\" INTEGER PRIMARY KEY AUTOINCREMENT ,\"series_name\" TEXT,\"std_series\" INTEGER NOT NULL );";
        database.execSQL(sql);
    }

    public static void dropTable(Database database, boolean paramBoolean) {
        String str;
        if (paramBoolean) {
            str = "IF EXISTS ";
        } else {
            str = "";
        }
        String sql = "DROP TABLE " +
                str +
                "\"" + TABLE_NAME + "\"";
        database.execSQL(sql);
    }

    protected final void bindValues(SQLiteStatement sqLiteStatement, SeriesInfo seriesInfo) {
        sqLiteStatement.clearBindings();
        Long id = seriesInfo.getId();
        if (id != null) {
            sqLiteStatement.bindLong(1, id);
        }
        String seriesName = seriesInfo.getSeriesName();
        if (seriesName != null) {
            sqLiteStatement.bindString(2, seriesName);
        }
        if (seriesInfo.isStdSeries()) {
            sqLiteStatement.bindLong(3, 1);
        } else {
            sqLiteStatement.bindLong(3, 0);
        }

    }

    protected final void bindValues(DatabaseStatement databaseStatement, SeriesInfo seriesInfo) {
        databaseStatement.clearBindings();
        Long id = seriesInfo.getId();
        if (id != null) {
            databaseStatement.bindLong(1, id);
        }
        String seriesName = seriesInfo.getSeriesName();
        if (seriesName != null) {
            databaseStatement.bindString(2, seriesName);
        }
        if (seriesInfo.isStdSeries()) {
            databaseStatement.bindLong(3, 1);
        } else {
            databaseStatement.bindLong(3, 0);
        }
    }

    public Long getKey(SeriesInfo seriesInfo) {
        return seriesInfo != null ? seriesInfo.getId() : null;
    }

    public boolean hasKey(SeriesInfo seriesInfo) {
        return seriesInfo.getId() != null;
    }

    protected final boolean isEntityUpdateable() {
        return true;
    }

    public SeriesInfo readEntity(Cursor cursor, int offset) {
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

    public void readEntity(Cursor cursor, SeriesInfo seriesInfo, int offset) {
        Long id = null;
        if (cursor.isNull(offset)) {
            id = cursor.getLong(offset);
        }
        seriesInfo.setId(id);
        String seriesName = null;
        if (!cursor.isNull(offset + 1)) {
            seriesName = cursor.getString(offset + 1);
        }
        seriesInfo.setSeriesName(seriesName);
        seriesInfo.setStdSeries(cursor.getShort(offset + 2) != 0);
    }

    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset) ? null : cursor.getLong(offset);
    }

    protected final Long updateKeyAfterInsert(SeriesInfo seriesInfo, long rowId) {
        seriesInfo.setId(rowId);
        return rowId;
    }

    public static class Properties {
        public static final Property seriesName = new Property(1, String.class, "seriesName", false, "series_name");
        public static final Property stdSeries = new Property(2, Boolean.TYPE, "stdSeries", false, "std_series");
        public static final Property id = new Property(0, Long.class, "id", true, "id");
    }
}
