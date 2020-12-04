package com.hb712.gleak_android.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.SharedPreferencesCompat;

/**
 * 本地缓存工具类
 *
 * @author hiYuzu
 * @version V1.0
 * @date 2020/10/21 11:31
 */
public class SPUtil {
    private static final String SP_NAME = "enpromi_ldar";

    /**
     * 取
     * @param context context
     * @param key 键
     * @param defValue 默认值
     * @return 存入的值或默认值
     */
    public static Object get(Context context, String key, Object defValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        if (defValue instanceof String) {
            return sharedPreferences.getString(key, (String) defValue);
        } else if (defValue instanceof Integer) {
            return sharedPreferences.getInt(key, (Integer) defValue);
        } else if (defValue instanceof Boolean) {
            return sharedPreferences.getBoolean(key, (Boolean) defValue);
        } else if (defValue instanceof Float) {
            return sharedPreferences.getFloat(key, (Float) defValue);
        } else {
            return sharedPreferences.getLong(key, (Long) defValue);
        }
    }

    /**
     * 存
     * @param context context
     * @param key 键
     * @param value 值
     */
    public static void put(Context context, String key, Object value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit();
        if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else {
            editor.putString(key, value.toString());
        }
        editor.apply();
    }

    /**
     * 删除
     * @param context context
     * @param key 键
     */
    public static void remove(Context context, String key) {
        SharedPreferences.Editor editor = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit();
        editor.remove(key);
        editor.apply();
    }
}
