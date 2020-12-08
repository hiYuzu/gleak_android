package com.hb712.gleak_android;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.hb712.gleak_android.controller.CalibrationInfoController;
import com.hb712.gleak_android.controller.SeriesInfoController;
import com.hb712.gleak_android.entity.CalibrationInfo;
import com.hb712.gleak_android.entity.SeriesInfo;
import com.hb712.gleak_android.message.blue.Phx21Status;
import com.hb712.gleak_android.util.LogUtil;
import com.hb712.gleak_android.util.ThreadPoolUtil;
import com.hb712.gleak_android.util.ToastUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class CalibrateActivity extends AppCompatActivity {
    private static final String TAG = CalibrateActivity.class.getSimpleName();
    private int calibrateIndex = -1;
    private MainApplication mainApp;
    private boolean ifexit = false;

    private Spinner seriesInfoSp;
    private TextView currentSignal;
    private TextView currentPpm;
    private EditText gas1;
    private EditText gas2;
    private EditText gas3;
    private EditText gas4;
    private EditText gas5;
    private EditText gas6;
    private Button caliBtn1;
    private Button caliBtn2;
    private Button caliBtn3;
    private Button caliBtn4;
    private Button caliBtn5;
    private Button caliBtn6;

    private Button[] caliButtons;
    private List<SeriesInfo> seriesInfoList;
    private String[] seriesNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibrate);
        mainApp = MainApplication.getInstance();
        setupActionBar();
        initView();
        if (seriesInfoList != null && seriesInfoList.size() > 0) {
            initCalibrateInfo(seriesInfoList.get(0).getSeriesName());
        }
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (calibrateIndex > -1) {
                ToastUtil.shortInstanceToast("校准中，请等待结束...");
                return true;
            }
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        seriesInfoSp = findViewById(R.id.caliSeriesSp);
        currentSignal = findViewById(R.id.caliCurrentSignal);
        currentPpm = findViewById(R.id.caliCurrentPpm);
        gas1 = findViewById(R.id.caliGas1);
        gas2 = findViewById(R.id.caliGas2);
        gas3 = findViewById(R.id.caliGas3);
        gas4 = findViewById(R.id.caliGas4);
        gas5 = findViewById(R.id.caliGas5);
        gas6 = findViewById(R.id.caliGas6);
        caliBtn1 = findViewById(R.id.caliBtn1);
        caliBtn2 = findViewById(R.id.caliBtn2);
        caliBtn3 = findViewById(R.id.caliBtn3);
        caliBtn4 = findViewById(R.id.caliBtn4);
        caliBtn5 = findViewById(R.id.caliBtn5);
        caliBtn6 = findViewById(R.id.caliBtn6);
        caliButtons = new Button[]{caliBtn1, caliBtn2, caliBtn3, caliBtn4, caliBtn5, caliBtn6};
        seriesInfoList = SeriesInfoController.getAll();
        seriesNames = new String[seriesInfoList.size()];
        for (int i = 0; i < seriesInfoList.size(); i++) {
            seriesNames[i] = seriesInfoList.get(i).getSeriesName();
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.item_spinner_normal, seriesNames);
        seriesInfoSp.setAdapter(arrayAdapter);
        seriesInfoSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                changeSeries(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void changeSeries(int position) {
        if (seriesInfoList.size() > position) {
            initCalibrateInfo(seriesInfoList.get(position).getSeriesName());
        }
    }

    private void initCalibrateInfo(String seriesName) {
        CalibrationInfoController.getInstance().setCurrentSeries(seriesName);
        showCalibrateInfo(new EditText[]{gas1, gas2, gas3, gas4, gas5, gas6});
    }

    private void showCalibrateInfo(EditText[] editTexts) {
        for (EditText editText : editTexts) {
            editText.setText("");
        }
        for (int i = 0; i < editTexts.length; i++) {
            List<CalibrationInfo> calibrationInfoList = CalibrationInfoController.getInstance().getCalibrationInfoList();
            if (calibrationInfoList != null && calibrationInfoList.size() > i) {
                editTexts[i].setText(String.valueOf(calibrationInfoList.get(i).getStandardValue()));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ifexit = true;
        // TODO: hiYuzu 2020/12/8
//        mainApp.deviceController.setIfCalibrating(false);
        mainApp.mBluetooth.removeBlueListener("calibrateBT");
    }
/*
    @Override
    protected void onResume() {
        super.onResume();
        mainApp.mBluetooth.addBluetoothListener("calibrateBT", this::analysisData);
        ifexit = false;
        ThreadPoolUtil.scheduledCommonExecute(this::showSystemCurrentStatus, 500, TimeUnit.MILLISECONDS);
    }

    private void analysisData(byte[] data) {

    }
/*
    public void onCaliBtnClick(View view) {
        switch (view.getId()) {
            case R.id.caliBtn1:
            case R.id.caliBtn2:
            case R.id.caliBtn3:
            case R.id.caliBtn4:
            case R.id.caliBtn5:
            case R.id.caliBtn6:
                break;
            default:
                break;
        }
    }
    private class InnerThread extends Thread {
        @Override
        public void run() {
            while (!ifexit) {
                mainApp.deviceController.sendReadDataCommand();
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException ie) {
                    LogUtil.errorOut(TAG, ie, null);
                }
                runOnUiThread(CalibrateActivity.this::showSystemCurrentStatus);
            }
        }
    }

    private void showSystemCurrentStatus() {
        Phx21Status localPhx21Status = mainApp.deviceController.getStatus();
        if (localPhx21Status != null) {
            currentSignal.setText(NumPointDealer.getString(mainApp.deviceController.getSystemCurrent()));
            doCalibrateRecord(localPhx21Status);
        }
        currentPpm.setText(NumPointDealer.getString(mainApp.deviceController.getPpm()));
    }*/
}
