package com.hb712.gleak_android.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.hb712.gleak_android.R;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/12/23 13:36
 */
public class LoadingDialog {
    private final Context context;
    public LoadingDialog(Context context) {
        this.context = context;
    }

    public void showLoading() {
        @SuppressLint("InflateParams")
        View loadingView = LayoutInflater.from(context).inflate(R.layout.dialog_progress_bar, null);
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setView(loadingView)
                .create();
        alertDialog.show();
    }
}
