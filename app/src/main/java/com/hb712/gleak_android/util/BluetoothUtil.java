package com.hb712.gleak_android.util;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.hb712.gleak_android.service.BluetoothService;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/9/25 10:56
 */
public class BluetoothUtil {
    private final String TAG = BluetoothUtil.class.getSimpleName();

    private final BluetoothAdapter mBluetoothAdapter;
    private BluetoothService mBluetoothService = null;
    private boolean isConnected = false;
    private boolean isConnecting = false;
    boolean isInit = false;

    private static BluetoothUtil instance;

    public static synchronized BluetoothUtil getInstance() {
        if (instance == null) {
            instance = new BluetoothUtil();
        }
        return instance;
    }

    private BluetoothUtil() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public interface BluetoothListener {
        /**
         * 默认方法：蓝牙连接回调
         *
         * @param name    蓝牙设备名称
         * @param address 蓝牙设备地址
         */
        default void onDeviceConnected(String name, String address) {
            ToastUtil.shortInstanceToast("蓝牙已连接");
        }

        /**
         * 默认方法：蓝牙断开回调
         */
        default void onDeviceDisconnected() {
            ToastUtil.shortInstanceToast("蓝牙已断开");
        }

        /**
         * 默认方法：蓝牙连接失败回调
         */
        default void onDeviceConnectionFailed() {
            ToastUtil.shortInstanceToast("蓝牙连接失败");
        }

        /**
         * @param data
         */
        void onDataReceived(byte[] data);
    }

    @SuppressLint("HardwareIds")
    public boolean isBluetoothAvailable() {
        try {
            if (mBluetoothAdapter == null || mBluetoothAdapter.getAddress() == null) {
                return false;
            }
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
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GlobalParam.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    if (readBuf != null && readBuf.length > 0) {
                        onDataReceived(readBuf);
                    }
                    break;
                case GlobalParam.MESSAGE_DEVICE_NAME:
                    String mDeviceName = msg.getData().getString(GlobalParam.DEVICE_NAME);
                    String mDeviceAddress = msg.getData().getString(GlobalParam.DEVICE_ADDRESS);
                    onDeviceConnected(mDeviceName, mDeviceAddress);
                    isConnected = true;
                    break;
                case GlobalParam.MESSAGE_STATE_CHANGE:
                    if (isConnected && msg.arg1 != GlobalParam.STATE_CONNECTED) {
                        onDeviceDisconnected();
                        isConnected = false;
                    }
                    if (!isConnecting && msg.arg1 == GlobalParam.STATE_CONNECTING) {
                        isConnecting = true;
                    } else if (isConnecting) {
                        if (msg.arg1 != GlobalParam.STATE_CONNECTED) {
                            onDeviceConnectionFailed();
                        }
                        isConnecting = false;
                    }
                    break;
                default:
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


    Map<String, BluetoothListener> listenerMap = new HashMap<>();

    public void addBluetoothListener(String name, BluetoothListener listener) {
        if (!listenerMap.containsKey(name)) {
            listenerMap.put(name, listener);
        }
    }

    public void removeBlueListener(String name) {
        listenerMap.remove(name);
    }

    private void onDataReceived(byte[] data) {
        for (Map.Entry<String, BluetoothListener> entry : listenerMap.entrySet()) {
            entry.getValue().onDataReceived(data);
        }
    }

    private void onDeviceConnected(String name, String address) {
        for (Map.Entry<String, BluetoothListener> entry : listenerMap.entrySet()) {
            entry.getValue().onDeviceConnected(name, address);
        }
    }

    void onDeviceDisconnected() {
        for (Map.Entry<String, BluetoothListener> entry : listenerMap.entrySet()) {
            entry.getValue().onDeviceDisconnected();
        }
    }

    void onDeviceConnectionFailed() {
        for (Map.Entry<String, BluetoothListener> entry : listenerMap.entrySet()) {
            entry.getValue().onDeviceConnectionFailed();
        }
    }

    public void readData() {
        init();
        writeByte(new byte[]{90, 4, 37, 65});
    }

    /**
     * 点火1
     */
    public void openFire() {
        writeByte(new byte[]{90, 27, 32, 1, -81, 0, 5, 0, 10, 0, 16, 39, -120, 19, -24, 3, -120, 19, -120, 19, 0, 0, 0, 0, 0, 0, 115});
    }

    /**
     * 点火2
     */
    public void openFire2() {
        writeByte(new byte[]{90, 27, 32, 1, -81, 0, 5, 0, 10, 0, 16, 39, -120, 19, -24, 3, -120, 19, -120, 19, 0, 0, 0, 0, 1, 0, 117});
    }

    /**
     * 关火
     */
    public void closeFire() {
        openFire();
        try {
            Thread.sleep(300L);
        } catch (InterruptedException localInterruptedException) {
            localInterruptedException.printStackTrace();
        }
        writeByte(new byte[]{90, 27, 32, 0, -81, 0, 5, 0, 10, 0, 16, 39, -120, 19, -24, 3, -120, 19, -120, 19, 0, 0, 0, 0, 0, 0, -77});
    }

    /**
     * 发送
     *
     * @param byteArray 发送的数据
     */
    public void writeByte(byte[] byteArray) {
        if (byteArray == null || byteArray.length <= 0) {
            LogUtil.warnOut(TAG, null, "发送失败，发送内容为空");
            return;
        }
        mBluetoothService.write(byteArray);
    }

    /**
     * 第一次读取数据发送
     */
    private void init() {
        if (!this.isInit) {
            setSampleParameters(0);
            setIntegrationControlParams((byte) 10);
            setDeadHeadParams();
            setCalH2PressureCompensation();
            this.isInit = true;
        }
    }

    /**
     * 设置采样参数
     *
     * @param paramInt
     */
    public void setSampleParameters(int paramInt) {
        byte[] arrayOfByte = new byte[5];
        arrayOfByte[0] = 90;
        arrayOfByte[1] = 5;
        arrayOfByte[2] = 4;
        arrayOfByte[3] = ((byte) paramInt);
        arrayOfByte[4] = 0;
        arrayOfByte[(arrayOfByte.length - 1)] = CRC8.calcCrc(arrayOfByte, arrayOfByte.length - 1);
        writeByte(arrayOfByte);
    }

    /**
     * 设置控制参数
     *
     * @param param
     */
    public void setIntegrationControlParams(byte param) {
        byte[] arrayOfByte = new byte[11];
        arrayOfByte[0] = 90;
        arrayOfByte[1] = 11;
        arrayOfByte[2] = 12;
        arrayOfByte[3] = 0;
        arrayOfByte[4] = 1;
        arrayOfByte[5] = 7;
        arrayOfByte[6] = 80;
        arrayOfByte[7] = -61;
        arrayOfByte[8] = param;
        arrayOfByte[9] = 0;
        arrayOfByte[10] = 0;
        arrayOfByte[(arrayOfByte.length - 1)] = CRC8.calcCrc(arrayOfByte, arrayOfByte.length - 1);
        writeByte(arrayOfByte);
    }

    private void setDeadHeadParams() {
        writeByte(new byte[]{90, 9, 30, 1, (byte) 150, 0, 100, 0, 20});
    }

    private void setCalH2PressureCompensation() {
        writeByte(new byte[]{90, 25, 36, 0, 0, 0, 0, 0, -72, 11, 0, 0, 0, 0, 0, 0, 72, -12, -1, -1, -1, -1, -1, -1});
    }
}
