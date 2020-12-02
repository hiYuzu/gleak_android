package com.hb712.gleak_android.util;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间生成工具
 *
 * @author hiYuzu
 * @version V1.0
 * @date 2020/12/1 14:23
 */
public class DateUtil {
    public static final String DEFAULT_TIME = "yyyy-MM-dd HH:mm:ss";
    public static final String TIME_SERIES = "yyyyMMddHHmmss";

    /**
     * 获取通用格式的当前时间
     * @return yyyy-MM-dd HH:mm:ss
     */
    @SuppressLint("SimpleDateFormat")
    public static String getDefaultTime() {
        return new SimpleDateFormat(DEFAULT_TIME).format(new Date());
    }

    @SuppressLint("SimpleDateFormat")
    public static String getCurrentTime(String format) {
        return new SimpleDateFormat(format).format(new Date());
    }
}
