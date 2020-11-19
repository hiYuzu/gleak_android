package com.hb712.gleak_android;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import com.hb712.gleak_android.callback.FactorAddSuccessCallback;
import com.hb712.gleak_android.controller.CalibrationInfoController;
import com.hb712.gleak_android.controller.DeviceController;
import com.hb712.gleak_android.dao.DBManager;
import com.hb712.gleak_android.dao.FactorCoefficientInfoDao;
import com.hb712.gleak_android.dao.SeriesLimitInfoDao;
import com.hb712.gleak_android.dialog.FactorDialog;
import com.hb712.gleak_android.dialog.SeriesDialog;
import com.hb712.gleak_android.pojo.FactorCoefficientInfo;
import com.hb712.gleak_android.pojo.SeriesInfo;
import com.hb712.gleak_android.pojo.SeriesLimitInfo;
import com.hb712.gleak_android.rtsp.RtspPlayer;
import com.hb712.gleak_android.util.BluetoothUtil;
import com.hb712.gleak_android.util.GlobalParam;
import com.hb712.gleak_android.util.LogUtil;
import com.hb712.gleak_android.util.SPUtil;
import com.hb712.gleak_android.util.UnitManager;

import org.greenrobot.greendao.query.WhereCondition;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tv.danmaku.ijk.media.player.ffmpeg.FFmpegApi;

public class DetectActivity extends AppCompatActivity {

    private static final String TAG = DetectActivity.class.getSimpleName();
    //连接
    private Button detectConnectB;
    //连接的设备名
    private TextView connDeviceTV;
    //单位
    private TextView detectUnit;
    //曲线
    private TextView detectSeriesTV;
    //响应因子
    private TextView detectFactorTV;
    //检测值
    private EditText detectValueET;
    //最大值
    private EditText detectMaxvalueET;
    //仪器参数
//    private Button deviceParamB;
//    private ScrollView detectStatus;

    private TextView detectParamPower;
    private TextView detectParamVol;
    private TextView detectParamPump;
    private TextView detectParamDischargePress;
    private TextView detectParamOutletHydrogenPress;
    private TextView detectParamSystemCurrent;
    private TextView detectParamHydrogen;
    private TextView detectParamHydrogenPress;
    private TextView detectParamCcTemp;
    private TextView detectParamFireTemp;
    private TextView detectParamMicroCurrent;

    //当前趋势
//    private Button currentTrendB;
//    private LinearLayout detectCurve;
//    private LineChart detectCurveChart;
    private LineDataSet lineDataSet;
    private double minValue = Double.MAX_VALUE;
    private double maxValue = Double.MIN_VALUE;

    //other
    private BluetoothUtil mBluetooth;
    private DeviceController deviceController;
    private SeriesLimitInfo seriesLimitInfo;
    private boolean unitPPM = true;
    private boolean detecting = false;
    private double detectMaxvaluePPM = 0;
    private double detectMaxvalueMg = 0;

