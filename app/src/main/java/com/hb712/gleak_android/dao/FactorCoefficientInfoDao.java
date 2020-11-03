package com.hb712.gleak_android.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import com.hb712.gleak_android.pojo.FactorCoefficientInfo;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.internal.DaoConfig;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/10/21 15:17
 */
public class FactorCoefficientInfoDao extends AbstractDao<FactorCoefficientInfo, Long> {
    private static final String TABLE_NAME = "factor_coefficient_info";

    public FactorCoefficientInfoDao(DaoConfig paramDaoConfig) {
        super(paramDaoConfig);
    }

    FactorCoefficientInfoDao(DaoConfig paramDaoConfig, DaoSession paramDaoSession) {
        super(paramDaoConfig, paramDaoSession);
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
                "\"" + TABLE_NAME + "\" (\"id\" INTEGER PRIMARY KEY AUTOINCREMENT ,\"factor_name\" TEXT,\"cas\" TEXT,\"coefficient\" REAL NOT NULL ,\"molecule_value\" REAL NOT NULL );";
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

    protected final void bindValues(SQLiteStatement paramSQLiteStatement, FactorCoefficientInfo paramFactorCoefficientInfo) {
        paramSQLiteStatement.clearBindings();
        Object localObject = paramFactorCoefficientInfo.getId();
        if (localObject != null) {
            paramSQLiteStatement.bindLong(1, (Long) localObject);
        }
        localObject = paramFactorCoefficientInfo.getFactorName();
        if (localObject != null) {
            paramSQLiteStatement.bindString(2, (String) localObject);
        }
        localObject = paramFactorCoefficientInfo.getCas();
        if (localObject != null) {
            paramSQLiteStatement.bindString(3, (String) localObject);
        }
        paramSQLiteStatement.bindDouble(4, paramFactorCoefficientInfo.getCoefficient());
        paramSQLiteStatement.bindDouble(5, paramFactorCoefficientInfo.getMoleculeValue());
    }

    protected final void bindValues(DatabaseStatement paramDatabaseStatement, FactorCoefficientInfo paramFactorCoefficientInfo) {
        paramDatabaseStatement.clearBindings();
        Object localObject = paramFactorCoefficientInfo.getId();
        if (localObject != null) {
            paramDatabaseStatement.bindLong(1, (Long) localObject);
        }
        localObject = paramFactorCoefficientInfo.getFactorName();
        if (localObject != null) {
            paramDatabaseStatement.bindString(2, (String) localObject);
        }
        localObject = paramFactorCoefficientInfo.getCas();
        if (localObject != null) {
            paramDatabaseStatement.bindString(3, (String) localObject);
        }
        paramDatabaseStatement.bindDouble(4, paramFactorCoefficientInfo.getCoefficient());
        paramDatabaseStatement.bindDouble(5, paramFactorCoefficientInfo.getMoleculeValue());
    }

    public Long getKey(FactorCoefficientInfo paramFactorCoefficientInfo) {
        if (paramFactorCoefficientInfo != null) {
            return paramFactorCoefficientInfo.getId();
        }
        return null;
    }

    public boolean hasKey(FactorCoefficientInfo paramFactorCoefficientInfo) {
        return paramFactorCoefficientInfo.getId() != null;
    }

    protected final boolean isEntityUpdateable() {
        return true;
    }

    public FactorCoefficientInfo readEntity(Cursor paramCursor, int paramInt) {
        Long localLong;
        if (paramCursor.isNull(paramInt)) {
            localLong = null;
        } else {
            localLong = paramCursor.getLong(paramInt);
        }
        String str1;
        if (paramCursor.isNull(paramInt + 1)) {
            str1 = null;
        } else {
            str1 = paramCursor.getString(paramInt + 1);
        }
        String str2;
        if (paramCursor.isNull(paramInt + 2)) {
            str2 = null;
        } else {
            str2 = paramCursor.getString(paramInt + 2);
        }
        return new FactorCoefficientInfo(localLong, str1, str2, paramCursor.getDouble(paramInt + 3), paramCursor.getDouble(paramInt + 4));
    }

    public void readEntity(Cursor paramCursor, FactorCoefficientInfo paramFactorCoefficientInfo, int paramInt) {
        boolean bool = paramCursor.isNull(paramInt);
        Object localObject1;
        if (bool) {
            localObject1 = null;
        } else {
            localObject1 = paramCursor.getLong(paramInt);
        }
        paramFactorCoefficientInfo.setId((Long) localObject1);
        if (paramCursor.isNull(paramInt + 1)) {
            localObject1 = null;
        } else {
            localObject1 = paramCursor.getString(paramInt + 1);
        }
        paramFactorCoefficientInfo.setFactorName((String) localObject1);
        if (paramCursor.isNull(paramInt + 2)) {
            localObject1 = null;
        } else {
            localObject1 = paramCursor.getString(paramInt + 2);
        }
        paramFactorCoefficientInfo.setCas((String) localObject1);
        paramFactorCoefficientInfo.setCoefficient(paramCursor.getDouble(paramInt + 3));
        paramFactorCoefficientInfo.setMoleculeValue(paramCursor.getDouble(paramInt + 4));
    }

    public Long readKey(Cursor paramCursor, int paramInt) {
        if (paramCursor.isNull(paramInt)) {
            return null;
        }
        return paramCursor.getLong(paramInt);
    }

    protected final Long updateKeyAfterInsert(FactorCoefficientInfo paramFactorCoefficientInfo, long paramLong) {
        paramFactorCoefficientInfo.setId(paramLong);
        return paramLong;
    }

    public static class Properties {
        public static final Property cas = new Property(2, String.class, "cas", false, "cas");
        public static final Property coefficient = new Property(3, Double.TYPE, "coefficient", false, "coefficient");
        public static final Property factorName = new Property(1, String.class, "factorName", false, "factor_name");
        public static final Property moleculeValue = new Property(4, Double.TYPE, "moleculeValue", false, "molecule_value");
        public static final Property id = new Property(0, Long.class, "id", true, "id");
    }
}
