package com.hb712.gleak_android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;

import com.hb712.gleak_android.base.BaseActivity;
import com.hb712.gleak_android.controller.CalibrationInfoController;
import com.hb712.gleak_android.controller.DeviceController;
import com.hb712.gleak_android.dao.DBManager;
import com.hb712.gleak_android.dao.FactorCoefficientInfoDao;
import com.hb712.gleak_android.dao.SeriesLimitInfoDao;
import com.hb712.gleak_android.dialog.CommonDialog;
import com.hb712.gleak_android.dialog.FactorDialog;
import com.hb712.gleak_android.dialog.SeriesDialog;
import com.hb712.gleak_android.entity.DetectInfo;
import com.hb712.gleak_android.interfaceabs.HttpInterface;
import com.hb712.gleak_android.interfaceabs.OKHttpListener;
import com.hb712.gleak_android.message.net.LeakData;
import com.hb712.gleak_android.message.net.MonitorData;
import com.hb712.gleak_android.message.net.NewLeak;
import com.hb712.gleak_android.entity.FactorCoefficientInfo;
import com.hb712.gleak_android.message.net.InitLeakData;
import com.hb712.gleak_android.entity.SeriesInfo;
import com.hb712.gleak_android.entity.SeriesLimitInfo;
import com.hb712.gleak_android.rtsp.RtspPlayer;
import com.hb712.gleak_android.util.BluetoothUtil;
import com.hb712.gleak_android.util.DateUtil;
import com.hb712.gleak_android.util.ThreadPoolUtil;
import com.hb712.gleak_android.util.ToastUtil;
import com.hb712.gleak_android.util.GlobalParam;
import com.hb712.gleak_android.util.HttpUtils;
import com.hb712.gleak_android.util.LogUtil;
import com.hb712.gleak_android.util.SPUtil;
import com.hb712.gleak_android.util.UnitUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import androidx.annotation.RequiresApi;

public class DetectActivity extends BaseActivity implements HttpInterface {

    private static final String TAG = DetectActivity.class.getSimpleName();

    private EditText videoIpEdit;
    private ImageView fireImage;
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

    private double minValue = Double.MAX_VALUE;
    private double maxValue = Double.MIN_VALUE;

    private double saveMaxValue = 0;
    private String saveTime;
    private boolean isSaving = false;

    private String selectedLeakId;
    private String selectedLeakName;

    private LeakData lastedLeakData;

    private BluetoothUtil mBluetooth;
    private DeviceController deviceController;
    private SeriesLimitInfo seriesLimitInfo;
    private boolean unitPPM = true;
    private double detectMaxvaluePPM = 0;
    private double detectMaxvalueMg = 0;

    private RtspPlayer mRtspPlayer;
    private MainApplication mainApp;
    private Future<?> future;

    private final String videoUrl = "videoUrl";
    private final String defaultVideoUrl = "192.168.3.137";

    private int saveMode;
    private enum SaveMode {
        /**
         * 仅保存上传视频
         */
        ONLY_VIDEO(2),
        /**
         * 仅保存上传数据
         */
        ONLY_DETECT(1),
        /**
         * 视频 + 数据
         */
        ALL_DATA(0);

        private final int value;

        SaveMode(int value) {
            this.value = value;
        }

        private int getValue() {
            return value;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect);
        setupActionBar();
        mainApp = MainApplication.getInstance();
        mBluetooth = mainApp.mBluetooth;
        initView();
        initBluetooth();
        initClass();
        initSeriesInfo();
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
        videoIpEdit = findViewById(R.id.videoIp);
        videoIpEdit.setText((String) SPUtil.get(this, videoUrl, defaultVideoUrl));
        fireImage = findViewById(R.id.ioFire);
        startRecordBtn = findViewById(R.id.startRecordBtn);
        startRecordBtn.setOnClickListener((p) -> {
            if (!mainApp.isLogin()) {
                ToastUtil.longToastShow("当前未登录");
                return;
            }
            if (!deviceController.isFireOn() && !mRtspPlayer.isPlaying()) {
                ToastUtil.longToastShow("当前无视频信号且设备未点火");
                return;
            }
            if (!isSaving) {
                Intent intent = new Intent(getApplicationContext(), LeakMapActivity.class);
                startActivityForResult(intent, GlobalParam.REQUEST_LEAK_DATA);
            } else {
                stopRecord();
            }
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
            ToastUtil.longToastShow("蓝牙不可用");
            finish();
        }


        mBluetooth.addBluetoothListener("", new BluetoothUtil.BluetoothListener() {
            @Override
            public void onDeviceConnected(String name, String address) {
                GlobalParam.isConnected = true;
                future = ThreadPoolUtil.getInstance().scheduledCommonExecute(() -> mBluetooth.readData(), 100, 500, TimeUnit.MILLISECONDS);
                detectConnectB.setText(R.string.detect_disconnect);
                connDeviceTV.setText(name);
                deviceController.setDeviceName(name);
                ToastUtil.longToastShow("蓝牙已连接");
            }

            @Override
            public void onDeviceDisconnected() {
                GlobalParam.isConnected = false;
                if (future != null) {
                    future.cancel(true);
                }
                detectConnectB.setText(R.string.detect_connect);
                connDeviceTV.setText(R.string.detect_disconnected);
                ToastUtil.longToastShow("蓝牙已断开");
            }

            @Override
            public void onDataReceived(byte[] data) {
                showFragmentContent(data);
            }
        });
    }

