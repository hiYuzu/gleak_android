package com.hb712.gleak_android.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
    public static BaseActivity baseActivity;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        baseActivity = this;
    }
}
