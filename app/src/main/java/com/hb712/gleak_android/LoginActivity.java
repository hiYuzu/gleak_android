package com.hb712.gleak_android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.hb712.gleak_android.base.BaseActivity;
import com.hb712.gleak_android.interfaceabs.HttpInterface;
import com.hb712.gleak_android.interfaceabs.OKHttpListener;
import com.hb712.gleak_android.util.HttpUtils;
import com.hb712.gleak_android.util.LogUtil;
import com.hb712.gleak_android.util.PermissionsUtil;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/9/22 10:20
 */
public class LoginActivity extends BaseActivity implements HttpInterface {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private Button mLoginButton;
    private TextView mMessageText;
    private TextView mResultText;
    private EditText mUsernameEdit;
    private EditText mPasswordEdit;
    private CheckBox mSaveUsernameCheckBox;
    private ImageView mLoadingImage;

    @SuppressLint("BatteryLife")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        try {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (pm == null) {
                    LogUtil.warnOut(TAG, null, "PowerManager为NULL");
                } else if (!pm.isIgnoringBatteryOptimizations(getPackageName())) {
                    Intent intent = new Intent();
                    String packageName = getPackageName();
                    intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:" + packageName));
                    startActivity(intent);
                }
            }
        } catch (Exception e) {
            LogUtil.errorOut(TAG, e, e.getMessage());
        } finally {
            mSaveUsernameCheckBox = findViewById(R.id.save_username_pwd);
            mUsernameEdit = findViewById(R.id.username);
            mPasswordEdit = findViewById(R.id.password);
            mMessageText = findViewById(R.id.message);
            mResultText = findViewById(R.id.result_message);
            mLoginButton = findViewById(R.id.login_button);
            mLoadingImage = findViewById(R.id.loading_image);

            mMessageText.setVisibility(View.GONE);
            mResultText.setVisibility(View.GONE);
            mLoadingImage.setVisibility(View.GONE);
            PermissionsUtil.requestWriteExternalStorage(this, this);
            MainApplication.getInstance().removeUserPwd();

            mLoginButton.setOnClickListener(v -> {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromInputMethod(v.getWindowToken(), 0);
                }

                String username = mUsernameEdit.getText().toString();
                String password = mPasswordEdit.getText().toString();

                mLoginButton.setEnabled(false);
                mUsernameEdit.setEnabled(false);
                mPasswordEdit.setEnabled(false);
                mSaveUsernameCheckBox.setEnabled(false);

                if (username.isEmpty() || password.isEmpty()) {
                    onLoginFailed("请输入用户名密码");
                    return;
                }

                startLoginAction(username, password);
            });
        }
    }

    private void onLoginFailed(String message) {
        mLoginButton.setEnabled(true);
        mUsernameEdit.setEnabled(true);
        mPasswordEdit.setEnabled(true);
        mSaveUsernameCheckBox.setEnabled(true);

        mMessageText.setVisibility(View.GONE);
        mResultText.setVisibility(View.VISIBLE);
        mResultText.setText(message);

        mLoadingImage.clearAnimation();
        mLoadingImage.setVisibility(View.GONE);
    }

    private void startLoginAction(String username, String password) {
        HttpUtils.get(this, MainApplication.getInstance().baseUrl + "/api/login?name=" + username + "&password=" + password, new OKHttpListener() {
            @Override
            public void onStart() {
                mResultText.setVisibility(View.GONE);
                mMessageText.setVisibility(View.VISIBLE);
                mMessageText.setText("正在登录...");
                Animation loadingAnim = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.loading);
                loadingAnim.setRepeatCount(Animation.INFINITE);
                mLoadingImage.startAnimation(loadingAnim);
                mLoadingImage.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(Bundle bundle) {
                String result = bundle.getString(HttpUtils.MESSAGE);
                JSONObject json = JSONObject.parseObject(result);
                if (json.getBoolean("status")) {
                    LogUtil.debugOut(TAG, "用户" + json.getJSONObject("data").getString("userId") + "登录成功!");

                    MainApplication.getInstance().saveUserPwd(json.getJSONObject("data").getString("userId"), json.getJSONObject("data").getString("token"), username, password, mSaveUsernameCheckBox.isChecked());

                    gotoMainActivity(username, password);
                } else {
                    onLoginFailed(json.getString("msg"));
                }
            }

            @Override
            public void onServiceError(Bundle bundle) {
                onLoginFailed(bundle.getString(HttpUtils.MESSAGE));
            }

            @Override
            public void onNetworkError(Bundle bundle) {
                onLoginFailed(bundle.getString(HttpUtils.MESSAGE));
            }
        });
    }

    private void gotoMainActivity(String username, String password) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_USERNAME, username);
        intent.putExtra(MainActivity.EXTRA_PASSWORD, password);
        startActivity(intent);
        finish();
    }

    public void loginWithoutUser(View view) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_USERNAME, "");
        intent.putExtra(MainActivity.EXTRA_PASSWORD, "");
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public boolean isDiscardHttp() {
        return isFinishing();
    }
}

