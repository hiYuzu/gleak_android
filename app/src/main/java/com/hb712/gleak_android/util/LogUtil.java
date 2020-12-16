package com.hb712.gleak_android.util;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/10/19 10:56
 */
public class LogUtil {
    private static final String TAG = LogUtil.class.getSimpleName();
    public static String logFilePath;

    private static final char DEBUG = 'D';
    private static final char INFO = 'I';
    private static final char WARN = 'W';
    private static final char ERROR = 'E';

    private static boolean isSave = false;

    public static void initDirectory() {
        logFilePath = GlobalParam.LOG_PATH + File.separator + "log-" + DateUtil.getCurrentTime(DateUtil.DATE_SERIES) + ".txt";
        File logFileDir = new File(GlobalParam.LOG_PATH);
        if (logFileDir.exists()) {
            isSave = true;
            return;
        }
        if (!logFileDir.mkdirs()) {
            Log.w(TAG,"日志文件夹创建失败");
        }
        try {
            File file = new File(logFilePath);
            if (file.createNewFile()) {
                isSave = true;
            }
        } catch (IOException ioe) {
            Log.w(TAG, "创建日志文件失败");
        }
    }

    /**
     * debug
     *
     * @param tag    标签
     * @param detail 描述
     */
    public static void debugOut(String tag, @NonNull String detail) {
        Log.d(tag, detail);
        if (isSave) {
            writeToFile(DEBUG, tag, detail);
        }
    }

    /**
     * info
     *
     * @param tag    标签
     * @param detail 描述
     */
    public static void infoOut(String tag, String detail) {
        Log.i(tag, detail);
        ToastUtil.shortInstanceToast(detail);
        if (isSave) {
            writeToFile(INFO, tag, detail);
        }
    }

    /**
     * warn
     *
     * @param tag    标签
     * @param e     异常类型
     * @param detail 描述
     */
    public static void warnOut(String tag, Exception e, String detail) {
        StringBuilder sb = new StringBuilder();
        if (e != null) {
            sb.append(e.getMessage());
        }
        if (detail != null && !detail.isEmpty()) {
            sb.append(", detail: ").append(detail);
        }
        String msg = sb.toString();
        Log.w(tag, msg);
        if (isSave) {
            writeToFile(WARN, tag, msg);
        }
    }

    /**
     * error
     *
     * @param tag    标签
     * @param e     异常类型
     * @param detail 描述
     */
    public static void errorOut(String tag, Exception e, String detail) {
        StringBuilder sb = new StringBuilder();
        if (e != null) {
            sb.append(e.getMessage());
        }
        if (detail != null && !detail.isEmpty()) {
            sb.append(", detail: ").append(detail);
        }
        String msg = sb.toString();
        Log.e(tag, msg);
        if (isSave) {
            writeToFile(ERROR, tag, msg);
        }
    }

    private static void writeToFile(Character type, String tag, String msg) {
        String log = DateUtil.getCurrentTime(DateUtil.HP_TIME) + " " + type + "/" + tag + ": " + msg + "\n";
        FileOutputStream fos = null;
        BufferedWriter bw = null;
        try {
            fos = new FileOutputStream(logFilePath, true);
            bw = new BufferedWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8));
            bw.write(log);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException ioe) {
                Log.e(TAG, "buffered writer 关闭失败", ioe);
            }
        }
    }
}
