package com.hb712.gleak_android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/9/22 10:20
 */
public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_USERNAME = "staff_id";
    public static final String EXTRA_PASSWORD = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void detectClick(View view) {
        Intent intent = new Intent(MainActivity.this, DetectActivity.class);
        startActivity(intent);
    }

    public void calibrateClick(View view) {
        Intent intent = new Intent(MainActivity.this, CalibrateActivity.class);
        startActivity(intent);
    }

    public void historyClick(View view) {
        Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
        startActivity(intent);
    }

    public void settingsClick(View view) {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }
}
