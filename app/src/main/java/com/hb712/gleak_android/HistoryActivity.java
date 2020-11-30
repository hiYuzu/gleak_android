package com.hb712.gleak_android;

import android.annotation.SuppressLint;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.hb712.gleak_android.adapter.HistoryAdapter;
import com.hb712.gleak_android.pojo.DetectInfo;
import com.hb712.gleak_android.util.SPUtil;

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
    private boolean unitPPM = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        setupActionBar();
        initView();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /// 返回destroy activity
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            onBackPressed();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        sampleName = findViewById(R.id.historyName);
        detectDate = findViewById(R.id.historyDate);
        maxValue = findViewById(R.id.historyMaxValue);
        minValue = findViewById(R.id.historyMinValue);
        avgValue = findViewById(R.id.historyAvgValue);
        historyLV = findViewById(R.id.historyList);

        detectDate.setOnTouchListener((v, event) -> {
            if (event.getAction() == 0) {
                showDatePickDialog();
                return true;
            }
            return false;
        });

        historyAdapter = new HistoryAdapter(this, detectInfoList);
        unitPPM = SPUtil.get(this, "unit", getResources().getString(R.string.detect_value_unit_ppm)).toString().equals(getResources().getString(R.string.detect_value_unit_ppm));
        historyAdapter.setUnitPPM(unitPPM);
        historyLV.setAdapter(historyAdapter);
        historyLV.setOnItemClickListener((parent, view, position, id) -> {
            historyAdapter.setSelectItem(position);
            historyAdapter.notifyDataSetChanged();
        });
    }

    private void showDatePickDialog() {

    }

    public void searchHistory(View view) {

    }

    public void exportHistory(View view) {

    }
}

