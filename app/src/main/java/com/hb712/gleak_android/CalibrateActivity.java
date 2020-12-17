package com.hb712.gleak_android;

import android.annotation.SuppressLint;
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
import com.hb712.gleak_android.controller.DeviceController;
import com.hb712.gleak_android.controller.SeriesInfoController;
import com.hb712.gleak_android.entity.CalibrationInfo;
import com.hb712.gleak_android.entity.SeriesInfo;
import com.hb712.gleak_android.util.DateUtil;
import com.hb712.gleak_android.util.LogUtil;
import com.hb712.gleak_android.util.ThreadPoolUtil;
import com.hb712.gleak_android.util.ToastUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class CalibrateActivity extends AppCompatActivity {
    private static final String TAG = CalibrateActivity.class.getSimpleName();
    private int calibrateIndex = -1;
    private MainApplication mainApp;
    private DeviceController deviceController;
    private CalibrationInfoController calibrationInfoController;
    private boolean ifexit = false;
    private Future<?> future = null;

    private Spinner seriesInfoSp;
    private TextView currentSignal;
    private TextView currentPpm;
    private EditText editText1;
    private EditText editText2;
    private EditText editText3;
    private EditText editText4;
    private EditText editText5;
    private EditText editText6;
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
        initClass();
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
                ToastUtil.toastWithLog("校准中，请等待结束...");
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
        editText1 = findViewById(R.id.caliGas1);
        editText2 = findViewById(R.id.caliGas2);
        editText3 = findViewById(R.id.caliGas3);
        editText4 = findViewById(R.id.caliGas4);
        editText5 = findViewById(R.id.caliGas5);
        editText6 = findViewById(R.id.caliGas6);
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

    private void initClass() {
        deviceController = DeviceController.getInstance();
        calibrationInfoController = CalibrationInfoController.getInstance();

    }

    private void changeSeries(int position) {
        if (seriesInfoList.size() > position) {
            initCalibrateInfo(seriesInfoList.get(position).getSeriesName());
        }
    }

    private void initCalibrateInfo(String seriesName) {
        calibrationInfoController.setCurrentSeries(seriesName);
        showCalibrateInfo(new EditText[]{editText1, editText2, editText3, editText4, editText5, editText6});
    }

    private void showCalibrateInfo(EditText[] editTexts) {
        for (EditText editText : editTexts) {
            editText.setText("");
        }
        for (int i = 0; i < editTexts.length; i++) {
            List<CalibrationInfo> calibrationInfoList = calibrationInfoController.getCalibrationInfoList();
            if (calibrationInfoList != null && calibrationInfoList.size() > i) {
                editTexts[i].setText(String.valueOf(calibrationInfoList.get(i).getStandardValue()));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ifexit = true;
        if (future != null) {
            future.cancel(true);
        }
//        mainApp.mBluetooth.removeBlueListener("calibrateBT");
    }

    @Override
    protected void onResume() {
        super.onResume();
        /// DetectActivity 和 DeviceController 为单例则注释下方监听
//        mainApp.mBluetooth.addBluetoothListener("calibrateBT", this::analysisData);
        ifexit = false;
        future = ThreadPoolUtil.getInstance().scheduledCommonExecute(this::showSystemCurrentStatus, 0L, 500, TimeUnit.MILLISECONDS);
    }

    private void analysisData(byte[] dataArray) {
        deviceController.analysisCommand(dataArray);
    }

    @SuppressLint("NonConstantResourceId")
    public void onCaliBtnClick(View view) {
        switch (view.getId()) {
            case R.id.caliBtn1:
                calibrationButtonClick(0);
                break;
            case R.id.caliBtn2:
                calibrationButtonClick(1);
                break;
            case R.id.caliBtn3:
                calibrationButtonClick(2);
                break;
            case R.id.caliBtn4:
                calibrationButtonClick(3);
                break;
            case R.id.caliBtn5:
                calibrationButtonClick(4);
                break;
            case R.id.caliBtn6:
                calibrationButtonClick(5);
                break;
            default:
                break;
        }
    }

    private void showSystemCurrentStatus() {
        runOnUiThread(() -> {
            DeviceController.Voc3000Status status = deviceController.getStatus();
            if (status != null) {
                currentSignal.setText(new DecimalFormat("0.0").format(deviceController.getMicroCurrent()));
                doCalibrateRecord(status);
            }
            currentPpm.setText(new DecimalFormat("0.0").format(deviceController.getCurrentValue()));
        });
    }

    List<Double> signals = new ArrayList<>();
    CalibrationInfo calibrationInfo = null;

    private void doCalibrateRecord(DeviceController.Voc3000Status status) {
        if (calibrateIndex > -1) {
            signals.add(status.microCurrent);
            caliButtons[calibrateIndex].setText(String.valueOf(5 - signals.size() / 2));
            if (signals.size() > 9) {
                double d1 = 0.0D;
                int i = 0;
                while (i < signals.size()) {
                    d1 += signals.get(i);
                    i += 1;
                }
                double d2 = signals.size();
                calibrationInfo.setSignalValue(d1 / d2);
                List<CalibrationInfo> calibrationInfoList = new ArrayList<>();
                calibrationInfoList.add(calibrationInfo);
                calibrationInfoController.updateCalibrateInfo(calibrationInfoList);

                signals.clear();
                calibrationInfo = null;
                Button button = caliButtons[calibrateIndex];
                String str = "校准" + (calibrateIndex + 1);
                button.setText(str);
                caliButtons[calibrateIndex].setEnabled(true);
                calibrateIndex = -1;
            }
        }
    }

    private void calibrationButtonClick(int paramInt) {
        EditText[] editTexts = new EditText[6];
        editTexts[0] = editText1;
        editTexts[1] = editText2;
        editTexts[2] = editText3;
        editTexts[3] = editText4;
        editTexts[4] = editText5;
        editTexts[5] = editText6;
        double standardValue;
        try {
            standardValue = Double.parseDouble(editTexts[paramInt].getText().toString().trim());
        } catch (NumberFormatException nfe) {
            LogUtil.infoOut(TAG, "非法输入，请重试！");
            return;
        }
        if (paramInt > 0 && standardValue == 0) {
            List<CalibrationInfo> calibrationInfos = calibrationInfoController.getCalibrationInfoList();
            for (int i = 0; i < calibrationInfos.size(); i++) {
                if (calibrationInfos.get(i).getStandardValue() == 0.0D) {
                    if (calibrationInfos.size() > paramInt) {
                        calibrationInfos.get(paramInt).delete();
                        calibrationInfos.remove(paramInt);
                        showCalibrateInfo(editTexts);
                        calibrationInfoController.saveAll();
                        return;
                    }
                    return;
                }
            }
        }

        if (currentSignal.getText().toString().isEmpty()) {
            ToastUtil.toastWithLog("信号错误！");
            return;
        }
        if (calibrationInfoController.getCurrentSeries() == null) {
            ToastUtil.toastWithLog("请选择工作曲线！");
            return;
        }
        if (editTexts[paramInt].getText().toString().isEmpty()) {
            ToastUtil.toastWithLog("请输入标气" + (paramInt + 1) + "浓度！");
            return;
        }
        CalibrationInfo localCalibrationInfo = new CalibrationInfo();
        localCalibrationInfo.setCalibrateTime(DateUtil.getDefaultTime());
        localCalibrationInfo.setDeviceName(deviceController.getDeviceName());
        localCalibrationInfo.setSeriesId(calibrationInfoController.getCurrentSeries().getId());
        localCalibrationInfo.setStandardValue(standardValue);
        if (calibrateIndex < 0) {
            caliButtons[paramInt].setEnabled(false);
            calibrationInfo = localCalibrationInfo;
            calibrateIndex = paramInt;
            calibrationInfoController.deleteCalibrateInfo(calibrateIndex);
        }
    }
}
