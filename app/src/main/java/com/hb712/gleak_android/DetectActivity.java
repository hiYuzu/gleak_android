package com.hb712.gleak_android;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;

import com.hb712.gleak_android.base.BaseActivity;
import com.hb712.gleak_android.controller.CalibrationInfoController;
import com.hb712.gleak_android.controller.DeviceController;
import com.hb712.gleak_android.dao.DBManager;
import com.hb712.gleak_android.dao.FactorCoefficientInfoDao;
import com.hb712.gleak_android.dao.SeriesLimitInfoDao;
import com.hb712.gleak_android.dialog.CommonDialog;
import com.hb712.gleak_android.dialog.FactorDialog;
import com.hb712.gleak_android.dialog.SeriesDialog;
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
import com.hb712.gleak_android.util.ByteArrayConvertUtil;
import com.hb712.gleak_android.util.ToastUtil;
import com.hb712.gleak_android.util.GlobalParam;
import com.hb712.gleak_android.util.HttpUtils;
import com.hb712.gleak_android.util.LogUtil;
import com.hb712.gleak_android.util.SPUtil;
import com.hb712.gleak_android.util.UnitManager;

import org.greenrobot.greendao.query.WhereCondition;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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

    private double saveMaxValue = 0;
    private String saveTime;
    private boolean isSaving = false;

    private String selectedLeakId;

    private LeakData lastedLeakData;

    //other
    private BluetoothUtil mBluetooth;
    private DeviceController deviceController;
    private SeriesLimitInfo seriesLimitInfo;
    private boolean unitPPM = true;
    private double detectMaxvaluePPM = 0;
    private double detectMaxvalueMg = 0;

    private RtspPlayer mRtspPlayer;

    private TimerTask timerTask;
    private Timer timer;
    private int timerTime = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect);
        setupActionBar();
        mBluetooth = MainApplication.getInstance().mBluetooth;
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
            if (!MainApplication.getInstance().isLogin()) {
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
            ToastUtil.longInstanceToast("蓝牙不可用");
            finish();
        }

        mBluetooth.addBluetoothListener("", new BluetoothUtil.BluetoothListener() {
            @Override
            public void onDeviceConnected(String name, String address) {
                GlobalParam.isConnected = true;
                detectConnectB.setText(R.string.detect_disconnect);
                connDeviceTV.setText(name);
                ToastUtil.shortInstanceToast("蓝牙已连接");
                startReadTask();
            }

            @Override
            public void onDeviceDisconnected() {
                GlobalParam.isConnected = false;
                detectConnectB.setText(R.string.detect_connect);
                connDeviceTV.setText(R.string.detect_disconnected);
                ToastUtil.shortInstanceToast("蓝牙已断开");
                stopReadTask();
            }

            @Override
            public void onDataReceived(byte[] data) {
                //仪器参数
                showFragmentContent(data);
            }
        });
    }

    /**
     * 开启读取线程
     */
    private void startReadTask() {
        if (this.timerTask != null) {
            this.timerTask.cancel();
        }
        this.timerTask = new TimerTask() {
            public void run() {
                mBluetooth.readData();
            }
        };
        if (this.timer == null) {
            this.timer = new Timer();
        }
        this.timer.purge();
        this.timer.schedule(this.timerTask, this.timerTime, this.timerTime);
    }

    /**
     * 关闭读取线程
     */
    private void stopReadTask() {
        if (this.timer != null) {
            this.timer.cancel();
            this.timer = null;
        }
        if (this.timerTask != null) {
            this.timerTask.cancel();
            this.timerTask = null;
        }
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

        CommonDialog.getDialog(this, "视频ip地址", null, ipEdit, () -> GlobalParam.VIDEO_URL = ipEdit.getText().toString()).show();
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
            Bundle bundle = data.getExtras();
            if (bundle == null) {
                ToastUtil.shortInstanceToast("获取漏点信息失败，请重试");
                return;
            }
            // 获取漏点数据
            if (resultCode == Activity.RESULT_FIRST_USER) {
                addNewLeak(data.getExtras());
            } else if (resultCode == Activity.RESULT_OK) {
                startSave(data.getExtras());
            } else {
                ToastUtil.shortInstanceToast("获取漏点信息失败，请重试");
            }
        }
    }

    private void addNewLeak(Bundle bundle) {
        NewLeak newLeak = (NewLeak) bundle.getSerializable("NewLeak");
        if (newLeak == null) {
            return;
        }
        HttpUtils.post(this, MainApplication.getInstance().baseUrl + "/api/monitor/insert", newLeak.toMap(), new OKHttpListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(Bundle bundle) {
                String result = bundle.getString(HttpUtils.MESSAGE);
                JSONObject json = JSONObject.parseObject(result);
                if (json.getBoolean("status")) {
                    selectedLeakId = json.getString("data");
                    GlobalParam.initLeakData.add(new InitLeakData(selectedLeakId, newLeak.getName(), newLeak.getCode(), newLeak.getLongitude(), newLeak.getLatitude()));
                    startSave();
                } else {
                    LogUtil.infoOut(TAG, json.getString("msg"));
                }
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

    private void startSave() {
        if (isConnected() && startRecord()) {
            startRecordBtn.setText(R.string.stopRecord);
        }
    }

    private void startSave(@NonNull Bundle bundle) {
        if (isConnected() && startRecord()) {
            selectedLeakId = bundle.getString("leakId");
            startRecordBtn.setText(R.string.stopRecord);
        }
    }

    private boolean startRecord() {
        if (mRtspPlayer.startRecord() == 0) {
            isSaving = true;
            startRecordBtn.setText(R.string.stopRecord);
        }
        return isSaving;
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
        // TODO: hiYuzu 2020/12/4 取消注释 
//        if (seriesLimitInfo == null) {
//            LogUtil.warnOut(TAG, null, "曲线信息为空");
//            return;
//        }

        // 如果已登录，则上传可用
        if (MainApplication.getInstance().isLogin()) {
            uploadVideoBtn.setEnabled(true);
        }


        String leakId = selectedLeakId;
        String userId = MainApplication.getInstance().getUserId();
        // TODO: hiYuzu 2020/12/4 超标状态由当前选择的限值决定
//        int monitorStatus = saveMaxValue > seriesLimitInfo.getMaxValue() ? 0 : 1;
        int monitorStatus = 1;
        MonitorData monitorData = new MonitorData(saveTime, saveMaxValue, monitorStatus);
        lastedLeakData = new LeakData(leakId, userId, monitorData);
        System.out.println(lastedLeakData.toString());
        System.out.println(mRtspPlayer.getVideoPath());

        // TODO: hiYuzu 2020/12/2 保存到本地数据库 表：leak_data_info
        // | id | leak_id | monitor_value | monitor_time | monitor_status | video_path | opt_user | opt_time |
    }

    public void uploadVideo(View view) {
        HttpUtils.post(this, MainApplication.getInstance().baseUrl + "/video/insert", lastedLeakData.toString(), new File(mRtspPlayer.getVideoPath()),
                new OKHttpListener() {
                    @Override
                    public void onStart() {
                        ToastUtil.shortInstanceToast("开始上传...");
                    }

                    @Override
                    public void onSuccess(Bundle bundle) {
                        ToastUtil.shortInstanceToast("上传成功");
                        uploadVideoBtn.setEnabled(false);
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
            mBluetooth.openFire2();
//            mBluetooth.readData();
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
        mBluetooth.analysisCommand(paramBytes);
        // TODO: hiYuzu 2020/12/2 数据处理：paramBytes 给到 DeviceController 做进一步处理解析
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
        if (isSaving) {
            saveMaxValue = maxValue;
            saveTime = DateUtil.getDefaultTime();
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
                    factorDialog.setFactorAddSuccessCallback(factorCoefficientInfo -> {
                        UnitManager.changeFactor(factorCoefficientInfo);
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
