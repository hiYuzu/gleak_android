package com.hb712.gleak_android;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.view.MenuItem;

import com.hb712.gleak_android.base.BaseActivity;
import com.hb712.gleak_android.dialog.CommonDialog;
import com.hb712.gleak_android.service.UploadPositionService;
import com.hb712.gleak_android.util.ToastUtil;
import com.hb712.gleak_android.util.GlobalParam;
import com.hb712.gleak_android.util.SPUtil;

import java.util.List;

public class SettingsActivity extends AppCompatPreferenceActivity {

    /**
     * 监听Preference
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = (preference, value) -> {
        preference.setSummary(value.toString());
        return true;
    };

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
     * 验证，组织未知的fragment启动
     */
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
        ToastUtil.longInstanceToast("检查更新中...");
        // TODO: hiYuzu 2020/12/2 检查更新功能
    }

    private void backLogin() {
        CommonDialog.getDialog(this, "提示", "确定登出？", () -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            this.finish();
        }).show();
    }

    /**
     * 通用设置fragment UI
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {

        private EditTextPreference serveIp;
        private EditTextPreference servePort;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            SwitchPreference general_remember = (SwitchPreference) findPreference("general_remember");
            general_remember.setChecked(GlobalParam.rememberPwd);
            general_remember.setOnPreferenceChangeListener((preference, newValue) -> {
                MainApplication context = MainApplication.getInstance();
                if ((boolean) newValue) {
                    context.saveUserPwd();
                } else {
                    context.removeUserPwd();
                }
                return true;
            });

            SwitchPreference uploadPosition = (SwitchPreference) findPreference("upload_position");
            uploadPosition.setChecked(GlobalParam.isUploadPosition);
            uploadPosition.setOnPreferenceChangeListener((preference, newValue) -> {
                GlobalParam.isUploadPosition = (boolean) newValue;
                UploadPositionService.getInstance().uploadPosition();
                return true;
            });


            serveIp = (EditTextPreference) findPreference(GlobalParam.SERVE_IP);
            servePort = (EditTextPreference) findPreference(GlobalParam.SERVE_PORT);
            serveIp.setText(SPUtil.get(MainApplication.getInstance(), GlobalParam.SERVE_IP, GlobalParam.DEFAULT_IP).toString());
            servePort.setText(SPUtil.get(MainApplication.getInstance(), GlobalParam.SERVE_PORT, GlobalParam.DEFAULT_PORT).toString());
            serveIp.setOnPreferenceChangeListener((preference, newValue) -> {
                String ip = (String) newValue;
                if (isCorrectIp(ip)) {
                    serveIp.setSummary(ip);
                    serveIp.setText(ip);
                    SPUtil.put(MainApplication.getInstance(), GlobalParam.SERVE_IP, ip);
                    ToastUtil.shortInstanceToast("服务器地址已更改，重启应用生效");
                    return true;
                } else {
                    ToastUtil.shortInstanceToast("请输入正确的IP格式");
                    return false;
                }
            });
            servePort.setOnPreferenceChangeListener((preference, newValue) -> {
                String port = (String) newValue;
                if (isCorrectPort(port)) {
                    servePort.setSummary(port);
                    serveIp.setText(port);
                    SPUtil.put(MainApplication.getInstance(), GlobalParam.SERVE_PORT, port);
                    ToastUtil.shortInstanceToast("服务器地址已更改，重启应用生效");
                    return true;
                } else {
                    ToastUtil.shortInstanceToast("请输入正确的Port格式");
                    return false;
                }
            });
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
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
            addPreferencesFromResource(R.xml.pref_device);
            setHasOptionsMenu(true);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
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
            addPreferencesFromResource(R.xml.pref_about);
            setHasOptionsMenu(true);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
