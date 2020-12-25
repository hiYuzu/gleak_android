package com.hb712.gleak_android.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.hb712.gleak_android.R;

import java.util.Objects;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/12/23 13:36
 */
public class LoadingDialog {

    private final Activity activity;
    private AlertDialog mLoading = null;

    public LoadingDialog(Activity activity) {
        this.activity = activity;
    }

    public void showLoading() {
        @SuppressLint("InflateParams")
        View loadingView = LayoutInflater.from(activity).inflate(R.layout.dialog_progress_bar, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(loadingView);
        mLoading = builder.create();
        mLoading.setCanceledOnTouchOutside(false);
        Objects.requireNonNull(mLoading.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        mLoading.show();
    }

    public void hideLoading() {
        if (mLoading != null) {
            mLoading.cancel();
        }
        mLoading = null;
    }
}
