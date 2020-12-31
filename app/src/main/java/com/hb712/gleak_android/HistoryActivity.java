package com.hb712.gleak_android;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
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
import com.hb712.gleak_android.dialog.LoadingDialog;
import com.hb712.gleak_android.dialog.VideoDialog;
import com.hb712.gleak_android.entity.DetectInfo;
import com.hb712.gleak_android.interfaceabs.ITaskHandler;
import com.hb712.gleak_android.util.DateUtil;
import com.hb712.gleak_android.util.Exporter;
import com.hb712.gleak_android.util.GlobalParam;
import com.hb712.gleak_android.util.LogUtil;
import com.hb712.gleak_android.util.PermissionsUtil;
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
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private static final String TAG = HistoryActivity.class.getSimpleName();

    private EditText sampleName;
    private EditText detectDate;
    private TextView maxValue;
    private TextView minValue;
    private TextView avgValue;

    private boolean isExporting = false;
    private List<DetectInfo> detectInfoList;
    private HistoryAdapter historyAdapter;
    private boolean unitPpm = true;
    private LoadingDialog mLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        detectInfoList = new ArrayList<>();
        setupActionBar();
        initView();
        mLoading = new LoadingDialog(this);
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
    public void onBackPressed() {
        if (isExporting) {
            ToastUtil.toastWithoutLog("请等待导出结束...");
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        showLastData();
        super.onResume();
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
            PermissionsUtil.requestRWPermission(this);
            new VideoDialog(this).showVideo(detectInfoList.get(position).getVideoPath());
        });
        historyLv.setOnItemLongClickListener((parent, view, position, id) -> {
            CommonDialog.getDialog(this, "删除此条记录？", null, null, null, null, () -> {
                DetectInfo detectInfo = detectInfoList.get(position);
                detectInfoList.remove(position);
                try {
                    DBManager.getInstance().getWritableSession().getDetectInfoDao().delete(detectInfo);
                    File video = new File(detectInfo.getVideoPath());
                    if (video.exists() && !video.isDirectory()) {
                        if (video.delete()) {
                            LogUtil.infoOut(TAG, "文件已删除");
                            searchHistory(null);
                        } else {
                            LogUtil.warnOut(TAG, null, "文件删除失败：" + video.getPath());
                        }
                    }
                } catch (Exception e) {
                    ToastUtil.toastWithoutLog("本地数据库发生错误！");
                    LogUtil.assertOut(TAG, e, "DetectInfoDao");
                }
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

    private void showLastData() {
        detectInfoList.clear();
        historyAdapter.clear();
        setStatisticValue(null, unitPpm);
        List<DetectInfo> detectInfoList = null;
        try {
            detectInfoList = DBManager.getInstance().getReadableSession().getDetectInfoDao().queryBuilder().limit(100).list();
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
            DecimalFormat df = new DecimalFormat("0.00");
            maxValue = df.format(max);
            minValue = df.format(min);
            avgValue = df.format(avg);
        }
        this.maxValue.setText(maxValue);
        this.minValue.setText(minValue);
        this.avgValue.setText(avgValue);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResult) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResult);
        if (requestCode == GlobalParam.REQUEST_RW_PERMISSION) {
            for (int grant : grantResult) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    CommonDialog.getDialog(this, "权限不足", "应用需要开启读写功能，请到 “应用信息 -> 权限” 中授予！", null, null, "拒绝", () -> {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        startActivity(intent);
                    }).show();
                }
            }
        }
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
            } else if (name.isEmpty()) {
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
        if (historyAdapter.detectInfoViewList.size() < 1) {
            ToastUtil.toastWithoutLog("请先搜索记录数据！");
            return;
        }
        new ExportTask(new ITaskHandler() {
            @Override
            public void onStart() {
                isExporting = true;
                mLoading.showLoading();
            }

            @Override
            public void onSuccess(Bundle bundle) {
                isExporting = false;
                mLoading.hideLoading();
                Looper.prepare();
                ToastUtil.toastWithoutLog("导出成功：" + bundle.getString("result"));
                Looper.loop();
            }

            @Override
            public void onFailed(String failMsg) {
                isExporting = false;
                mLoading.hideLoading();
                Looper.prepare();
                ToastUtil.toastWithoutLog(failMsg);
                Looper.loop();
            }
        }).execute();
    }

    private String exportExcel() throws Exception {
        Exporter exporter = new Exporter();
        String fileName = DateUtil.getCurrentTime(DateUtil.TIME_SERIES) + ".xls";
        exporter.exportToExcel(fileName, historyAdapter.detectInfoViewList);
        return GlobalParam.EXCEL_PATH + File.separator + fileName;
    }

    @SuppressLint("StaticFieldLeak")
    private class ExportTask extends AsyncTask<String, Integer, Bundle> {
        private final ITaskHandler mHandler;

        ExportTask(ITaskHandler taskHandler) {
            super();
            mHandler = taskHandler;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mHandler.onStart();
        }

        @Override
        protected Bundle doInBackground(String... strings) {
            try {
                Bundle bundle = new Bundle();
                bundle.putString("result", exportExcel());
                mHandler.onSuccess(bundle);
            } catch (Exception e) {
                LogUtil.errorOut(TAG, e, null);
                mHandler.onFailed("失败：" + e.getMessage());
            }
            return null;
        }
    }
}

