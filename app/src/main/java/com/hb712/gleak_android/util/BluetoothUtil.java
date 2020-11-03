package com.hb712.gleak_android.util;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.hb712.gleak_android.service.BluetoothService;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/9/25 10:56
 */
public class BluetoothUtil {
    private final String TAG = BluetoothUtil.class.getSimpleName();

    private BluetoothStateListener mBluetoothStateListener = null;
    private OnDataReceivedListener mDataReceivedListener = null;
    private BluetoothConnectionListener mBluetoothConnectionListener = null;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothService mChatService = null;
    private String mDeviceName = null;
    private String mDeviceAddress = null;
    private boolean isConnected = false;
    private boolean isConnecting = false;
    private boolean isAndroid = GlobalParam.DEVICE_ANDROID;

    private static BluetoothUtil instance;

    public static synchronized BluetoothUtil getInstance() {
        if (instance == null) {
            instance = new BluetoothUtil();
        }
        return instance;
    }

    //获取蓝牙adapter
    private BluetoothUtil() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public interface BluetoothStateListener {
        void onServiceStateChanged(int state);
    }

    public interface OnDataReceivedListener {
        void onDataReceived(byte[] data, String message);
    }

    public interface BluetoothConnectionListener {
        void onDeviceConnected(String name, String address);

        void onDeviceDisconnected();

        void onDeviceConnectionFailed();
    }

    @SuppressLint("HardwareIds")
    public boolean isBluetoothAvailable() {
        try {
            if (mBluetoothAdapter == null || mBluetoothAdapter.getAddress() == null)
                return false;
        } catch (NullPointerException e) {
            return false;
        }
        return true;
    }

    public boolean isBluetoothEnabled() {
        return mBluetoothAdapter.isEnabled();
    }

    public boolean isServiceAvailable() {
        return mChatService != null;
    }

    public void setupService() {
        mChatService = new BluetoothService(mHandler);
    }

    //开启蓝牙一直监听是否连接的状态
    public void startService(boolean isAndroid) {
        if (mChatService != null) {
            if (mChatService.getState() == GlobalParam.STATE_NONE) {
                mChatService.start(isAndroid);
                this.isAndroid = isAndroid;
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GlobalParam.MESSAGE_READ:
                    String message = "";
                    byte[] readBuf = (byte[]) msg.obj;
                    if (readBuf != null && readBuf.length > 0) {
                        if (mDataReceivedListener != null)
                            mDataReceivedListener.onDataReceived(readBuf, message);
                    }
                    break;
                case GlobalParam.MESSAGE_DEVICE_NAME:
                    mDeviceName = msg.getData().getString(GlobalParam.DEVICE_NAME);
                    mDeviceAddress = msg.getData().getString(GlobalParam.DEVICE_ADDRESS);
                    if (mBluetoothConnectionListener != null)
                        mBluetoothConnectionListener.onDeviceConnected(mDeviceName, mDeviceAddress);
                    isConnected = true;
                    break;
                case GlobalParam.MESSAGE_STATE_CHANGE:
                    if (mBluetoothStateListener != null)
                        mBluetoothStateListener.onServiceStateChanged(msg.arg1);
                    if (isConnected && msg.arg1 != GlobalParam.STATE_CONNECTED) {
                        if (mBluetoothConnectionListener != null) {
                            mBluetoothConnectionListener.onDeviceDisconnected();
                        }
                        isConnected = false;
                        mDeviceName = null;
                        mDeviceAddress = null;
                    }
                    if (!isConnecting && msg.arg1 == GlobalParam.STATE_CONNECTING) {
                        isConnecting = true;
                    } else if (isConnecting) {
                        if (msg.arg1 != GlobalParam.STATE_CONNECTED) {
                            if (mBluetoothConnectionListener != null)
                                mBluetoothConnectionListener.onDeviceConnectionFailed();
                        }
                        isConnecting = false;
                    }
                    break;
            }
        }
    };

    public void connect(Intent data) {
        Bundle bundle = data.getExtras();
        if (bundle == null) {
            LogUtil.debugOut(TAG, "无参数，连接失败");
            return;
        }
        String address = bundle.getString(GlobalParam.DEVICE_ADDRESS);
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        mChatService.connect(device);
    }

    public void disconnect() {
        if (mChatService != null) {
            mChatService.stop();
            if (mChatService.getState() == GlobalParam.STATE_NONE) {
                mChatService.start(BluetoothUtil.this.isAndroid);
            }
        }
    }

    public void setOnDataReceivedListener(OnDataReceivedListener listener) {
        mDataReceivedListener = listener;
    }

    public void setBluetoothConnectionListener(BluetoothConnectionListener listener) {
        mBluetoothConnectionListener = listener;
    }

    public void send(byte[] data) {
        int length = data.length;
        byte[] sendMsg = new byte[length];
        mChatService.write(sendMsg);
    }
}