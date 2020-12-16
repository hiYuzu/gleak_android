package com.hb712.gleak_android.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import com.hb712.gleak_android.entity.FactorCoefficientInfo;

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
    public static final String TABLENAME = "factor_coefficient_info";

    public FactorCoefficientInfoDao(DaoConfig daoConfig) {
        super(daoConfig);
    }

    FactorCoefficientInfoDao(DaoConfig daoConfig, DaoSession daoSession) {
        super(daoConfig, daoSession);
    }

    static void createTable(Database database) {
        String sql = "CREATE TABLE IF NOT EXISTS " +
                "\"" + TABLENAME + "\" (\"id\" INTEGER PRIMARY KEY AUTOINCREMENT ,\"factor_name\" TEXT,\"cas\" TEXT,\"coefficient\" REAL NOT NULL ,\"molecule_value\" REAL NOT NULL );";
        database.execSQL(sql);
    }

    static void dropTable(Database database) {
        String sql = "DROP TABLE IF EXISTS " +
                "\"" + TABLENAME + "\"";
        database.execSQL(sql);
    }


    @Override
    protected void bindValues(SQLiteStatement stmt, FactorCoefficientInfo entity) {
        stmt.clearBindings();
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        String factorName = entity.getFactorName();
        if (factorName != null) {
            stmt.bindString(2, factorName);
        }
        String cas = entity.getCas();
        if (cas != null) {
            stmt.bindString(3, cas);
        }
        stmt.bindDouble(4, entity.getCoefficient());
        stmt.bindDouble(5, entity.getMoleculeValue());
    }

    @Override
    protected void bindValues(DatabaseStatement stmt, FactorCoefficientInfo entity) {
        stmt.clearBindings();
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        String factorName = entity.getFactorName();
        if (factorName != null) {
            stmt.bindString(2, (String) factorName);
        }
        String cas = entity.getCas();
        if (cas != null) {
            stmt.bindString(3, cas);
        }
        stmt.bindDouble(4, entity.getCoefficient());
        stmt.bindDouble(5, entity.getMoleculeValue());
    }

    @Override
    protected Long getKey(FactorCoefficientInfo entity) {
        return entity != null ? entity.getId() : null;
    }

    @Override
    protected boolean hasKey(FactorCoefficientInfo entity) {
        return entity.getId() != null;
    }

    @Override
    protected boolean isEntityUpdateable() {
        return true;
    }

    @Override
    protected FactorCoefficientInfo readEntity(Cursor cursor, int offset) {
        Long id = null;
        if (!cursor.isNull(offset)) {
            id = cursor.getLong(offset);
        }

        String factorName = null;
        if (!cursor.isNull(offset + 1)) {
            factorName = cursor.getString(offset + 1);
        }

        String cas = null;
        if (!cursor.isNull(offset + 2)) {
            cas = cursor.getString(offset + 2);
        }

        return new FactorCoefficientInfo(id, factorName, cas, cursor.getDouble(offset + 3), cursor.getDouble(offset + 4));
    }

    @Override
    protected void readEntity(Cursor cursor, FactorCoefficientInfo entity, int offset) {
        boolean bool = cursor.isNull(offset);
        Long id = null;
        if (!bool) {
            id = cursor.getLong(offset);
        }
        entity.setId(id);

        String factorName = null;
        if (!cursor.isNull(offset + 1)) {
            factorName = cursor.getString(offset + 1);
        }
        entity.setFactorName(factorName);

        String cas = null;
        if (!cursor.isNull(offset + 2)) {
            cas = cursor.getString(offset + 2);
        }
        entity.setCas(cas);
        entity.setCoefficient(cursor.getDouble(offset + 3));
        entity.setMoleculeValue(cursor.getDouble(offset + 4));
    }

    @Override
    protected Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset) ? null : cursor.getLong(offset);
    }

    @Override
    protected Long updateKeyAfterInsert(FactorCoefficientInfo entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }

    public static class Properties {
        public static final Property CAS = new Property(2, String.class, "cas", false, "cas");
        public static final Property COEFFICIENT = new Property(3, Double.TYPE, "coefficient", false, "coefficient");
        public static final Property FACTOR_NAME = new Property(1, String.class, "factorName", false, "factor_name");
        public static final Property MOLECULE_VALUE = new Property(4, Double.TYPE, "moleculeValue", false, "molecule_value");
        public static final Property ID = new Property(0, Long.class, "id", true, "id");
    }
}
