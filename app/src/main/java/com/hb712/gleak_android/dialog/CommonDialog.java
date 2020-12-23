package com.hb712.gleak_android.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.hb712.gleak_android.MainApplication;

/**
 * 弹出框生成工具
 * @author hiYuzu
 * @version V1.0
 * @date 2020/12/2 15:30
 */
public class CommonDialog {

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

    public static AlertDialog getDialog(Context context, String title, String message, View view, String positiveBtn, String negativeBtn, @NonNull SuccessCallback successCallback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (title != null) {
            builder.setTitle(title);
        }
        if (message != null) {
            builder.setMessage(message);
        }
        if (view != null) {
            builder.setView(view);
        }
        if (positiveBtn == null) {
            positiveBtn = "确定";
        }
        builder.setPositiveButton(positiveBtn, (dialog, which) -> successCallback.onConfirm());
        if (negativeBtn != null) {
            builder.setNegativeButton(negativeBtn, (dialog, which) -> dialog.dismiss());
        }
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    public static void infoDialog(Context context, String message) {
        new AlertDialog.Builder(context)
                .setTitle("提示")
                .setMessage(message)
                .setPositiveButton("确定", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
        getDialog(context, "提示", null, null, null, ).show();
    }

    public static void warnDialog(Context context, String message) {
        new AlertDialog.Builder(context)
                .setTitle("警告")
                .setMessage(message)
                .setPositiveButton("确定", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    public interface SuccessCallback {
        /**
         * 确认回调
         */
        void onConfirm();
    }
}