    private RtspPlayer mRtspPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect);
        setupActionBar();
        mBluetooth = BluetoothUtil.getInstance();
        initView();
        initBluetooth();
        initClass();
        initCurveInfo();
        runOnUiThread(this::loadRtsp);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * 初始化控件
     */
    private void initView() {
        detectConnectB = findViewById(R.id.detectConnectButton);
        connDeviceTV = findViewById(R.id.connDevice);
        detectUnit = findViewById(R.id.detectUnit);
        detectUnit.setOnClickListener((p) -> {
            if (detectUnit.getText().toString().equals(getString(R.string.detect_value_unit_ppm))) {
                unitPPM = false;
                detectUnit.setText(R.string.detect_value_unit_mg);
            } else {
                unitPPM = true;
                detectUnit.setText(R.string.detect_value_unit_ppm);
            }
            SPUtil.put(this, "unit", detectUnit.getText().toString());
        });
        detectSeriesTV = findViewById(R.id.detectSeries);
        detectFactorTV = findViewById(R.id.detectFactor);
        detectValueET = findViewById(R.id.detectValue);
        detectValueET.setEnabled(false);
        detectMaxvalueET = findViewById(R.id.detectMaxvalue);
        detectMaxvalueET.setEnabled(false);

//        deviceParamB = findViewById(R.id.deviceParam);
//        detectStatus = findViewById(R.id.detectStatus);
        detectParamPower = findViewById(R.id.detectParamPower);
        detectParamVol = findViewById(R.id.detectParamVol);
        detectParamPump = findViewById(R.id.detectParamPump);
        detectParamDischargePress = findViewById(R.id.detectParamDischargePress);
        detectParamOutletHydrogenPress = findViewById(R.id.detectParamOutletHydrogenPress);
        detectParamSystemCurrent = findViewById(R.id.detectParamSystemCurrent);
        detectParamHydrogen = findViewById(R.id.detectParamHydrogen);
        detectParamHydrogenPress = findViewById(R.id.detectParamHydrogenPress);
        detectParamCcTemp = findViewById(R.id.detectParamCcTemp);
        detectParamFireTemp = findViewById(R.id.detectParamFireTemp);
        detectParamMicroCurrent = findViewById(R.id.detectParamMicroCurrent);

//        currentTrendB = findViewById(R.id.currentTrend);
//        detectCurve = findViewById(R.id.detectCurve);
//        detectCurveChart = findViewById(R.id.detectCurveChart);
    }

    /**
     * 初始化蓝牙
     */
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
            System.out.println("接收到蓝牙信息:" + Arrays.toString(data));
            //仪器参数
            showFragmentContent(data);
        });
    }

    private void initClass() {
        deviceController = DeviceController.getInstance();
    }

    /**
     * 初始化曲线信息（工作曲线和响应因子）
     */
    private void initCurveInfo() {
        Object obj1 = SPUtil.get(this, "DetectSeries", "");
        String seriesName = "";
        if (obj1 != null) {
            seriesName = obj1.toString();
        }
        if (seriesName.equals("")) {
            seriesName = "标准曲线";
        }
        try {
            CalibrationInfoController.getInstance().setCurrentSeries(seriesName);
            Object obj2 = SPUtil.get(this, "DetectFactor", 0L);
            long id = -1;
            if (obj2 != null) {
                id = Long.parseLong(obj2.toString());
            }
            List<FactorCoefficientInfo> factorCoefficientInfoList = DBManager.getInstance().getReadableSession().getFactorCoefficientInfoDao().queryBuilder().where(FactorCoefficientInfoDao.Properties.id.eq(id), new WhereCondition[0]).list();
            if (factorCoefficientInfoList != null && factorCoefficientInfoList.size() > 0) {
                UnitManager.changeFactor(factorCoefficientInfoList.get(0));
                deviceController.changeFactor(factorCoefficientInfoList.get(0));
            }
            showSeriesName();
            showFactorName();
        } catch (Exception e) {
            LogUtil.warnOut(TAG, e, "");
        }
    }

    private void loadRtsp() {
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (ActivityCompat.checkSelfPermission(this, permissions[0]) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, 1234);
        }
        mRtspPlayer = new RtspPlayer();
        mRtspPlayer.init(DetectActivity.this, new RtspPlayer.BaseLoadingView() {
            @Override
            public void showLoading() {

            }

            @Override
            public void dismissLoading() {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRtspPlayer.startPlay();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mRtspPlayer.stopPlay();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRtspPlayer.release();
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
            startActivityForResult(intent, GlobalParam.REQUEST_ENABLE_BT);
        } else {
            if (!mBluetooth.isServiceAvailable()) {
                mBluetooth.setupService();
            }
        }
    }

    public void setVideoIp(View view) {
        EditText ipEdit = new EditText(this);
        ipEdit.setText(GlobalParam.VIDEO_URL);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("视频ip地址");
        builder.setView(ipEdit);
        builder.setPositiveButton("确定",
                (dialog, which) -> {
                    GlobalParam.VIDEO_URL = ipEdit.getText().toString();
                })
                .setNegativeButton("取消", (dialog, which) -> {
                })
                .setCancelable(true)
                .show();
    }

    public void videoStartClick(View view) {
        mRtspPlayer.startPlay();
    }

    public void videoStopClick(View view) {
        mRtspPlayer.stopPlay();
    }

    public void getScreenshots(View view) {

        if (!mRtspPlayer.isRecording) {
            mRtspPlayer.startRecord(this);
        } else {
            mRtspPlayer.stopRecord(this);
        }
        runOnUiThread(() -> mRtspPlayer.getScreenshots(getApplicationContext()));
    }

    public void connectClick(View view) {
        if (GlobalParam.isConnected) {
            mBluetooth.disconnect();
        } else {
            Intent intent = new Intent(getApplicationContext(), DeviceActivity.class);
            startActivityForResult(intent, GlobalParam.REQUEST_CONNECT_DEVICE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GlobalParam.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK) {
                mBluetooth.connect(data);
            }
        } else if (requestCode == GlobalParam.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                mBluetooth.setupService();
            } else {
                finish();
            }
        }
    }

    private void showSeriesName() {
        SeriesInfo currentSeries = CalibrationInfoController.getInstance().getCurrentSeries();
        if (currentSeries != null) {
            detectSeriesTV.setText(currentSeries.getSeriesName());
            if (!currentSeries.isStdSeries()) {
                detectFactorTV.setText("响应因子");
            }
            try {
                List<SeriesLimitInfo> seriesLimitInfoList = DBManager.getInstance().getReadableSession().getSeriesLimitInfoDao().queryBuilder().where(SeriesLimitInfoDao.Properties.seriesId.eq(currentSeries.getId()), new WhereCondition[0]).list();
                if (seriesLimitInfoList != null && seriesLimitInfoList.size() > 0) {
                    seriesLimitInfo = seriesLimitInfoList.get(0);
                }
            } catch (Exception e) {
                LogUtil.warnOut(TAG, e, "");
            }
        } else {
            seriesLimitInfo = null;
        }
    }

    private void showFactorName() {
        FactorCoefficientInfo factorCoefficientInfo = CalibrationInfoController.getInstance().getFactor();
        if (factorCoefficientInfo != null) {
            detectFactorTV.setText(factorCoefficientInfo.getFactorName());
            return;
        }
        detectFactorTV.setText("响应因子");
    }

    /**
     * 点火
     *
     * @param view
     */
    public void fireClick(View view) {
        if (isConnected()) {
            System.out.println("点火1");
        }
    }

    /**
     * 点火2
     *
     * @param view
     */
    public void fireClick2(View view) {
        if (isConnected()) {
            System.out.println("点火2");
        }
    }

    /**
     * 关火
     *
     * @param view
     */
    public void ceasefireClick(View view) {
        if (isConnected()) {
            System.out.println("关火");
        }
    }

    /**
     * 记录
     *
     * @param view
     */
    public void recordClick(View view) {
        if (isConnected()) {
            System.out.println("记录");
        }
    }

