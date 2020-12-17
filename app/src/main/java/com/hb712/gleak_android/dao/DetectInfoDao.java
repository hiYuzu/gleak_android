package com.hb712.gleak_android.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import com.hb712.gleak_android.entity.DetectInfo;
import com.hb712.gleak_android.entity.SeriesInfo;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.internal.DaoConfig;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/12/16 14:55
 */
public class DetectInfoDao extends AbstractDao<DetectInfo, Long> {
    public static final String TABLENAME = "detect_info";

    public DetectInfoDao(DaoConfig config) {
        super(config);
    }

    DetectInfoDao(DaoConfig config, AbstractDaoSession daoSession) {
        super(config, daoSession);
    }

    public static void createTable(Database database) {
        String sql = "CREATE TABLE IF NOT EXISTS " +
                "\"" + TABLENAME + "\" (\"id\" INTEGER PRIMARY KEY AUTOINCREMENT ,\"leak_name\" TEXT,\"monitor_value\" REAL NOT NULL,\"monitor_time\" TEXT,\"standard\" INTEGER NOT NULL,\"video_path\" TEXT,\"opt_user\" INTEGER );";
        database.execSQL(sql);
    }

    public static void dropTable(Database database) {
        String sql = "DROP TABLE IF EXISTS " +
                "\"" + TABLENAME + "\"";
        database.execSQL(sql);
    }

    @Override
    protected void bindValues(SQLiteStatement stmt, DetectInfo entity) {
        stmt.clearBindings();
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        String leakName = entity.getLeakName();
        if (leakName != null) {
            stmt.bindString(2, leakName);
        }
        stmt.bindDouble(3, entity.getMonitorValue());
        String monitorTime = entity.getMonitorTime();
        if (monitorTime != null) {
            stmt.bindString(4, monitorTime);
        }
        if (entity.isStandard()) {
            stmt.bindLong(5, 1);
        } else {
            stmt.bindLong(5, 0);
        }
        String videoPath = entity.getVideoPath();
        if (videoPath != null) {
            stmt.bindString(6, videoPath);
        }
        Long optUser = entity.getOptUser();
        if (optUser != null) {
            stmt.bindLong(7, optUser);
        }
    }

    @Override
    protected void bindValues(DatabaseStatement stmt, DetectInfo entity) {
        stmt.clearBindings();
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        String leakName = entity.getLeakName();
        if (leakName != null) {
            stmt.bindString(2, leakName);
        }
        stmt.bindDouble(3, entity.getMonitorValue());
        String monitorTime = entity.getMonitorTime();
        if (monitorTime != null) {
            stmt.bindString(4, monitorTime);
        }
        if (entity.isStandard()) {
            stmt.bindLong(5, 1);
        } else {
            stmt.bindLong(5, 0);
        }
        String videoPath = entity.getVideoPath();
        if (videoPath != null) {
            stmt.bindString(6, videoPath);
        }
        Long optUser = entity.getOptUser();
        if (optUser != null) {
            stmt.bindLong(7, optUser);
        }
    }

    @Override
    protected Long getKey(DetectInfo entity) {
        return entity != null ? entity.getId() : null;
    }

    @Override
    protected boolean hasKey(DetectInfo entity) {
        return entity.getId() != null;
    }

    @Override
    protected boolean isEntityUpdateable() {
        return true;
    }

    @Override
    protected DetectInfo readEntity(Cursor cursor, int offset) {
        Long id = null;
        if (!cursor.isNull(offset)) {
            id = cursor.getLong(offset);
        }
        String leakName = null;
        if (!cursor.isNull(offset + 1)) {
            leakName = cursor.getString(offset + 1);
        }
        String monitorTime = null;
        if (!cursor.isNull(offset + 3)) {
            monitorTime = cursor.getString(offset + 3);
        }
        boolean standard;
        standard = cursor.getShort(offset + 4) != 0;
        String videoPath = null;
        if (!cursor.isNull(offset + 5)) {
            videoPath = cursor.getString(offset + 5);
        }
        Long optUser = null;
        if (!cursor.isNull(offset + 6)) {
            optUser = cursor.getLong(offset + 6);
        }
        return new DetectInfo(id, leakName, cursor.getDouble(offset + 2), monitorTime, standard, videoPath, optUser);
    }

    @Override
    protected void readEntity(Cursor cursor, DetectInfo entity, int offset) {
        Long id = null;
        if (!cursor.isNull(offset)) {
            id = cursor.getLong(offset);
        }
        entity.setId(id);
        String leakName = null;
        if (!cursor.isNull(offset + 1)) {
            leakName = cursor.getString(offset + 1);
        }
        entity.setLeakName(leakName);
        entity.setMonitorValue(cursor.getDouble(offset + 2));
        String monitorTime = null;
        if (!cursor.isNull(offset + 3)) {
            monitorTime = cursor.getString(offset + 3);
        }
        entity.setMonitorTime(monitorTime);
        entity.setStandard(cursor.getShort(offset + 4) != 0);
        String videoPath = null;
        if (!cursor.isNull(offset + 5)) {
            videoPath = cursor.getString(offset + 5);
        }
        entity.setVideoPath(videoPath);
        Long optUser = null;
        if (!cursor.isNull(offset + 6)) {
            optUser = cursor.getLong(offset + 6);
        }
        entity.setOptUser(optUser);
    }

    @Override
    protected Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset) ? null : cursor.getLong(offset);
    }

    @Override
    protected Long updateKeyAfterInsert(DetectInfo entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }

    public static class Properties {
        public static final Property ID = new Property(0, Long.class, "id", true, "id");
        public static final Property LEAK_NAME = new Property(1, String.class, "leakName", false, "leak_name");
        public static final Property MONITOR_VALUE = new Property(2, Double.TYPE, "monitorValue", false, "monitor_value");
        public static final Property MONITOR_TIME = new Property(3, String.class, "monitorTime", false, "monitor_time");
        public static final Property STANDARD = new Property(4, Boolean.TYPE, "standard", false, "standard");
        public static final Property VIDEO_PATH = new Property(5, String.class, "videoPath", false, "video_path");
        public static final Property OPT_USER = new Property(6, Long.class, "optUser", false, "opt_user");
    }
}
