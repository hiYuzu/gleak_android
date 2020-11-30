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
        //TODO..
    }

    private void backLogin() {
        new AlertDialog.Builder(this).setTitle("提示").setMessage("确定登出？").setNegativeButton("取消", (paramAnonymousDialogInterface, paramAnonymousInt) -> paramAnonymousDialogInterface.dismiss()).setPositiveButton("确认", (paramAnonymousDialogInterface, paramAnonymousInt) -> {
            paramAnonymousDialogInterface.dismiss();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }).show();
    }

    /**
     * 通用设置fragment UI
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {

        private final String SERVE_IP = "serve_ip";
        private final String SERVE_PORT = "serve_port";

        private SwitchPreference general_remember;
        private EditTextPreference serveIp;
        private EditTextPreference servePort;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            general_remember = (SwitchPreference) findPreference("general_remember");

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

            serveIp = (EditTextPreference) findPreference(SERVE_IP);
            servePort = (EditTextPreference) findPreference(SERVE_PORT);
            serveIp.setText(SPUtil.get(MainApplication.getInstance(), SERVE_IP, "127.0.0.1").toString());
            servePort.setText(SPUtil.get(MainApplication.getInstance(), SERVE_PORT, "").toString());
            serveIp.setOnPreferenceChangeListener((preference, newValue) -> {
                String ip = (String) newValue;
                if (isCorrectIp(ip)) {
                    serveIp.setSummary(ip);
                    serveIp.setText(ip);
                    SPUtil.put(MainApplication.getInstance(), SERVE_IP, ip);
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
                    SPUtil.put(MainApplication.getInstance(), SERVE_PORT, port);
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