/*
    public void deviceParamClick(View view) {
        detectCurve.setVisibility(View.GONE);
        currentTrendB.setTextColor(getResources().getColor(R.color.white));
        deviceParamB.setTextColor(getResources().getColor(R.color.black));
        detectStatus.setVisibility(View.VISIBLE);
    }

    public void currentTrendClick(View view) {
        detectStatus.setVisibility(View.GONE);
        deviceParamB.setTextColor(getResources().getColor(R.color.white));
        currentTrendB.setTextColor(getResources().getColor(R.color.black));
        detectCurve.setVisibility(View.VISIBLE);
    }
*/
    private boolean isConnected() {
        if (GlobalParam.isConnected) {
            return true;
        }
        Toast.makeText(this, "蓝牙未连接", Toast.LENGTH_SHORT).show();
        return false;
    }

    /**
     * 数据显示
     *
     * @param paramBytes 接收的数据
     */
    private void showFragmentContent(byte[] paramBytes) {
        //TODO..数据处理：paramBytes给到deviceController
        double systemCurrent = deviceController.getSystemCurrent();
        if (!unitPPM) {
            systemCurrent = UnitManager.getMg(systemCurrent);
        }

        //展示仪器参数或当前趋势
//        if (detectStatus.getVisibility() == View.VISIBLE) {
            showStatus();
            cacheCurveData(systemCurrent);
//        } else {
//            showCurveData(systemCurrent);
//            return;
//        }
        detectValueET.setText(new DecimalFormat("0.0").format(systemCurrent));
        showValueBySeriesLimit(systemCurrent);
        showMax(systemCurrent);
    }

    @SuppressLint("SetTextI18n")
    private void showStatus() {
        DecimalFormat localDecimalFormat = new DecimalFormat("0.00");
        detectParamPower.setText(localDecimalFormat.format(deviceController.getPowerPercent()) + "%");
        detectParamVol.setText(new DecimalFormat("0.000").format(deviceController.getVol()));
        detectParamHydrogen.setText(localDecimalFormat.format(deviceController.getHydrogenPressPercent()) + "%");
        detectParamHydrogenPress.setText(String.valueOf(deviceController.getHydrogenPress()));
        detectParamPump.setText(String.valueOf(deviceController.getPump()));
        detectParamCcTemp.setText(localDecimalFormat.format(deviceController.getCcTemp()));
        detectParamDischargePress.setText(localDecimalFormat.format(deviceController.getDischargePress()));
        detectParamFireTemp.setText(localDecimalFormat.format(deviceController.getFireTemp()));
        detectParamOutletHydrogenPress.setText(localDecimalFormat.format(deviceController.getOutletHydrogenPress()));
        detectParamMicroCurrent.setText(localDecimalFormat.format(deviceController.getMicroCurrent()));
        detectParamSystemCurrent.setText(localDecimalFormat.format(deviceController.getSystemCurrent()));
    }
