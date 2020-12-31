package com.hb712.gleak_android;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.SwitchPreference;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.view.MenuItem;

import com.hb712.gleak_android.dialog.CommonDialog;
import com.hb712.gleak_android.service.UploadPositionService;
import com.hb712.gleak_android.util.LogUtil;
import com.hb712.gleak_android.util.ToastUtil;
import com.hb712.gleak_android.util.GlobalParam;
import com.hb712.gleak_android.util.SPUtil;

import java.util.List;

public class SettingsActivity extends AppCompatPreferenceActivity {
    private static final String TAG = SettingsActivity.class.getSimpleName();

    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * 验证，阻止未知的fragment启动
     */
    @Override
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || AboutPreferenceFragment.class.getName().equals(fragmentName)
                || DevicePreferenceFragment.class.getName().equals(fragmentName);
    }

    @Override
    public void onHeaderClick(Header header, int position) {
        super.onHeaderClick(header, position);
        if (header.id == R.id.checkUpdate) {
            checkUpdate();
        } else if (header.id == R.id.exitApp) {
            backLogin();
        }
    }

    private void checkUpdate() {
        ToastUtil.toastWithoutLog("检查更新中...");
        // TODO: hiYuzu 2020/12/2 检查更新功能
    }

    private void backLogin() {
        CommonDialog.getDialog(this, "登出", "退出并返回登录界面？", null, "确定", null, () -> {
            MainApplication.getInstance().setUserId("");
            MainApplication.getInstance().setToken("");
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            this.finish();
        }).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        backMain();
    }

    private void backMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        this.finish();
    }

    /**
     * 通用设置fragment UI
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        private static final String TAG = GeneralPreferenceFragment.class.getSimpleName();
        private final MainApplication mainApp = MainApplication.getInstance();
        private EditTextPreference serveIp;
        private EditTextPreference servePort;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);
            initRemember();
            initPosition();
            initServe();
        }

        private void initRemember() {
            SwitchPreference generalRemember = (SwitchPreference) findPreference("general_remember");
            generalRemember.setChecked(GlobalParam.rememberPwd);
            generalRemember.setOnPreferenceChangeListener((preference, newValue) -> {
                try {
                    if ((boolean) newValue) {
                        mainApp.saveUserPwd();
                    } else {
                        mainApp.removeUserPwd();
                    }
                    return true;
                } catch (Exception e) {
                    LogUtil.errorOut(TAG, e, null);
                    ToastUtil.toastWithoutLog("设置失败！");
                }
                return false;

            });
        }

        private void initPosition() {
            EditTextPreference uploadPositionPeriod = (EditTextPreference) findPreference("upload_position_period");
            uploadPositionPeriod.setEnabled((boolean) SPUtil.get(mainApp, GlobalParam.UPLOAD_POSITION_KEY, GlobalParam.IS_UPLOAD_POSITION));
            uploadPositionPeriod.setText(String.valueOf(SPUtil.get(mainApp, GlobalParam.UPLOAD_DELAY_KEY, GlobalParam.UPLOAD_DELAY)));
            uploadPositionPeriod.setOnPreferenceChangeListener((preference, newValue) -> {
                try {
                    int period = Integer.parseInt((String) newValue);
                    SPUtil.put(mainApp, GlobalParam.UPLOAD_DELAY_KEY, period);
                    UploadPositionService.getInstance().restartUploadPosition();
                    return true;
                } catch (NumberFormatException nfe) {
                    String msg = "请输入正确合理的数字！";
                    LogUtil.warnOut(TAG, nfe, msg);
                    ToastUtil.toastWithoutLog(msg);
                } catch (Exception e) {
                    LogUtil.errorOut(TAG, e, null);
                    ToastUtil.toastWithoutLog("设置失败！");
                }
                return false;
            });

            SwitchPreference uploadPosition = (SwitchPreference) findPreference("upload_position");
            uploadPosition.setChecked((boolean) SPUtil.get(mainApp, GlobalParam.UPLOAD_POSITION_KEY, GlobalParam.IS_UPLOAD_POSITION));
            uploadPosition.setOnPreferenceChangeListener((preference, newValue) -> {
                try {
                    boolean isUpload = (boolean) newValue;
                    SPUtil.put(mainApp, GlobalParam.UPLOAD_POSITION_KEY, isUpload);
                    uploadPositionPeriod.setEnabled(isUpload);
                    UploadPositionService.getInstance().uploadPosition();
                    return true;
                } catch (Exception e) {
                    LogUtil.errorOut(TAG, e, null);
                    ToastUtil.toastWithoutLog("设置失败！");
                }
                return false;
            });
        }

        private void initServe() {
            serveIp = (EditTextPreference) findPreference(GlobalParam.SERVE_IP);
            servePort = (EditTextPreference) findPreference(GlobalParam.SERVE_PORT);
            serveIp.setText(SPUtil.get(mainApp, GlobalParam.SERVE_IP, GlobalParam.DEFAULT_IP).toString());
            servePort.setText(SPUtil.get(mainApp, GlobalParam.SERVE_PORT, GlobalParam.DEFAULT_PORT).toString());
            serveIp.setOnPreferenceChangeListener((preference, newValue) -> {
                try {
                    String ip = (String) newValue;
                    if (isCorrectIp(ip)) {
                        serveIp.setSummary(ip);
                        SPUtil.put(mainApp, GlobalParam.SERVE_IP, ip);
                        CommonDialog.getDialog(getContext(), "重启应用", "服务器地址已更改，重启应用生效", null, "立即重启", "稍后重启", () -> {
                            Intent intent = new Intent(mainApp, LaunchActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mainApp.startActivity(intent);
                            android.os.Process.killProcess(android.os.Process.myPid());
                        }).show();
                    } else {
                        ToastUtil.toastWithoutLog("请输入正确的IP格式");
                    }
                } catch (Exception e) {
                    LogUtil.errorOut(TAG, e, null);
                    ToastUtil.toastWithoutLog("设置失败！");
                }
                return false;
            });
            servePort.setOnPreferenceChangeListener((preference, newValue) -> {
                try {
                    String port = (String) newValue;
                    if (isCorrectPort(port)) {
                        servePort.setSummary(port);
                        SPUtil.put(mainApp, GlobalParam.SERVE_PORT, port);
                        CommonDialog.getDialog(getContext(), "重启应用", "服务器地址已更改，重启应用生效",null, "立即重启", "稍后重启", () -> {
                            Intent intent = new Intent(mainApp, LaunchActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mainApp.startActivity(intent);
                            android.os.Process.killProcess(android.os.Process.myPid());
                        }).show();
                    } else {
                        ToastUtil.toastWithoutLog("请输入正确的Port格式");
                    }
                } catch (Exception e) {
                    LogUtil.errorOut(TAG, e, null);
                    ToastUtil.toastWithoutLog("设置失败！");
                }
                return false;
            });
        }

        private boolean isCorrectIp(String ip) {
            //0.0.0.0 -> 255.255.255.255
            if (ip.length() < 7 || ip.length() > 15) {
                return false;
            }
            String[] ipArray = ip.split("\\.");
            if (ipArray.length != 4) {
                return false;
            }
            for (String temp : ipArray) {
                try {
                    int num = Integer.parseInt(temp);
                    if (num < 0 || num > 255) {
                        return false;
                    }
                } catch (Exception e) {
                    return false;
                }
            }
            return true;
        }

        private boolean isCorrectPort(String port) {
            if (port.isEmpty()) {
                return true;
            }
            try {
                int portInt = Integer.parseInt(port);
                if (portInt < 1 || portInt > 65535) {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
            return true;
        }
    }

    /**
     * 设备相关设置 fragment UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DevicePreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            startActivity(new Intent(getContext(), DeviceSettingActivity.class));
        }
    }

    /**
     * 软件相关信息 fragment UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class AboutPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            startActivity(new Intent(getContext(), AboutSettingActivity.class));
        }
    }
}
