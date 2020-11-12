package com.hb712.gleak_android;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.os.Process;

import com.hb712.gleak_android.base.BaseActivity;
import com.hb712.gleak_android.base.BaseBean;
import com.hb712.gleak_android.interfaceabs.OKHttpListener;
import com.hb712.gleak_android.util.CommonUtils;
import com.hb712.gleak_android.util.HttpUtils;
import com.hb712.gleak_android.util.MapUtils;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/9/22 10:20
 */
public class MainActivity extends BaseActivity {
    public static final String EXTRA_USERNAME = "username";
    public static final String EXTRA_PASSWORD = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //TODO 测试http，可屏蔽掉
        HttpUtils.postDefault(this, "http://114.115.217.241/GeneralMonitorController/getGenaralAreaData",
                MapUtils.getHttpInstance().put("select", "plst"),
                BaseBean.class, new OKHttpListener<BaseBean>() {
                    @Override
                    public void onSuccess(BaseBean bean) {
                        CommonUtils.toast(bean.response);
                    }
                });
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

    public void onBackPressed() {
        new AlertDialog.Builder(this).setTitle("提示").setMessage("确定退出？").setNegativeButton("取消", (paramAnonymousDialogInterface, paramAnonymousInt) -> paramAnonymousDialogInterface.dismiss()).setPositiveButton("确认", (paramAnonymousDialogInterface, paramAnonymousInt) -> {
            paramAnonymousDialogInterface.dismiss();
            Process.killProcess(Process.myPid());
        }).show();
    }
}
