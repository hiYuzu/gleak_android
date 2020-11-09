package com.hb712.gleak_android.base;

import android.support.v7.app.AppCompatActivity;

import com.hb712.gleak_android.interfaceabs.HttpInterface;

public class BaseActivity extends AppCompatActivity implements HttpInterface {
    @Override
    public BaseActivity getActivity() {
        return this;
    }

    @Override
    public boolean isDiscardHttp() {
        return isFinishing();
    }
}
