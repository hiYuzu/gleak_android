package com.hb712.gleak_android;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.hb712.gleak_android.adapter.FragmentPageAdapter;
import com.hb712.gleak_android.base.BaseActivity;
import com.hb712.gleak_android.fragment.LimitSettingFragment;
import com.hb712.gleak_android.fragment.SeriesSettingFragment;

import java.util.ArrayList;
import java.util.Objects;

public class DeviceSettingActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_setting);
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

    private void initView() {
        TabLayout tabLayout = findViewById(R.id.tabs);
        ViewPager viewPager = findViewById(R.id.series_setting_view);
        ArrayList<Fragment> fragments = new ArrayList<>();
        tabLayout.addTab(tabLayout.newTab());
        fragments.add(new SeriesSettingFragment());
        tabLayout.addTab(tabLayout.newTab());
        fragments.add(new LimitSettingFragment());
        tabLayout.setupWithViewPager(viewPager, false);
        FragmentPageAdapter pageAdapter = new FragmentPageAdapter(fragments, getSupportFragmentManager());
        viewPager.setAdapter(pageAdapter);
        Objects.requireNonNull(tabLayout.getTabAt(0)).setText("曲线设置");
        Objects.requireNonNull(tabLayout.getTabAt(1)).setText("限值设置");
    }
}