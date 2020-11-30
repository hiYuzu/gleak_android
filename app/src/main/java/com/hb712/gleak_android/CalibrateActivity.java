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
import com.hb712.gleak_android.pojo.CalibrationInfo;
import com.hb712.gleak_android.pojo.SeriesInfo;

import java.util.List;

public class CalibrateActivity extends AppCompatActivity {

    private static final String TAG = CalibrateActivity.class.getSimpleName();
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
    private Button caliToDetect;

    private Button[] caliBtns;
    private List<SeriesInfo> seriesInfoList;
    private String[] seriesNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibrate);
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

    /// 返回destroy activity
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            onBackPressed();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

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
        caliToDetect = findViewById(R.id.caliToDetect);

        caliBtns = new Button[]{caliBtn1, caliBtn2, caliBtn3, caliBtn4, caliBtn5, caliBtn6};
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
}
