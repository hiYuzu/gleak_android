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
    private static final String DEFAULT_POSITIVE_BUTTON = "确定";
    private static final String DEFAULT_NEGATIVE_BUTTON = "取消";
    /**
     * 通用 dialog 窗口
     * @param context 调用者
     * @param title 标题 {@link android.app.AlertDialog.Builder#setTitle(CharSequence)}
     * @param message 内容 {@link android.app.AlertDialog.Builder#setMessage(CharSequence)}
     * @param view 视图 {@link android.app.AlertDialog.Builder#setView(View)}
     * @param positiveBtn 确认按钮，可为 {@code null}，当参数为 {@code null} 时，值为 {@link CommonDialog#DEFAULT_POSITIVE_BUTTON}
     * @param negativeBtn 取消按钮，可为 {@code null}，当参数为 {@code null} 时，值为 {@link CommonDialog#DEFAULT_NEGATIVE_BUTTON}
     * @param successCallback 确认回调接口，不可为 {@code null}
     * @see AlertDialog
     * @see CommonDialog.SuccessCallback
     * @return {@link AlertDialog} 对象
     */
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
            positiveBtn = DEFAULT_POSITIVE_BUTTON;
        }
        builder.setPositiveButton(positiveBtn, (dialog, which) -> successCallback.onConfirm());
        if (negativeBtn == null) {
            negativeBtn = DEFAULT_NEGATIVE_BUTTON;
        }
        builder.setNegativeButton(negativeBtn, (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    public static void infoDialog(Context context, String message) {
        new AlertDialog.Builder(context)
                .setTitle("提示")
                .setMessage(message)
                .setPositiveButton(DEFAULT_POSITIVE_BUTTON, (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    public static void warnDialog(Context context, String message) {
        new AlertDialog.Builder(context)
                .setTitle("警告")
                .setMessage(message)
                .setPositiveButton(DEFAULT_POSITIVE_BUTTON, (dialog, which) -> dialog.dismiss())
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
