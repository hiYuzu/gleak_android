package com.hb712.gleak_android.util;

import android.annotation.SuppressLint;
import android.support.annotation.Nullable;

import java.text.ParseException;
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
    public static final String HP_TIME = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String LP_TIME = "yyyy-MM-dd";
    public static final String TIME_SERIES = "yyyyMMddHHmmss";
    public static final String DATE_SERIES = "yyyyMMdd";

    /**
     * 获取默认格式 {@link #DEFAULT_TIME} 的当前时间
     *
     * @return yyyy-MM-dd HH:mm:ss
     */
    @SuppressLint("SimpleDateFormat")
    public static String getDefaultTime() {
        return new SimpleDateFormat(DEFAULT_TIME).format(new Date());
    }

    /**
     * 获取指定格式的当前时间
     * @param format 时间格式
     * @return {@link String}
     */
    @SuppressLint("SimpleDateFormat")
    public static String getCurrentTime(String format) {
        return new SimpleDateFormat(format).format(new Date());
    }

    /**
     * 获取指定时间的指定格式
     * @param date 源时间
     * @param format 时间格式
     * @return {@link String}
     */
    @SuppressLint("SimpleDateFormat")
    public static String getDateString(Date date, String format) {
        return new SimpleDateFormat(format).format(date);
    }

    /**
     * 将字符串格式的日期转为 {@link Date} 格式
     * @param dateStr 日期字符串
     * @param format 时间格式
     * @return {@link Date}
     */
    @SuppressLint("SimpleDateFormat")
    @Nullable
    public static Date parseDate(String dateStr, String format) {
        Date date = null;
        try {
            date = new SimpleDateFormat(format).parse(dateStr);
        } catch (ParseException pe) {
            ToastUtil.longToastShow("日期格式有误！");
        }
        return null;
    }
}
