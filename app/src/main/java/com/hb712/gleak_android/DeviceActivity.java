package com.hb712.gleak_android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.hb712.gleak_android.adapter.DeviceAdapter;
import com.hb712.gleak_android.util.GlobalParam;
import com.hb712.gleak_android.util.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/11/3 11:22
 */
public class DeviceActivity extends AppCompatActivity {

    private static final String TAG = DeviceActivity.class.getSimpleName();
    private BluetoothAdapter bluetoothAdapter;
    private List<String> pairedDeviceList;

    private final AdapterView.OnItemClickListener mDeviceClickListener = (parent, view, position, id) -> {
        bluetoothAdapter.cancelDiscovery();
        String info = pairedDeviceList.get(position);
        String address = info.substring(info.length() - 17);
        Intent intent = new Intent();
        intent.putExtra(GlobalParam.DEVICE_ADDRESS, address);
        LogUtil.debugOut(TAG, "连接地址：" + address);
        setResult(Activity.RESULT_OK, intent);
        finish();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        setResult(Activity.RESULT_CANCELED);
        pairedDeviceList = new ArrayList<>();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> btDeviceSet = bluetoothAdapter.getBondedDevices();
        if (btDeviceSet.size() > 0) {
            for (BluetoothDevice device : btDeviceSet) {
                pairedDeviceList.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            pairedDeviceList.add(getResources().getText(R.string.none_paired).toString());
        }
        DeviceAdapter deviceAdapter = new DeviceAdapter(this, pairedDeviceList);
        ListView pairedDevices = findViewById(R.id.paired_devices);
        pairedDevices.setAdapter(deviceAdapter);
        pairedDevices.setOnItemClickListener(mDeviceClickListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bluetoothAdapter != null) {
            bluetoothAdapter.cancelDiscovery();
        }
        finish();
    }
}

