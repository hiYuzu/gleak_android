package com.hb712.gleak_android;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.hb712.gleak_android.dialog.CommonDialog;
import com.hb712.gleak_android.util.DateUtil;
import com.hb712.gleak_android.util.GlobalParam;

public class AboutSettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_setting);
        setupActionBar();
        init();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    private void init() {
        TextView versionName = findViewById(R.id.versionName);
        versionName.setText(GlobalParam.versionName);

        TextView updateDate = findViewById(R.id.updateDate);
        updateDate.setText(GlobalParam.updateTime);
    }
}