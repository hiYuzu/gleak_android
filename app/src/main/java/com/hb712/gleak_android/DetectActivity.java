package com.hb712.gleak_android;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;

import com.hb712.gleak_android.base.BaseActivity;
import com.hb712.gleak_android.callback.FactorAddSuccessCallback;
import com.hb712.gleak_android.controller.CalibrationInfoController;
import com.hb712.gleak_android.controller.DeviceController;
import com.hb712.gleak_android.dao.DBManager;
import com.hb712.gleak_android.dao.FactorCoefficientInfoDao;
import com.hb712.gleak_android.dao.SeriesLimitInfoDao;
import com.hb712.gleak_android.dialog.FactorDialog;
import com.hb712.gleak_android.dialog.SeriesDialog;
import com.hb712.gleak_android.interfaceabs.HttpInterface;
import com.hb712.gleak_android.interfaceabs.OKHttpListener;
import com.hb712.gleak_android.message.net.LeakData;
import com.hb712.gleak_android.message.net.MonitorData;
import com.hb712.gleak_android.pojo.FactorCoefficientInfo;
import com.hb712.gleak_android.pojo.SeriesInfo;
import com.hb712.gleak_android.pojo.SeriesLimitInfo;
import com.hb712.gleak_android.rtsp.RtspPlayer;
import com.hb712.gleak_android.util.BluetoothUtil;
import com.hb712.gleak_android.util.ByteArrayConvertUtil;
import com.hb712.gleak_android.util.ToastUtil;
import com.hb712.gleak_android.util.GlobalParam;
import com.hb712.gleak_android.util.HttpUtils;
import com.hb712.gleak_android.util.LogUtil;
import com.hb712.gleak_android.util.SPUtil;
import com.hb712.gleak_android.util.UnitManager;

import org.greenrobot.greendao.query.WhereCondition;

