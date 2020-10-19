package com.hb712.gleak_android;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hb712.gleak_android.util.BluetoothState;
import com.hb712.gleak_android.util.BluetoothUtil;
import com.hb712.gleak_android.util.GlobalParam;

public class DetectActivity extends AppCompatActivity {

    private static final String TAG = DetectActivity.class.getSimpleName();
    private Button detectConnectB;
    private TextView connDeviceTV;
    private TextView detectSeriesTV;
    private TextView detectFactorTV;
    private EditText detectValueET;
    private EditText detectMaxvalueET;
    private Button deviceParamB;
    private Button currentTrendB;

    private BluetoothUtil mBluetooth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect);
        setupActionBar();
        mBluetooth = BluetoothUtil.getInstance();
        initView();
        initBluetooth();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initView() {
        detectConnectB = findViewById(R.id.detectConnectButton);
        connDeviceTV = findViewById(R.id.connDevice);
        detectSeriesTV = findViewById(R.id.detectSeries);
        detectFactorTV = findViewById(R.id.detectFactor);
        detectValueET = findViewById(R.id.detectValue);
        detectValueET.setEnabled(false);
        detectMaxvalueET = findViewById(R.id.detectMaxvalue);
        detectMaxvalueET.setEnabled(false);
        deviceParamB = findViewById(R.id.deviceParam);
        currentTrendB = findViewById(R.id.currentTrend);
        currentTrendB.setTextColor(getResources().getColor(R.color.login_textColor));
    }

    private void initBluetooth() {
        if (!mBluetooth.isBluetoothAvailable()) {
            Toast.makeText(this, "蓝牙不可用", Toast.LENGTH_LONG).show();
            finish();
        }

        mBluetooth.setBluetoothConnectionListener(new BluetoothUtil.BluetoothConnectionListener() {
            @Override
            public void onDeviceConnected(String name, String address) {
                GlobalParam.isConnected = true;
                detectConnectB.setText(R.string.detect_disconnect);
                connDeviceTV.setText(name);
                //TODO..线程循环请求仪器参数
                Toast.makeText(DetectActivity.this, "蓝牙已连接", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeviceDisconnected() {
                GlobalParam.isConnected = false;
                detectConnectB.setText(R.string.detect_connect);
                connDeviceTV.setText(R.string.detect_disconnected);
                //TODO..释放请求仪器参数线程
                Toast.makeText(DetectActivity.this, "蓝牙已断开", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onDeviceConnectionFailed() {
                Toast.makeText(DetectActivity.this, "蓝牙连接失败", Toast.LENGTH_SHORT).show();
            }
        });

        mBluetooth.setOnDataReceivedListener((data, message) -> {
            if (data.length > 0) {
                String msg = new String(data);
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * 左上角退出不要destroy activity
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                moveTaskToBack(true);
                return false;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 手机返回键不要destroy activity
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onStart() {
        super.onStart();
        if (!mBluetooth.isBluetoothEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if (!mBluetooth.isServiceAvailable()) {
                mBluetooth.setupService();
                mBluetooth.startService(BluetoothState.DEVICE_ANDROID);
            }
        }
    }

    public void connectClick(View view) {
        if (GlobalParam.isConnected) {
            mBluetooth.disconnect();
        } else {
            Intent intent = new Intent(getApplicationContext(), DeviceActivity.class);
            startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK) {
                mBluetooth.connect(data);
            }
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                mBluetooth.setupService();
                mBluetooth.startService(BluetoothState.DEVICE_ANDROID);
            } else {
                finish();
            }
        }
    }

    /**
     * 点火
     *
     * @param view
     */
    public void fireClick(View view) {
        if (isConnected()) {
            //TODO..
            mBluetooth.send(new byte[1]);
        }
    }

    /**
     * 点火2
     *
     * @param view
     */
    public void fireClick2(View view) {
        if (isConnected()) {
            //TODO..
        }
    }

    /**
     * 关火
     *
     * @param view
     */
    public void ceasefireClick(View view) {
        if (isConnected()) {
            //TODO..
        }
    }

    /**
     * 记录
     *
     * @param view
     */
    public void recordClick(View view) {
        if (isConnected()) {
            //TODO..
        }
    }

    /**
     * 选择工作曲线
     *
     * @param view
     */
    public void selectSeries(View view) {
        final String seriesExample = "标准曲线";
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.detect_select_series);
        builder.setMessage(seriesExample);
        builder.setCancelable(true);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                detectSeriesTV.setText(seriesExample);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 选择响应因子
     *
     * @param view
     */
    public void selectFactor(View view) {
        final String seriesExample = "甲醇";
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.detect_select_series);
        builder.setMessage(seriesExample);
        builder.setCancelable(true);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                detectFactorTV.setText(seriesExample);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 显示仪器参数
     *
     * @param view
     */
    public void deviceParamClick(View view) {
    }

    /**
     * 显示趋势曲线
     *
     * @param view
     */
    public void currentTrendClick(View view) {
    }

    private boolean isConnected() {
        if (GlobalParam.isConnected) {
            return true;
        }
        Toast.makeText(this, "蓝牙未连接", Toast.LENGTH_SHORT).show();
        return false;
    }
}
