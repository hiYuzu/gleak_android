package com.hb712.gleak_android.util;

import android.annotation.SuppressLint;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/9/30 13:38
 */
public class GlobalParam {
    public static boolean rememberPwd = false;
    public static boolean isConnected = false;
    public static boolean isFireOn = false;

    // 蓝牙状态
    public static final int STATE_NONE = 0;       	// 无动作
    public static final int STATE_CONNECTING = 1; 	// 正在连接
    public static final int STATE_CONNECTED = 2;  	// 已连接

    //message what
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_DEVICE_NAME = 3;

    //request code
    public static final int REQUEST_CONNECT_DEVICE = 384;
    public static final int REQUEST_ENABLE_BT = 385;

    //其他
    public static final String DEVICE_NAME = "device_name";
    public static final String DEVICE_ADDRESS = "device_address";
    public static final boolean DEVICE_ANDROID = false;

    //video
    public static boolean loadFirst = true;
    @SuppressLint("AuthLeak")
    public static String VIDEO_URL = "rtsp://admin:tjtcb712@192.168.1.100:554/h264/ch1/main/av_stream";
}
