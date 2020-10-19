package com.hb712.gleak_android.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/10/19 10:56
 */
public class LogUtil {

    /**
     * debug
     * @param tag 标签
     * @param detail 描述
     */
    public static void debugOut(String tag, @NonNull String detail) {
        Log.d(tag, detail);
    }

    /**
     * info
     * @param tag 标签
     * @param ex 异常类型
     * @param detail 描述
     */
    public static void infoOut(String tag, @Nullable Exception ex, @NonNull String detail) {
        Log.i(tag, ex == null ? detail : ex.getMessage() + " " + detail);
    }

    /**
     * warn
     * @param tag 标签
     * @param ex 异常类型
     * @param detail 描述
     */
    public static void warnOut(String tag, @Nullable Exception ex, @NonNull String detail) {
        Log.w(tag, ex == null ? detail : ex.getMessage() + " " + detail);
    }

    /**
     * error
     * @param tag 标签
     * @param ex 异常类型
     * @param detail 描述
     */
    public static void errorOut(String tag, @Nullable Exception ex, @NonNull String detail) {
        Log.e(tag, ex == null ? detail : ex.getMessage() + " " + detail);
    }
}
