package com.hb712.gleak_android.dao;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/10/21 14:23
 */
public class DBManager {
    private static final String DB_NAME = "LEAK_DATA.db";
    private static final String TAG = DBManager.class.getSimpleName();
    @SuppressLint("StaticFieldLeak")
    private static DBManager instance;
    private Context context;
    private DaoMaster.DevOpenHelper openHelper;

    public static synchronized DBManager getInstance() {
        if (instance == null) {
            instance = new DBManager();
        }
        return instance;
    }

    private SQLiteDatabase getReadableDatabase() {
        if (openHelper == null) {
            openHelper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
        }
        return openHelper.getReadableDatabase();
    }

    public DaoSession getReadableSession() {
        return new DaoMaster(getReadableDatabase()).newSession();
    }

    private SQLiteDatabase getWritableDatabase() {
        if (openHelper == null) {
            openHelper = new DaoMaster.DevOpenHelper(this.context, DB_NAME, null);
        }
        return openHelper.getWritableDatabase();
    }

    public DaoSession getWritableSession() {
        return new DaoMaster(getWritableDatabase()).newSession();
    }

    public void init(Context paramContext) {
        this.context = paramContext;
        this.openHelper = new DaoMaster.DevOpenHelper(paramContext, DB_NAME, null);
    }
}
