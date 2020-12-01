package com.hb712.gleak_android.service;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.hb712.gleak_android.util.GlobalParam;
import com.hb712.gleak_android.util.LogUtil;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/9/25 10:56
 */
@SuppressLint("NewApi")
public class BluetoothService {
    private static final String TAG = BluetoothService.class.getSimpleName();
    private static final String NAME_SECURE = "Bluetooth Secure";
    private static final UUID UUID_ANDROID_DEVICE =
            UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    private static final UUID UUID_OTHER_DEVICE =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private final BluetoothAdapter mAdapter;
    private final Handler mHandler;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private int mState;
    private boolean isAndroid = GlobalParam.DEVICE_ANDROID;

    public BluetoothService(Handler handler) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = GlobalParam.STATE_NONE;
        mHandler = handler;
    }


    private synchronized void setState(int state) {
        LogUtil.debugOut(TAG, "设置状态 " + mState + " -> " + state);
        mState = state;
        mHandler.obtainMessage(GlobalParam.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    public synchronized int getState() {
        return mState;
    }

    public synchronized void connect(BluetoothDevice device) {
        // 关掉正在连接的设备
        if (mState == GlobalParam.STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        // 关掉当前已连接设备
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // 启动线程连接给定的设备
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(GlobalParam.STATE_CONNECTING);
    }

    private synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        //已连接
        Message msg = mHandler.obtainMessage(GlobalParam.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(GlobalParam.DEVICE_NAME, device.getName());
        bundle.putString(GlobalParam.DEVICE_ADDRESS, device.getAddress());
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        setState(GlobalParam.STATE_CONNECTED);
    }

    public synchronized void stop() {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        setState(GlobalParam.STATE_NONE);
    }

    public void write(byte[] out) {
        ConnectedThread r;
        synchronized (this) {
            if (mState != GlobalParam.STATE_CONNECTED) {
                return;
            }
            r = mConnectedThread;
        }
        r.write(out);
    }

    private void connectionFailed() {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        setState(GlobalParam.STATE_NONE);
    }

    //发起连接
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;
            try {
                if (BluetoothService.this.isAndroid) {
                    tmp = device.createRfcommSocketToServiceRecord(UUID_ANDROID_DEVICE);
                } else {
                    tmp = device.createRfcommSocketToServiceRecord(UUID_OTHER_DEVICE);
                }
            } catch (IOException e) {
                LogUtil.warnOut(TAG, e, "获取socket失败");
            }
            mmSocket = tmp;
        }

        public void run() {
            mAdapter.cancelDiscovery();
            try {
                mmSocket.connect();
            } catch (IOException e) {
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    LogUtil.warnOut(TAG, e2, "socket关闭失败");
                }
                LogUtil.warnOut(TAG, e, "与" + mmDevice.getName() + "建立连接失败");
                connectionFailed();
                return;
            }
            //完成连接，重置连接线程
            synchronized (BluetoothService.this) {
                mConnectThread = null;
            }
            connected(mmSocket, mmDevice);
        }

        void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                LogUtil.warnOut(TAG, e, "socket关闭失败");
            }
        }
    }

    //已连接
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                LogUtil.warnOut(TAG, e, "");
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            int ch;
            while (true) {
                try {
                    bytes = 0;
                    while ((ch = mmInStream.read()) != '\n') {
                        if (ch != -1) {
                            buffer[bytes] = (byte) ch;
                            bytes++;
                        }
                    }
//                    for (int i = 0; i < buffer.length; i++) {
//                        buffer[i] = ((Integer) mmInStream.read()).byteValue();
//                    }
                    buffer[bytes] = (byte) '\n';
                    bytes++;

                    mHandler.obtainMessage(GlobalParam.MESSAGE_READ, bytes, -1, buffer).sendToTarget();
//                    sleep(10);
                } catch (IOException e) {
                    LogUtil.warnOut(TAG, e, "收发失败");
                    connectionFailed();
                    break;
                }
//                catch (InterruptedException e) {
//                    LogUtil.warnOut(TAG, e, "thread sleep异常");
//                    connectionFailed();
//                    break;
//                }
            }
        }

        void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);
            } catch (IOException e) {
                LogUtil.errorOut(TAG, e, "发送数据失败");
            }
        }

        void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                LogUtil.errorOut(TAG, e, "无法关闭connect socket");
            }
        }
    }
}