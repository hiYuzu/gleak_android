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
    private static final String TABLE_NAME = "series_limit_info";
    private DaoSession daoSession;
    private String selectDeep;

    public SeriesLimitInfoDao(DaoConfig daoConfig) {
        super(daoConfig);
    }

    SeriesLimitInfoDao(DaoConfig daoConfig, DaoSession paramDaoSession) {
        super(daoConfig, paramDaoSession);
        this.daoSession = paramDaoSession;
    }

    static void createTable(Database database, boolean paramBoolean) {
        String str;
        if (paramBoolean) {
            str = "IF NOT EXISTS ";
        } else {
            str = "";
        }
        String sql = "CREATE TABLE " +
                str +
                "\"" + TABLE_NAME + "\" (\"id\" INTEGER PRIMARY KEY AUTOINCREMENT ,\"series_id\" INTEGER NOT NULL ,\"max_value\" REAL NOT NULL );";
        database.execSQL(sql);
    }

    static void dropTable(Database database, boolean paramBoolean) {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("DROP TABLE ");
        String str;
        if (paramBoolean) {
            str = "IF EXISTS ";
        } else {
            str = "";
        }
        localStringBuilder.append(str);
        localStringBuilder.append("\"" + TABLE_NAME + "\"");
        database.execSQL(localStringBuilder.toString());
    }

    protected final void attachEntity(SeriesLimitInfo seriesLimitInfo) {
        super.attachEntity(seriesLimitInfo);
        seriesLimitInfo.setDaoSession(daoSession);
    }

    protected final void bindValues(SQLiteStatement sqLiteStatement, SeriesLimitInfo seriesLimitInfo) {
        sqLiteStatement.clearBindings();
        Long id = seriesLimitInfo.getId();
        if (id != null) {
            sqLiteStatement.bindLong(1, id);
        }
        sqLiteStatement.bindLong(2, seriesLimitInfo.getSeriesId());
        sqLiteStatement.bindDouble(3, seriesLimitInfo.getMaxValue());
    }

    protected final void bindValues(DatabaseStatement databaseStatement, SeriesLimitInfo seriesLimitInfo) {
        databaseStatement.clearBindings();
        Long id = seriesLimitInfo.getId();
        if (id != null) {
            databaseStatement.bindLong(1, id);
        }
        databaseStatement.bindLong(2, seriesLimitInfo.getSeriesId());
        databaseStatement.bindDouble(3, seriesLimitInfo.getMaxValue());
    }

    public Long getKey(SeriesLimitInfo seriesLimitInfo) {
        return seriesLimitInfo != null ? seriesLimitInfo.getId() : null;
    }

    public boolean hasKey(SeriesLimitInfo seriesLimitInfo) {
        return seriesLimitInfo.getId() != null;
    }

    protected final boolean isEntityUpdateable() {
        return true;
    }

    public SeriesLimitInfo readEntity(Cursor cursor, int offset) {
        Long id = null;
        if (!cursor.isNull(offset)) {
            id = cursor.getLong(offset);
        }
        return new SeriesLimitInfo(id, cursor.getLong(offset + 1), cursor.getDouble(offset + 2));
    }

    public void readEntity(Cursor cursor, SeriesLimitInfo seriesLimitInfo, int offset) {
        Long id = null;
        if (!cursor.isNull(offset)) {
            id = cursor.getLong(offset);
        }
        seriesLimitInfo.setId(id);
        seriesLimitInfo.setSeriesId(cursor.getLong(offset + 1));
        seriesLimitInfo.setMaxValue(cursor.getDouble(offset + 2));
    }

    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset) ? null : cursor.getLong(offset);
    }

    protected final Long updateKeyAfterInsert(SeriesLimitInfo seriesLimitInfo, long rowId) {
        seriesLimitInfo.setId(rowId);
        return rowId;
    }

    public static class Properties {
        public static final Property seriesId = new Property(1, Long.TYPE, "seriesId", false, "series_id");
        public static final Property maxValue = new Property(2, Double.TYPE, "maxValue", false, "max_value");
        public static final Property id = new Property(0, Long.class, "id", true, "id");
    }

}
