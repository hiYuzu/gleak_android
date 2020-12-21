package com.hb712.gleak_android.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.hb712.gleak_android.R;
import com.hb712.gleak_android.controller.SeriesInfoController;
import com.hb712.gleak_android.entity.SeriesInfo;
import com.hb712.gleak_android.util.LogUtil;
import com.hb712.gleak_android.util.ToastUtil;

import java.util.List;

/**
 * 工作曲线dialog
 *
 * @author hiYuzu
 * @version V1.0
 * @date 2020/10/21 10:41
 */
public class SeriesDialog {
    private static final String TAG = SeriesDialog.class.getSimpleName();
    private List<SeriesInfo> seriesInfoList = null;
    private SeriesAddSuccessCallback seriesAddSuccessCallback;
    private final Context context;
    private String title;
    private Spinner seriesSp;

    public SeriesDialog(Context context) {
        this.context = context;
    }

    public SeriesDialog(Context context, String title) {
        this.context = context;
        this.title = title;
    }

    public void setSeriesAddSuccessCallback(SeriesAddSuccessCallback seriesAddSuccessCallback) {
        this.seriesAddSuccessCallback = seriesAddSuccessCallback;
    }

    public void showDialog() {
        @SuppressLint("InflateParams")
        View seriesDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_select_series, null);
        AlertDialog dialog = CommonDialog.getDialog(context, title, null, seriesDialogView, () -> {
            if (seriesAddSuccessCallback != null) {
                try {
                    seriesAddSuccessCallback.onSave(seriesInfoList.get(seriesSp.getSelectedItemPosition()));
                } catch (ArrayIndexOutOfBoundsException e) {
                    String msg = "曲线选择失败！";
                    LogUtil.errorOut(TAG, e, msg);
                    ToastUtil.toastWithLog(msg);
                }
            }
        });
        seriesSp = seriesDialogView.findViewById(R.id.seriesSp);
        seriesInfoList = SeriesInfoController.getAll();
        String[] seriesSize = new String[seriesInfoList.size()];
        int i = 0;
        while (i < seriesSize.length) {
            seriesSize[i] = seriesInfoList.get(i).getSeriesName();
            i += 1;
        }
        SpinnerAdapter spinnerAdapter = new ArrayAdapter<>(context, R.layout.item_spinner_normal, seriesSize);
        seriesSp.setAdapter(spinnerAdapter);
        dialog.show();
    }

    public interface SeriesAddSuccessCallback {
        void onSave(SeriesInfo seriesInfo);
    }
}