    private void initClass() {
        deviceController = DeviceController.getInstance();
    }

    /**
     * 初始化曲线信息（工作曲线和响应因子）
     */
    private void initSeriesInfo() {
        String seriesName = (String) SPUtil.get(this, "DetectSeries", "标准曲线");
        try {
            CalibrationInfoController.getInstance().setCurrentSeries(seriesName);
            Long id = (Long) SPUtil.get(this, "DetectFactor", 0L);
            List<FactorCoefficientInfo> factorCoefficientInfoList = DBManager.getInstance().getReadableSession().getFactorCoefficientInfoDao().queryBuilder().where(FactorCoefficientInfoDao.Properties.ID.eq(id), new WhereCondition[0]).list();
            if (factorCoefficientInfoList != null && factorCoefficientInfoList.size() > 0) {
                UnitUtil.changeFactor(factorCoefficientInfoList.get(0));
                deviceController.changeFactor(factorCoefficientInfoList.get(0));
            }
            showSeriesName();
            showFactorName();
        } catch (Exception e) {
            LogUtil.warnOut(TAG, e, null);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadRtsp() {
        mRtspPlayer = new RtspPlayer();
        mRtspPlayer.init(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();
        showSeriesName();
        mRtspPlayer.startPlay((String) SPUtil.get(this, videoUrl, defaultVideoUrl));
    }

    @Override
    protected void onPause() {
        super.onPause();
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

    @Override
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void refreshVideoClick(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(videoIpEdit.getWindowToken(), 0);
        videoIpEdit.clearFocus();
        ToastUtil.shortToastShow("刷新中...");
        String url = videoIpEdit.getText().toString();
        SPUtil.put(this, videoUrl, url);
        mRtspPlayer.startPlay(url);
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
            if (data == null) {
                return;
            }
            if (data.getExtras() == null) {
                ToastUtil.longToastShow("获取漏点信息失败，请重试");
                return;
            }
            // 获取漏点数据
            if (resultCode == Activity.RESULT_FIRST_USER) {
                addNewLeak(data.getExtras());
            } else if (resultCode == Activity.RESULT_OK) {
                selectedLeakId = data.getExtras().getString("leakId");
                selectedLeakName = data.getExtras().getString("leakName");
                startSave();
            } else {
                ToastUtil.longToastShow("获取漏点信息失败，请重试");
            }
        }
    }

    private void addNewLeak(Bundle bundle) {
        NewLeak newLeak = (NewLeak) bundle.getSerializable("NewLeak");
        if (newLeak == null) {
            return;
        }
        try {
            HttpUtils.post(this, mainApp.baseUrl + "/api/monitor/insert", newLeak.toMap(), new OKHttpListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onSuccess(Bundle bundle) {
                    String result = bundle.getString(HttpUtils.MESSAGE);
                    JSONObject json = JSONObject.parseObject(result);
                    if (json.getBoolean("status")) {
                        selectedLeakId = json.getString("data");
                        selectedLeakName = newLeak.getName();
                        GlobalParam.initLeakData.add(new InitLeakData(selectedLeakId, selectedLeakName, newLeak.getCode(), newLeak.getLongitude(), newLeak.getLatitude()));
                        startSave();
                    } else {
                        LogUtil.infoOut(TAG, json.getString("msg"));
                    }
                }

                @Override
                public void onServiceError(Bundle bundle) {
                    ToastUtil.longToastShow(Objects.requireNonNull(bundle.getString(HttpUtils.MESSAGE)));
                }

                @Override
                public void onNetworkError(Bundle bundle) {
                    ToastUtil.longToastShow(Objects.requireNonNull(bundle.getString(HttpUtils.MESSAGE)));
                }
            });
        } catch (NullPointerException npe) {
            LogUtil.errorOut(TAG, npe, null);
        }

    }

    private void startSave() {
        if (deviceController.isFireOn() && mRtspPlayer.isPlaying()) {
            // 视频 + 数据
            startRecord();
            saveMode = SaveMode.ALL_DATA.getValue();
        } else if (deviceController.isFireOn()){
            // 数据
            isSaving = true;
            startRecordBtn.setText(R.string.stopRecord);
            saveMode = SaveMode.ONLY_DETECT.getValue();
        } else {
            // 视频
            startRecord();
            saveMode = SaveMode.ONLY_VIDEO.getValue();
        }
    }

    private void startRecord() {
        if (mRtspPlayer.startRecord() == 0) {
            isSaving = true;
            startRecordBtn.setText(R.string.stopRecord);
        } else {
            ToastUtil.longToastShow("无法开启录制");
        }
    }

    private void stopRecord() {
        if (mRtspPlayer.isRecording()) {
            mRtspPlayer.stopRecord();
        }
        isSaving = false;
        startRecordBtn.setText(getText(R.string.startRecord));
        // 保存漏点信息、检测数据、视频信息
        saveData();
    }

    private void saveData() {
        if (saveMode == SaveMode.ONLY_VIDEO.getValue()) {
            saveMaxValue = -1.0D;
            saveTime = DateUtil.getDefaultTime();
            CommonDialog.infoDialog(this, "测量结束：\n时间：" + saveTime + "\n漏点名：" + selectedLeakName + "\n最大值：无数据");
        } else {
            saveMaxValue = Double.parseDouble(new DecimalFormat("0.00").format(saveMaxValue));
            CommonDialog.infoDialog(this, "测量结束：\n时间：" + saveTime + "\n漏点名：" + selectedLeakName + "\n最大值：" + saveMaxValue);
        }

        if (seriesLimitInfo == null) {
            ToastUtil.longToastShow("无限值数据，本次测量不保存");
            LogUtil.warnOut(TAG, null, "曲线信息为空");
            return;
        }

        // 如果已登录，则上传可用
        if (mainApp.isLogin()) {
            uploadVideoBtn.setEnabled(true);
        }

        String userId = mainApp.getUserId();
        boolean standard = saveMaxValue <= seriesLimitInfo.getMaxValue();
        int monitorStatus = saveMaxValue > seriesLimitInfo.getMaxValue() ? 0 : 1;

        MonitorData monitorData = new MonitorData(saveTime, saveMaxValue, monitorStatus);
        lastedLeakData = new LeakData(selectedLeakId, userId, monitorData);
        try {
            DetectInfo detectInfo = new DetectInfo();
            detectInfo.setLeakName(selectedLeakName);
            detectInfo.setMonitorTime(saveTime);
            detectInfo.setStandard(standard);
            detectInfo.setOptUser(Long.parseLong(userId));
            detectInfo.setMonitorValue(saveMaxValue);
            if (saveMode == SaveMode.ONLY_DETECT.getValue()) {
                detectInfo.setVideoPath(null);
            } else {
                detectInfo.setVideoPath(mRtspPlayer.getVideoPath());
            }
            DBManager.getInstance().getWritableSession().getDetectInfoDao().save(detectInfo);
        } catch (NumberFormatException nfe) {
            ToastUtil.longToastShow("用户id解析失败");
        } catch (Exception e) {
            ToastUtil.longToastShow("本地数据库发生错误！");
            LogUtil.assertOut(TAG, e, "DetectInfoDao");
        }
        saveMaxValue = 0;
    }

    public void uploadVideo(View view) {
        OKHttpListener mListener = new OKHttpListener() {
            @Override
            public void onStart() {
                ToastUtil.longToastShow("开始上传...");
            }

            @Override
            public void onSuccess(Bundle bundle) {
                ToastUtil.longToastShow("上传成功");
                uploadVideoBtn.setEnabled(false);
            }

            @Override
            public void onServiceError(Bundle bundle) {
                ToastUtil.longToastShow(Objects.requireNonNull(bundle.getString(HttpUtils.MESSAGE)));
            }

            @Override
            public void onNetworkError(Bundle bundle) {
                ToastUtil.longToastShow(Objects.requireNonNull(bundle.getString(HttpUtils.MESSAGE)));
            }
        };
        String httpUrl;
        try {
            if (saveMode != SaveMode.ONLY_DETECT.getValue()) {
                httpUrl = mainApp.baseUrl + "/video/insert";
                HttpUtils.post(this, httpUrl, lastedLeakData, new File(mRtspPlayer.getVideoPath()), mListener);
            } else {
                httpUrl = mainApp.baseUrl + "/api/monitorData/insertWithNullVideoData";
                HttpUtils.post(this, httpUrl, lastedLeakData.toMap(), mListener);
            }
        } catch (NullPointerException npe) {
            LogUtil.errorOut(TAG, npe, null);
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
                List<SeriesLimitInfo> seriesLimitInfoList = DBManager.getInstance().getReadableSession().getSeriesLimitInfoDao().queryBuilder().where(SeriesLimitInfoDao.Properties.SERIES_ID.eq(currentSeries.getId()), new WhereCondition[0]).list();
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
            ToastUtil.longToastShow("点火中...");
            mBluetooth.openFire();
        }
    }

    /**
     * 点火2
     *
     * @param view
     */
    public void fireClick2(View view) {
        if (isConnected()) {
            ToastUtil.longToastShow("点火中...");
            mBluetooth.openFire2();
        }
    }

    /**
     * 关火
     *
     * @param view
     */
    public void ceasefireClick(View view) {
        if (isConnected()) {
            mBluetooth.closeFire();
        }
    }

    boolean lastFireStatus = false;

    private void changeFirePic(boolean isFire) {
        if (lastFireStatus == isFire) {
            return;
        }
        lastFireStatus = isFire;
        if (isFire) {
            ToastUtil.longToastShow("点火成功");
            fireImage.setImageResource(R.drawable.fire_on);
        } else {
            ToastUtil.longToastShow("已关火");
            fireImage.setImageResource(R.drawable.fire_off);
        }
    }

    private boolean isConnected() {
        if (GlobalParam.isConnected) {
            return true;
        }
        ToastUtil.longToastShow("蓝牙未连接");
        return false;
    }

    /**
     * 数据显示
     *
     * @param paramBytes 接收的数据
     */
    private void showFragmentContent(byte[] paramBytes) {
        deviceController.analysisCommand(paramBytes);
        changeFirePic(deviceController.isFireOn());
        double currentPpm = deviceController.getCurrentValue();
        cacheCurveData(currentPpm);
        if (!unitPPM) {
            currentPpm = UnitUtil.getMg(currentPpm);
        }
        //展示仪器参数
        showStatus();
        showValueBySeriesLimit(currentPpm);
        showMax(currentPpm);
    }

    @SuppressLint("SetTextI18n")
    private void showStatus() {
        DecimalFormat df = new DecimalFormat("0.00");
        detectParamPower.setText(df.format(deviceController.getPowerPercent()) + "%");
        detectParamVol.setText(new DecimalFormat("0.000").format(deviceController.getVol()));
        detectParamHydrogen.setText(df.format(deviceController.getHydrogenPressPercent()) + "%");
        detectParamHydrogenPress.setText(String.valueOf(deviceController.getHydrogenPress()));
        detectParamPump.setText(String.valueOf(deviceController.getPump()));
        detectParamCcTemp.setText(df.format(deviceController.getCcTemp()));
        detectParamDischargePress.setText(df.format(deviceController.getDischargePress()));
        detectParamFireTemp.setText(df.format(deviceController.getFireTemp()));
        detectParamOutletHydrogenPress.setText(df.format(deviceController.getOutletHydrogenPress()));
        detectParamMicroCurrent.setText(df.format(deviceController.getMicroCurrent()));
        detectParamSystemCurrent.setText(df.format(deviceController.getSystemCurrent()));
    }

    private void cacheCurveData(double currentPpm) {
        if (minValue > currentPpm) {
            minValue = currentPpm;
        }
        if (maxValue < currentPpm) {
            maxValue = currentPpm;
        }
        if (isSaving && saveMode != SaveMode.ONLY_VIDEO.getValue()) {
            if (saveMaxValue < currentPpm) {
                saveMaxValue = currentPpm;
            }
            saveTime = DateUtil.getDefaultTime();
        }
    }

    private void showValueBySeriesLimit(double currentPpm) {
        detectValueET.setText(new DecimalFormat("0.00").format(currentPpm));
        if (seriesLimitInfo != null) {
            if (currentPpm > seriesLimitInfo.getMaxValue()) {
                detectValueET.setTextColor(getResources().getColor(R.color.red, null));
                return;
            }
            detectValueET.setTextColor(getResources().getColor(R.color.black, null));
        }
    }

    /**
     * 最大值
     *
     * @param currentPpm 系统检测值
     */
    private void showMax(double currentPpm) {
        if (unitPPM) {
            if (currentPpm > detectMaxvaluePPM) {
                detectMaxvaluePPM = currentPpm;
            }
        } else {
            if (currentPpm > detectMaxvalueMg) {
                detectMaxvalueMg = currentPpm;
            }
        }
        detectMaxvalueET.setText(new DecimalFormat("0.00").format(detectMaxvaluePPM));
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
                    factorDialog.setFactorAddSuccessCallback(factorCoefficientInfo -> {
                        UnitUtil.changeFactor(factorCoefficientInfo);
                        deviceController.changeFactor(factorCoefficientInfo);
                        showFactorName();
                        if (factorCoefficientInfo == null) {
                            SPUtil.put(DetectActivity.this, "DetectFactor", -1);
                            return;
                        }
                        SPUtil.put(DetectActivity.this, "DetectFactor", String.valueOf(factorCoefficientInfo.getId()));
                    });
                    factorDialog.showDialog();
                }
            }
        } catch (Exception e) {
            LogUtil.warnOut(TAG, e, "");
        }
    }
}
