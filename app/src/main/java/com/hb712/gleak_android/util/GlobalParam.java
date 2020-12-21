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

    public static final String DEFAULT_IP = "192.168.3.126";
    public static final String DEFAULT_PORT = "3000";

    public static final String KEY_USERTOKEN = "Authorization";

    private static final String DEVICE_ROOT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "/EnpromiLDAR";
    public static final String VIDEO_RECORD_PATH = DEVICE_ROOT_PATH + File.separator + "/ijkplayer/video";
    public static final String SNAPSHOT_PATH = DEVICE_ROOT_PATH + File.separator + "ijkplayer/snapshot";
    public static final String LOG_PATH = DEVICE_ROOT_PATH + File.separator + "/log";

    public static boolean rememberPwd = false;

    public static final boolean IS_UPLOAD_POSITION = false;
    public static final String UPLOAD_POSITION_KEY = "is_upload_position";
    public static final String UPLOAD_DELAY_KEY = "upload_delay";
    public static final int UPLOAD_DELAY = 10;

    public static final double MIN_DISTANCE = 5.0;

    public static boolean isConnected = false;

    // 蓝牙状态
    public static final int STATE_NONE = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;

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

    @SuppressLint("AuthLeak")
    public static String VIDEO_URL = "rtsp://admin:tjtcb712@192.168.3.100:554/h264/ch1/main/av_stream";

    public static List<InitLeakData> initLeakData = new ArrayList<>();
}
