package com.hb712.gleak_android.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;

/**
 * 弹出框生成工具
 * @author hiYuzu
 * @version V1.0
 * @date 2020/12/2 15:30
 */
public class CommonDialog {

    private final Context context;
    private final String title;
    private String message;
    private View view;
    private String positiveBtn = "确认";
    private String negativeBtn = "取消";
    private final SuccessCallback successCallback;

    public CommonDialog(Context context, String title, SuccessCallback successCallback) {
        this.context = context;
        this.title = title;
        this.successCallback = successCallback;
    }

    public CommonDialog(Context context, String title, String message, SuccessCallback successCallback) {
        this.context = context;
        this.title = title;
        this.message = message;
        this.successCallback = successCallback;
    }

    public CommonDialog(Context context, String title, String message, View view, SuccessCallback successCallback) {
        this.context = context;
        this.title = title;
        this.message = message;
        this.view = view;
        this.successCallback = successCallback;
    }

    public CommonDialog(Context context, String title, String message, View view, String positiveBtn, String negativeBtn, SuccessCallback successCallback) {
        this.context = context;
        this.title = title;
        this.message = message;
        this.view = view;
        this.positiveBtn = positiveBtn;
        this.negativeBtn = negativeBtn;
        this.successCallback = successCallback;
    }

    public void show() {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setView(view)
                .setPositiveButton(positiveBtn, (dialog, which) -> successCallback.onConfirm())
                .setNegativeButton(negativeBtn, (dialog, which) -> dialog.dismiss())
                .show();
    }

    public interface SuccessCallback {
        void onConfirm();
    }
}
