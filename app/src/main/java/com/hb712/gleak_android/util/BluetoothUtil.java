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

    private OnDataReceivedListener mDataReceivedListener = null;
    private BluetoothConnectionListener mBluetoothConnectionListener = null;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothService mBluetoothService = null;
    private boolean isConnected = false;
    private boolean isConnecting = false;

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
        return mBluetoothService != null;
    }

    public void setupService() {
        mBluetoothService = new BluetoothService(mHandler);
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
                    String mDeviceName = msg.getData().getString(GlobalParam.DEVICE_NAME);
                    String mDeviceAddress = msg.getData().getString(GlobalParam.DEVICE_ADDRESS);
                    if (mBluetoothConnectionListener != null)
                        mBluetoothConnectionListener.onDeviceConnected(mDeviceName, mDeviceAddress);
                    isConnected = true;
                    break;
                case GlobalParam.MESSAGE_STATE_CHANGE:
                    if (isConnected && msg.arg1 != GlobalParam.STATE_CONNECTED) {
                        if (mBluetoothConnectionListener != null) {
                            mBluetoothConnectionListener.onDeviceDisconnected();
                        }
                        isConnected = false;
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
        mBluetoothService.connect(device);
    }

    public void disconnect() {
        if (mBluetoothService != null) {
            mBluetoothService.stop();
        }
    }

    public void setOnDataReceivedListener(OnDataReceivedListener listener) {
        mDataReceivedListener = listener;
    }

    public void setBluetoothConnectionListener(BluetoothConnectionListener listener) {
        mBluetoothConnectionListener = listener;
    }

    public void writeByteRead() {
        //设置1
        byte[] byteArraySet1 = new byte[]{(byte) 90, (byte) 5, (byte) 4, (byte) 3, (byte) 71};
        //设置2
        byte[] byteArraySet2 = new byte[]{(byte) 90, (byte) 11, (byte) 12, (byte) 0, (byte) 1, (byte) 7, (byte) 80, (byte) 195, (byte) 50, (byte) 0, (byte) 157};
        //设置3
        byte[] byteArraySet3 = new byte[]{(byte) 0x5a, (byte) 0x09, (byte) 0x1e, (byte) 0x01, (byte) 0x96, (byte) 0x00, (byte) 0x64, (byte) 0x00, (byte) 0x14};
        //设置4
        byte[] byteArraySet4 = new byte[]{(byte) 90, (byte) 25, (byte) 36, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 184, (byte) 11, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 72, (byte) 244, (byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255};
        //读取数据
        byte[] byteArrayRead = new byte[]{(byte) 0x5a, (byte) 0x04, (byte) 0x25, (byte) 0x41};
//        mBluetoothService.write(byteArraySet1);
//        mBluetoothService.write(byteArraySet2);
//        mBluetoothService.write(byteArraySet3);
//        mBluetoothService.write(byteArraySet4);
        mBluetoothService.write(byteArrayRead);
    }

    public void writeByte(int flag) {
        byte[] byteArray;
        if (flag == 1) {//点火1
            byteArray = new byte[]{(byte) 0x5a, (byte) 0x1b, (byte) 0x20, (byte) 0x01, (byte) 0xaf, (byte) 0x00, (byte) 0x05, (byte) 0x00, (byte) 0x0a, (byte) 0x00, (byte) 0x10, (byte) 0x27, (byte) 0x88, (byte) 0x13, (byte) 0xe8, (byte) 0x03, (byte) 0x88, (byte) 0x13, (byte) 0x88, (byte) 0x13, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x73};
        } else if (flag == 2) {//点火2
            byteArray = new byte[]{(byte) 0x5a, (byte) 0x1b, (byte) 0x20, (byte) 0x01, (byte) 0xaf, (byte) 0x00, (byte) 0x05, (byte) 0x00, (byte) 0x0a, (byte) 0x00, (byte) 0x10, (byte) 0x27, (byte) 0x88, (byte) 0x13, (byte) 0xe8, (byte) 0x03, (byte) 0x88, (byte) 0x13, (byte) 0x88, (byte) 0x13, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x75};
        } else {
            byteArray = new byte[]{(byte) 0x5a, (byte) 0x1b, (byte) 0x20, (byte) 0x00, (byte) 0xaf, (byte) 0x00, (byte) 0x05, (byte) 0x00, (byte) 0x0a, (byte) 0x00, (byte) 0x10, (byte) 0x27, (byte) 0x88, (byte) 0x13, (byte) 0xe8, (byte) 0x03, (byte) 0x88, (byte) 0x13, (byte) 0x88, (byte) 0x13, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xb3};
        }
        mBluetoothService.write(byteArray);
    }

}
