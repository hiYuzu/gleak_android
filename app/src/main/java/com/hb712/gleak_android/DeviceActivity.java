package com.hb712.gleak_android;

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
import com.hb712.gleak_android.util.GlobalParam;
import com.hb712.gleak_android.util.LogUtil;

import java.util.Set;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/11/3 11:22
 */
public class DeviceActivity extends AppCompatActivity {

    private static final String TAG = DeviceActivity.class.getSimpleName();
    private BluetoothAdapter bluetoothAdapter;

    private AdapterView.OnItemClickListener mDeviceClickListener = (parent, view, position, id) -> {
        bluetoothAdapter.cancelDiscovery();
        String info = ((TextView) view).getText().toString();
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
        ArrayAdapter<String> pairedDevicesAdapter = new ArrayAdapter<>(this, R.layout.device_name);

        ListView pairedDevices = findViewById(R.id.paired_devices);
        pairedDevices.setAdapter(pairedDevicesAdapter);
        pairedDevices.setOnItemClickListener(mDeviceClickListener);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> btDeviceSet = bluetoothAdapter.getBondedDevices();
        if (btDeviceSet.size() > 0) {
            for (BluetoothDevice device : btDeviceSet) {
                pairedDevicesAdapter.add(device.getName() + "\n" + device.getAddress());
            }
            return;
        }
        pairedDevicesAdapter.add(getResources().getText(R.string.none_paired).toString());
    }

    protected void onDestroy() {
        super.onDestroy();
        if (bluetoothAdapter != null) {
            bluetoothAdapter.cancelDiscovery();
        }
        finish();
    }
}