import java.io.File;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DetectActivity extends BaseActivity implements HttpInterface {

    private static final String TAG = DetectActivity.class.getSimpleName();

    private Button startRecordBtn;
    private Button uploadVideoBtn;

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

    @Override
    public DetectActivity getActivity() {
        return this;
    }

    @Override
    public boolean isDiscardHttp() {
        return isFinishing();
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
        startRecordBtn = findViewById(R.id.startRecordBtn);
        startRecordBtn.setOnClickListener((p) -> {
            Intent intent = new Intent(getApplicationContext(), LeakMapActivity.class);
            startActivityForResult(intent, GlobalParam.REQUEST_LEAK_DATA);
        });
        uploadVideoBtn = findViewById(R.id.uploadVideoBtn);
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
    }

    /**
     * 初始化蓝牙
     */
    private void initBluetooth() {
        if (!mBluetooth.isBluetoothAvailable()) {
            ToastUtil.longInstanceToast("蓝牙不可用");
            finish();
        }

        mBluetooth.setBluetoothConnectionListener(new BluetoothUtil.BluetoothConnectionListener() {
            @Override
            public void onDeviceConnected(String name, String address) {
                GlobalParam.isConnected = true;
                detectConnectB.setText(R.string.detect_disconnect);
                connDeviceTV.setText(name);
                //TODO..线程循环请求/接收仪器参数
                ToastUtil.shortInstanceToast("蓝牙已连接");
            }

            @Override
            public void onDeviceDisconnected() {
                GlobalParam.isConnected = false;
                detectConnectB.setText(R.string.detect_connect);
                connDeviceTV.setText(R.string.detect_disconnected);
                //TODO..释放线程
                ToastUtil.shortInstanceToast("蓝牙已断开");
            }

            @Override
            public void onDeviceConnectionFailed() {
                ToastUtil.shortInstanceToast("蓝牙连接失败");
            }
        });

        mBluetooth.setOnDataReceivedListener((data, message) -> {
            System.out.println("接收到蓝牙信息:" + Arrays.toString(data));
            float measureValue = 0;
            float electricValue = 0;
            byte[] byteBit = new byte[8];
            boolean statusP = false;
            boolean statusA = false;
            boolean statusB = false;
            if (data != null && data.length > 38) {//读取测量值
                measureValue = ByteArrayConvertUtil.byteToFloat(data, 32);
                electricValue = ByteArrayConvertUtil.byteToFloat(data, 24);
                byteBit = ByteArrayConvertUtil.getBooleanArray(data[4]);
                if (byteBit[0] == 1) {
                    statusP = true;
                } else {
                    statusP = false;
                }
                if (byteBit[2] == 1) {
                    statusA = true;
                } else {
                    statusA = false;
                }
                if (byteBit[3] == 1) {
                    statusB = true;
                } else {
                    statusB = false;
                }
            }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            moveTaskToBack(true);
            return false;
        }
        return super.onOptionsItemSelected(item);
    }

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
            // 获取蓝牙设备
            if (resultCode == Activity.RESULT_OK) {
                mBluetooth.connect(data);
            }
        } else if (requestCode == GlobalParam.REQUEST_ENABLE_BT) {
            // 请求打开蓝牙
            if (resultCode == Activity.RESULT_OK) {
                mBluetooth.setupService();
            } else {
                finish();
            }
        } else if (requestCode == GlobalParam.REQUEST_LEAK_DATA) {
            // 获取漏点数据
            if (resultCode == Activity.RESULT_OK && data.getExtras() != null) {
                startRecord(data.getExtras());
            } else {
                ToastUtil.shortInstanceToast("获取漏点信息失败，请重试");
            }
        }
    }

    private void startRecord(Bundle bundle) {
        if (isConnected()) {
            if (!mRtspPlayer.isRecording()) {
                mRtspPlayer.startRecord();
                startRecordBtn.setText(getResources().getText(R.string.stopRecord));
            } else {
                mRtspPlayer.stopRecord();
                startRecordBtn.setText(getResources().getText(R.string.startRecord));
                // 保存漏点信息、检测数据、视频信息
                saveData(bundle);

                if (MainApplication.getInstance().isLogin()) {
                    uploadVideoBtn.setEnabled(true);
                }
            }
        }
    }

    private void saveData(Bundle bundle) {
        LeakData leakData = (LeakData) bundle.get(GlobalParam.LEAK_DATA);
        // TODO..保存到本地
    }

    public void uploadVideo(View view) {
        // TODO..从本地数据库中取出最新的一条上传
        /*
        NewLeakRequest newLeakRequest = new NewLeakRequest();
        Double lat = null;
        Double lon = null;
        GPSUtil gpsUtil = GPSUtil.getInstance(this);
        if(gpsUtil.isLocationProviderEnabled()){
            android.location.Location location = null;
            if (!gpsUtil.isLocationPermission()) {
                //判断是否为android6.0系统版本，如果是，需要动态添加权限
                ActivityCompat.requestPermissions(this, gpsUtil.permissions,100);
            } else {
                location = gpsUtil.getLocation();
            }
            if(location != null){
                lon = location.getLongitude();
                lat = location.getLatitude();
            }
        }
        newLeakRequest.setLocation(new Location.Builder().lon(lon).lat(lat).build());
         */
        LeakData leakData = new LeakData("1", MainApplication.getInstance().getUserId(), new MonitorData("2020-12-01 10:52:51", 100, 1));
        HttpUtils.postVideo(this, MainApplication.getInstance().baseUrl + "/video/insert", leakData.toString(), new File(mRtspPlayer.getVideoPath()),
                new OKHttpListener() {
                    @Override
                    public void onStart() {
                        ToastUtil.shortInstanceToast("开始上传...");
                    }

                    @Override
                    public void onSuccess(Bundle bundle) {
                        ToastUtil.shortInstanceToast("上传成功");
                    }

                    @Override
                    public void onServiceError(Bundle bundle) {
                        ToastUtil.shortInstanceToast(bundle.getString(HttpUtils.MESSAGE));
                    }

                    @Override
                    public void onNetworkError(Bundle bundle) {
                        ToastUtil.shortInstanceToast(bundle.getString(HttpUtils.MESSAGE));
                    }
                });
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
            mBluetooth.writeByte(1);
        }
    }

    /**
     * 点火2
     *
     * @param view
     */
    public void fireClick2(View view) {
        if (isConnected()) {
            mBluetooth.writeByteRead();
        }
    }

    /**
     * 关火
     *
     * @param view
     */
    public void ceasefireClick(View view) {
        if (isConnected()) {
            mBluetooth.writeByte(0);
        }
    }

    private boolean isConnected() {
        if (GlobalParam.isConnected) {
            return true;
        }
        ToastUtil.shortInstanceToast("蓝牙未连接");
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

        //展示仪器参数
        showStatus();
        cacheCurveData(systemCurrent);
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
