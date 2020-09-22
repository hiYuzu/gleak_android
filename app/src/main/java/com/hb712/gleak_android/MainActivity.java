package com.hb712.gleak_android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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
}
