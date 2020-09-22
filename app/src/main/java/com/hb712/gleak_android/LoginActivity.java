package com.hb712.gleak_android;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hb712.gleak_android.service.WebServiceClient;
import com.hb712.gleak_android.util.WebViewCookiesUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/9/22 10:20
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private Button mLoginButton;
    private TextView mMessageText;
    private TextView mResultText;
    private EditText mUsernameEdit;
    private EditText mPasswordEdit;
    private CheckBox mSaveUsernameCheckBox;
    private ImageView mLoadingImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!pm.isIgnoringBatteryOptimizations(getPackageName())) {
                Intent intent = new Intent();
                String packageName = getPackageName();
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                LoginActivity.this.startActivity(intent);
            }
        }

        mSaveUsernameCheckBox = findViewById(R.id.save_username_passwd);
        mUsernameEdit = findViewById(R.id.username);
        mPasswordEdit = findViewById(R.id.password);
        mMessageText = findViewById(R.id.message);
        mResultText = findViewById(R.id.result_message);
        mLoginButton = findViewById(R.id.login_button);
        mLoadingImage = findViewById(R.id.loading_image);

        mMessageText.setVisibility(View.GONE);
        mResultText.setVisibility(View.GONE);
        mLoadingImage.setVisibility(View.GONE);


        MainApplication application = MainApplication.getInstance();
        application.removePassword();

        String username = application.getUsername();

        mUsernameEdit.setText(username);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
            }
        });

    }

    private void onLoginFailed(String message){
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

    private void startLoginAction(final String username, final String password) {
        MainApplication.getInstance().getWebServiceClient().runLoginTask(new WebServiceClient.TaskHandler() {
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
            public void onSuccess(JSONObject obj) {
                Log.d(TAG, "Login onSuccess: " + obj.toString());
                try {
                    String displayUser = obj.getString("DisplayUsername");
                    WebViewCookiesUtils.saveCookie(MainApplication.baseUrl, "displayUser", displayUser);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Boolean saveUsername = mSaveUsernameCheckBox.isChecked();
                if(saveUsername) {
                    MainApplication.getInstance().saveUsername(username);
                    MainApplication.getInstance().savePassword(password);
                }

                gotoMainActivity(username, password);
            }

            @Override
            public void onFailed(String errMsg) {
                Log.d(TAG, "onLoginResult: Failed :" + errMsg);
                onLoginFailed(errMsg);
            }
        }, username, password);

    }

    private void gotoMainActivity(String username, String password) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_USERNAME, username);
        intent.putExtra(MainActivity.EXTRA_PASSWORD, password);
        startActivity(intent);
        finish();
    }
}

