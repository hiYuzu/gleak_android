package com.hb712.gleak_android;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.hb712.gleak_android.adapter.HistoryAdapter;
import com.hb712.gleak_android.dao.DBManager;
import com.hb712.gleak_android.dao.DaoSession;
import com.hb712.gleak_android.dao.DetectInfoDao;
import com.hb712.gleak_android.dialog.CommonDialog;
import com.hb712.gleak_android.entity.DetectInfo;
import com.hb712.gleak_android.util.DateUtil;
import com.hb712.gleak_android.util.LogUtil;
import com.hb712.gleak_android.util.SPUtil;
import com.hb712.gleak_android.util.ToastUtil;
import com.hb712.gleak_android.util.UnitUtil;


import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private static final String TAG = HistoryActivity.class.getSimpleName();

    private EditText sampleName;
    private EditText detectDate;
    private TextView maxValue;
    private TextView minValue;
    private TextView avgValue;

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
        List<DetectInfo> detectInfoList = null;
        try {
            detectInfoList = DBManager.getInstance().getReadableSession().getDetectInfoDao().queryBuilder().limit(100).list();
            Collections.reverse(detectInfoList);
        } catch (Exception e) {
            ToastUtil.toastWithoutLog("本地数据库发生错误！");
            LogUtil.assertOut(TAG, e, "DetectInfoDao");
        }
        if (detectInfoList != null && detectInfoList.size() > 0) {
            setStatisticValue(detectInfoList, unitPpm);
            this.detectInfoList.addAll(detectInfoList);
            historyAdapter.addData(this.detectInfoList);
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

        ListView historyLv = findViewById(R.id.historyList);
        historyAdapter = new HistoryAdapter(this, detectInfoList);
        unitPpm = SPUtil.get(this, "unit", getResources().getString(R.string.detect_value_unit_ppm)).toString().equals(getResources().getString(R.string.detect_value_unit_ppm));
        historyAdapter.setUnitPpm(unitPpm);
        historyLv.setAdapter(historyAdapter);
        historyLv.setOnItemClickListener((parent, view, position, id) -> {
            historyAdapter.setSelectItem(position);
            historyAdapter.notifyDataSetChanged();
        });
        historyLv.setOnItemLongClickListener((parent, view, position, id) -> {
            CommonDialog.getDialog(this, "删除此条记录？", () -> {
                DetectInfo detectInfo = detectInfoList.get(position);
                detectInfoList.remove(position);
                try {
                    DBManager.getInstance().getWritableSession().getDetectInfoDao().delete(detectInfo);
                    File video = new File(detectInfo.getVideoPath());
                    if (video.exists() && !video.isDirectory()) {
                        if (video.delete()) {
                            LogUtil.infoOut(TAG, "文件已删除");
                        } else {
                            LogUtil.warnOut(TAG, null,"文件删除失败：" + video.getPath());
                        }
                    }
                } catch (Exception e) {
                    ToastUtil.toastWithoutLog("本地数据库发生错误！");
                    LogUtil.assertOut(TAG, e, "DetectInfoDao");
                }
                historyAdapter.setSelectItem(position);
                historyAdapter.notifyDataSetChanged();
            }).show();
            return true;
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
        detectInfoList.clear();
        historyAdapter.clear();
        setStatisticValue(null, unitPpm);
        List<DetectInfo> detectInfoList = null;
        DaoSession daoSession;
        try {
            daoSession = DBManager.getInstance().getReadableSession();
        } catch (Exception e) {
            ToastUtil.toastWithoutLog("本地数据库发生错误！");
            LogUtil.assertOut(TAG, e, "DetectInfoDao");
            return;
        }
        String name = sampleName.getText().toString().trim();
        String date = detectDate.getText().toString().trim();

        if (name.isEmpty() && date.isEmpty()) {
            detectInfoList = daoSession.getDetectInfoDao().queryBuilder().list();
        } else {
            QueryBuilder<DetectInfo> queryBuilder = daoSession.getDetectInfoDao().queryBuilder();
            if (!name.isEmpty() && date.isEmpty()) {
                Property property = DetectInfoDao.Properties.LEAK_NAME;
                String str = "%" + name + "%";
                detectInfoList = queryBuilder.where(property.like(str), new WhereCondition[0]).list();
            } else if (name.isEmpty() && !date.isEmpty()) {
                detectInfoList = queryBuilder.where(DetectInfoDao.Properties.MONITOR_TIME.ge(date), new WhereCondition[0]).list();
            } else {
                Property nameProperty = DetectInfoDao.Properties.LEAK_NAME;
                Property dateProperty = DetectInfoDao.Properties.MONITOR_TIME;
                String str = "%" + name + "%";
                detectInfoList = queryBuilder.where(nameProperty.like(str), new WhereCondition[]{dateProperty.ge(date)}).list();
            }
        }
        if (detectInfoList != null && detectInfoList.size() > 0) {
            setStatisticValue(detectInfoList, unitPpm);
            this.detectInfoList.addAll(detectInfoList);
            historyAdapter.addData(this.detectInfoList);
        }
        historyAdapter.notifyDataSetChanged();
    }

    public void exportHistory(View view) {
        if (historyAdapter.detectInfoViewList.size() > 0) {
            // TODO: hiYuzu 2020/12/18 导出功能
//            mLoading.setText(paramString2);
//            mTask = new MyTask();
//            mTask.execute(new String[0]);
            return;
        }
        ToastUtil.toastWithoutLog("请先搜索记录数据！");
    }
}

