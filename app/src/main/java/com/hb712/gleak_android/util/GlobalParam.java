package com.hb712.gleak_android.util;

import android.annotation.SuppressLint;
import android.os.Environment;

import com.hb712.gleak_android.message.net.InitLeakData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/9/30 13:38
 */
public class GlobalParam {
    public static String SERVE_IP = "serve_ip";
    public static String SERVE_PORT = "serve_port";

    public static String DEFAULT_IP = "192.168.3.108";
    public static String DEFAULT_PORT = "3000";

    public static final String KEY_USERTOKEN = "Authorization";

    public static final String DEVICE_ROOT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "EnpromiLDAR";
    public static final String VIDEO_RECORD_PATH = DEVICE_ROOT_PATH + File.separator + "ijkplayer" + File.separator + "video";
    public static final String SNAPSHOT_PATH = DEVICE_ROOT_PATH + File.separator + "ijkplayer" + File.separator + "snapshot";
    public static final String LOG_PATH = DEVICE_ROOT_PATH + File.separator + "log";

    public static boolean rememberPwd = false;
    public static boolean isUploadPosition = true;
    public static int uploadDelay = 10000;
    public static boolean isConnected = false;
    public static boolean isFireOn = false;

    // 蓝牙状态
    public static final int STATE_NONE = 0;        // 无动作
    public static final int STATE_CONNECTING = 1;    // 正在连接
    public static final int STATE_CONNECTED = 2;    // 已连接

    //message what
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_DEVICE_NAME = 3;

    //request code
    public static final int REQUEST_CONNECT_DEVICE = 384;
    public static final int REQUEST_ENABLE_BT = 385;
    public static final int REQUEST_LEAK_DATA = 250;
    public static final int REQUEST_LOCATION_PERMISSION = 100;

    //其他
    public static final String DEVICE_NAME = "device_name";
    public static final String DEVICE_ADDRESS = "device_address";
    public static final boolean DEVICE_ANDROID = true;
    public static final boolean DEVICE_OTHER = false;

    //video
    public static boolean loadFirst = true;
    @SuppressLint("AuthLeak")
    public static String VIDEO_URL = "rtsp://admin:tjtcb712@192.168.3.100:554/h264/ch1/main/av_stream";

    public static final String MAP_KEY = "BpPnFERqEuxQtdvEuPp4eW8ouHv6LeKR";

    public static List<InitLeakData> initLeakData = new ArrayList<>();
}
