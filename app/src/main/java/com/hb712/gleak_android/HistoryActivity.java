package com.hb712.gleak_android;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.hb712.gleak_android.adapter.HistoryAdapter;
import com.hb712.gleak_android.dao.DBManager;
import com.hb712.gleak_android.dao.DetectInfoDao;
import com.hb712.gleak_android.entity.DetectInfo;
import com.hb712.gleak_android.util.DateUtil;
import com.hb712.gleak_android.util.SPUtil;
import com.hb712.gleak_android.util.UnitUtil;

import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private static final String TAG = HistoryActivity.class.getSimpleName();

    private EditText sampleName;
    private EditText detectDate;
    private TextView maxValue;
    private TextView minValue;
    private TextView avgValue;
    private ListView historyLV;

    private List<DetectInfo> detectInfoList;
    private HistoryAdapter historyAdapter;
    private boolean unitPpm = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        detectInfoList = new ArrayList<>();
        setupActionBar();
        initView();
//        initMediaScanner();
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
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        showLastData();
        super.onResume();
    }

    private void showLastData() {
        detectInfoList.clear();
        historyAdapter.clear();
        setStatisticValue(null, unitPpm);
        List<DetectInfo> detectInfoList = DBManager.getInstance().getReadableSession().getDetectInfoDao().queryBuilder().limit(100).list();
        if (detectInfoList != null && detectInfoList.size() > 0) {
            setStatisticValue(detectInfoList, unitPpm);
            this.detectInfoList.addAll(detectInfoList);
            historyAdapter.AddData(this.detectInfoList);
        }
        historyAdapter.notifyDataSetChanged();
    }

    private void setStatisticValue(List<DetectInfo> detectInfoList, boolean unitPpm) {
        String maxValue = "";
        String minValue = "";
        String avgValue = "";
        int size;
        if (detectInfoList != null && (size = detectInfoList.size()) > 0) {
            double max = 0.0D;
            double avg = 0.0D;
            double min = detectInfoList.get(0).getMonitorValue();
            int i = 0;
            while (i < size) {
                double monitorValue = detectInfoList.get(i).getMonitorValue();
                if (unitPpm) {
                    avg += monitorValue;
                } else {
                    avg += UnitUtil.getMg(monitorValue);
                }
                if (max < monitorValue) {
                    max = monitorValue;
                }
                if (min > monitorValue) {
                    min = monitorValue;
                }
                i += 1;
            }
            avg /= size;
            if (!unitPpm) {
                max = UnitUtil.getMg(max);
                min = UnitUtil.getMg(min);
            }
            DecimalFormat df = new DecimalFormat("0.0");
            maxValue = df.format(max);
            minValue = df.format(min);
            avgValue = df.format(avg);
        }
        this.maxValue.setText(maxValue);
        this.minValue.setText(minValue);
        this.avgValue.setText(avgValue);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        maxValue = findViewById(R.id.historyMaxValue);
        minValue = findViewById(R.id.historyMinValue);
        avgValue = findViewById(R.id.historyAvgValue);
        sampleName = findViewById(R.id.historyName);
        detectDate = findViewById(R.id.historyDate);
        detectDate.setOnTouchListener((v, event) -> {
            if (event.getAction() == 0) {
                showDatePickDialog();
                return true;
            }
            return false;
        });
        detectDate.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                showDatePickDialog();
            }
        });

        historyLV = findViewById(R.id.historyList);
        historyAdapter = new HistoryAdapter(this, detectInfoList);
        unitPpm = SPUtil.get(this, "unit", getResources().getString(R.string.detect_value_unit_ppm)).toString().equals(getResources().getString(R.string.detect_value_unit_ppm));
        historyAdapter.setUnitPpm(unitPpm);
        historyLV.setAdapter(historyAdapter);
        historyLV.setOnItemClickListener((parent, view, position, id) -> {
            historyAdapter.setSelectItem(position);
            historyAdapter.notifyDataSetChanged();
        });
    }

    private void showDatePickDialog() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            detectDate.setText(DateUtil.getDateString(calendar.getTime(), DateUtil.LP_TIME));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void searchHistory(View view) {
        /*
        detectInfoList.clear();
        this.adapter.Clear();
        SetStatisticValue(null, this.unitPpm);
        Object localObject;
        if (this.etSampleName.getText().toString().trim().equals("")) {
            if (this.etSampleTime.getText().toString().trim().equals("")) {
                localObject = DBManager.getInstance().getReadableSession().getDetectInfoDao().queryBuilder().list();
                break label359;
            }
        }
        Property localProperty;
        StringBuilder localStringBuilder;
        if (this.etSampleTime.getText().toString().trim().equals("")) {
            localObject = DBManager.getInstance().getReadableSession().getDetectInfoDao().queryBuilder();
            localProperty = DetectInfoDao.Properties.SampleName;
            localStringBuilder = new StringBuilder();
            localStringBuilder.append("%");
            localStringBuilder.append(this.etSampleName.getText().toString().trim());
            localStringBuilder.append("%");
            localObject = ((QueryBuilder) localObject).where(localProperty.like(localStringBuilder.toString()), new WhereCondition[0]).list();
        } else if (this.etSampleName.getText().toString().trim().equals("")) {
            localObject = DBManager.getInstance().getReadableSession().getDetectInfoDao().queryBuilder().where(DetectInfoDao.Properties.DetectTime.ge(this.etSampleTime.getText().toString().trim()), new WhereCondition[0]).list();
        } else {
            localObject = DBManager.getInstance().getReadableSession().getDetectInfoDao().queryBuilder();
            localProperty = DetectInfoDao.Properties.SampleName;
            localStringBuilder = new StringBuilder();
            localStringBuilder.append("%");
            localStringBuilder.append(this.etSampleName.getText().toString().trim());
            localStringBuilder.append("%");
            localObject = ((QueryBuilder) localObject).where(localProperty.like(localStringBuilder.toString()), new WhereCondition[]{DetectInfoDao.Properties.DetectTime.ge(this.etSampleTime.getText().toString().trim())}).list();
        }
        label359:
        if ((localObject != null) && (((List) localObject).size() > 0)) {
            SetStatisticValue((List) localObject, this.unitPpm);
            this.detectInfos.addAll((Collection) localObject);
            this.adapter.AddData(this.detectInfos);
        }
        this.adapter.notifyDataSetChanged();

         */
    }

    public void exportHistory(View view) {
        /*
        paramString1 = this.adapter;
        if ((paramString1 != null) && (paramString1.mList != null) && (this.adapter.mList.size() >= 1)) {
            this.mLoading.setText(paramString2);
            this.mTask = new MyTask();
            this.mTask.execute(new String[0]);
            return;
        }
        ToastManager.showShortToast(this, "请先搜索记录数据！");

         */
    }
}