/*
    public void showCurveData(double paramFloat) {
        cacheCurveData(paramFloat);
        List<Entry> localList = lineDataSet.getValues();
        detectCurveChart.getXAxis().setAxisMaximum(200.0F);
        detectCurveChart.getXAxis().setAxisMinimum(0.0F);
        detectCurveChart.getAxisLeft().setAxisMaximum((float) (maxValue * 1.1D));
        setData(localList);
        detectCurveChart.invalidate();
    }
*/
    private void cacheCurveData(double paramFloat) {
        if (minValue > paramFloat) {
            minValue = paramFloat;
        }
        if (maxValue < paramFloat) {
            maxValue = paramFloat;
        }
        List<Entry> entries;
        if (lineDataSet != null) {
            entries = lineDataSet.getValues();
        } else {
            entries = new ArrayList<>();
        }
        if (entries.size() >= 200) {
            entries.remove(0);
            int i = 0;
            while (i < entries.size()) {
                entries.get(i).setX(entries.get(i).getX() - 1.0F);
                i += 1;
            }
        }
        Entry entry = new Entry();
        entry.setX(entries.size() + 1);
        entry.setY((float) paramFloat);
        entries.add(entry);
        if (lineDataSet == null) {
            initDataSet(entries);
        }
    }

    private void initDataSet(List<Entry> paramList) {
        lineDataSet = new LineDataSet(paramList, "浓度趋势");
        lineDataSet.setMode(LineDataSet.Mode.LINEAR);
        lineDataSet.enableDashedLine(2.0F, 0.0F, 0.0F);
        lineDataSet.setColor(R.color.blue);
        lineDataSet.setLineWidth(1.5F);
        lineDataSet.setDrawCircles(false);
        lineDataSet.setCircleRadius(4.5F);
        lineDataSet.setValueTextSize(20.0F);
        lineDataSet.setDrawValues(false);
        lineDataSet.setValueTextColor(R.color.black);
        lineDataSet.setDrawFilled(false);
        lineDataSet.setFormLineWidth(1.0F);
        lineDataSet.setFormSize(15.0F);
        lineDataSet.setFillColor(R.color.blue);
    }
/*
    private void setData(List<Entry> paramList2) {
        if ((detectCurveChart.getData() != null) && (detectCurveChart.getData().getDataSetCount() > 0)) {
            ((LineDataSet) detectCurveChart.getLineData().getDataSetByIndex(0)).setValues(paramList2);
            detectCurveChart.notifyDataSetChanged();
            return;
        }
        detectCurveChart.setData(new LineData(lineDataSet));
    }
*/
    private void showValueBySeriesLimit(double paramDouble) {
        SeriesLimitInfo seriesLimitInfo = this.seriesLimitInfo;
        if (seriesLimitInfo != null) {
            if (paramDouble > seriesLimitInfo.getMaxValue()) {
                detectValueET.setTextColor(getResources().getColor(R.color.red));
                return;
            }
            detectValueET.setTextColor(getResources().getColor(R.color.black));
        }
    }

    /**
     * 最大值
     *
     * @param systemCurrent 系统电流
     */
    private void showMax(double systemCurrent) {
        detectMaxvalueET.setText(new DecimalFormat("0.0").format(systemCurrent));
        if (unitPPM) {
            if (systemCurrent > detectMaxvaluePPM) {
                detectMaxvaluePPM = systemCurrent;
            }
        } else {
            if (systemCurrent > detectMaxvalueMg) {
                detectMaxvalueMg = systemCurrent;
            }
        }
    }

    public void selectSeries(View view) {
        try {
            SeriesDialog seriesDialog = new SeriesDialog(DetectActivity.this, "选择工作曲线");
            seriesDialog.setSeriesAddSuccessCallback(seriesInfo -> {
                CalibrationInfoController.getInstance().setCurrentSeries(seriesInfo);
                showSeriesName();
                if (seriesInfo != null) {
                    SPUtil.put(DetectActivity.this, "DetectSeries", seriesInfo.getSeriesName());
                }
            });
            seriesDialog.showDialog();
        } catch (Exception e) {
            LogUtil.warnOut(TAG, e, "");
        }

    }

    public void selectFactor(View view) {
        try {
            if (CalibrationInfoController.getInstance().getCurrentSeries() != null) {
                if (CalibrationInfoController.getInstance().getCurrentSeries().isStdSeries()) {
                    FactorDialog factorDialog = new FactorDialog(DetectActivity.this, "选择响应因子");
                    factorDialog.setFactorAddSuccessCallback(new FactorAddSuccessCallback() {
                        @Override
                        public void onSave(FactorCoefficientInfo factorCoefficientInfo) {
                            UnitManager.changeFactor(factorCoefficientInfo);
                            deviceController.changeFactor(factorCoefficientInfo);
                            showFactorName();
                            if (factorCoefficientInfo == null) {
                                SPUtil.put(DetectActivity.this, "DetectFactor", -1);
                                return;
                            }
                            SPUtil.put(DetectActivity.this, "DetectFactor", String.valueOf(factorCoefficientInfo.getId()));
                        }
                    });
                    factorDialog.showDialog();
                }
            }
        } catch (Exception e) {
            LogUtil.warnOut(TAG, e, "");
        }
    }
}
