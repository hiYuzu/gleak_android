package com.hb712.gleak_android.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

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
    public static void infoOut(@Nullable Context context, String tag, @Nullable Exception ex, @NonNull String detail) {
        Log.i(tag, ex == null ? detail : ex.getMessage() + ", detail: " + detail);
        if (context != null) {
            Toast.makeText(context, detail, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * warn
     * @param tag 标签
     * @param ex 异常类型
     * @param detail 描述
     */
    public static void warnOut(String tag, @Nullable Exception ex, @NonNull String detail) {
        if (ex != null) {
            Log.e(tag, ex.getMessage() + ", detail: " + detail);
        }
        Log.w(tag, detail);
    }

    /**
     * error
     * @param tag 标签
     * @param ex 异常类型
     * @param detail 描述
     */
    public static void errorOut(String tag, @Nullable Exception ex, @NonNull String detail) {
        Log.e(tag, ex == null ? detail : ex.getMessage() + ", detail: " + detail);
    }
}
