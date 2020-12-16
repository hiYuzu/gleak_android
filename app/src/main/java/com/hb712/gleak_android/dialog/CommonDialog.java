package com.hb712.gleak_android.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;

import com.hb712.gleak_android.MainApplication;

/**
 * 弹出框生成工具
 * @author hiYuzu
 * @version V1.0
 * @date 2020/12/2 15:30
 */
public class CommonDialog {

    public static AlertDialog getDialog(Context context, String title, SuccessCallback successCallback) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setPositiveButton("确认", (dialog, which) -> successCallback.onConfirm())
                .create();
        return alertDialog;
    }

    public static AlertDialog getDialog(Context context, String title, String message, SuccessCallback successCallback) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("确认", (dialog, which) -> successCallback.onConfirm())
                .create();
        return alertDialog;
    }

    public static AlertDialog getDialog(Context context, String title, String message, View view, SuccessCallback successCallback) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setView(view)
                .setPositiveButton("确认", (dialog, which) -> successCallback.onConfirm())
                .create();
        return alertDialog;
    }

    public static AlertDialog getDialog(Context context, String title, String message, View view, String positiveBtn, String negativeBtn, SuccessCallback successCallback) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setView(view)
                .setPositiveButton(positiveBtn, (dialog, which) -> successCallback.onConfirm())
                .setNegativeButton(negativeBtn, (dialog, which) -> dialog.dismiss())
                .create();
        return alertDialog;
    }

    public static void infoDialog(String message) {
        new AlertDialog.Builder(MainApplication.getInstance())
                .setTitle("提示")
                .setMessage(message)
                .setPositiveButton("确定", null)
                .show();
    }

    public static void warnDialog(String message) {
        new AlertDialog.Builder(MainApplication.getInstance())
                .setTitle("警告")
                .setMessage(message)
                .setPositiveButton("确定", null)
                .show();
    }

    public interface SuccessCallback {
        /**
         * 确认回调
         */
        void onConfirm();
    }
}
