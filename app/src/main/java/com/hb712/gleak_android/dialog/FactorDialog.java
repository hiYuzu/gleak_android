package com.hb712.gleak_android.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

import com.hb712.gleak_android.R;
import com.hb712.gleak_android.adapter.FactorAdapter;
import com.hb712.gleak_android.controller.CalibrationInfoController;
import com.hb712.gleak_android.dao.DBManager;
import com.hb712.gleak_android.dao.FactorCoefficientInfoDao;
import com.hb712.gleak_android.entity.FactorCoefficientInfo;
import com.hb712.gleak_android.util.ToastUtil;

import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

/**
 * 响应因子dialog
 *
 * @author hiYuzu
 * @version V1.0
 * @date 2020/10/21 11:43
 */
public class FactorDialog {

    private int selectIndex = -1;
    private List<FactorCoefficientInfo> factorCoefficientInfoList = null;
    private FactorAddSuccessCallback factorAddSuccessCallback;
    private FactorAdapter adapter;
    private CheckBox ckbNo;
    private EditText factorNameET;
    private ListView listView;
    private final Context context;
    private String title;

    public FactorDialog(Context context) {
        this.context = context;
    }

    public FactorDialog(Context context, String title) {
        this.context = context;
        this.title = title;
    }

    public void setFactorAddSuccessCallback(FactorAddSuccessCallback factorAddSuccessCallback) {
        this.factorAddSuccessCallback = factorAddSuccessCallback;
    }

    public void showDialog() {
        @SuppressLint("InflateParams")
        View factorDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_select_factor, null);
        ckbNo = factorDialogView.findViewById(R.id.ckbNo);
        ckbNo.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectIndex = -1;
                if (adapter != null) {
                    adapter.setSelectItem(selectIndex);
                    adapter.notifyDataSetChanged();
                }
            }
        });
        listView = factorDialogView.findViewById(R.id.factorList);
        factorCoefficientInfoList = DBManager.getInstance().getReadableSession().getFactorCoefficientInfoDao().loadAll();
        adapter = new FactorAdapter(context, factorCoefficientInfoList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((paramAnonymousAdapterView, paramAnonymousView, paramAnonymousInt, paramAnonymousLong) -> {
            selectIndex = paramAnonymousInt;
            ckbNo.setChecked(false);
            adapter.setSelectItem(selectIndex);
            adapter.notifyDataSetChanged();
        });
        factorNameET = factorDialogView.findViewById(R.id.factorName);
        Button btnSearch = factorDialogView.findViewById(R.id.searchBtn);
        btnSearch.setOnClickListener(view -> {
            QueryBuilder<FactorCoefficientInfo> queryBuilder = DBManager.getInstance().getReadableSession().getFactorCoefficientInfoDao().queryBuilder();
            Property localProperty = FactorCoefficientInfoDao.Properties.factorName;
            String likeSearch = "%" +
                    factorNameET.getText().toString().trim() +
                    "%";
            List<FactorCoefficientInfo> list = queryBuilder.where(localProperty.like(likeSearch), new WhereCondition[0]).list();
            factorCoefficientInfoList.clear();
            factorCoefficientInfoList.addAll(list);
            setSelectedValue();
            adapter.notifyDataSetChanged();
        });
        CommonDialog commonDialog = new CommonDialog(context, title, null, factorDialogView, () -> {
            if (ckbNo.isChecked()) {
                factorAddSuccessCallback.onSave(null);
            } else {
                if (selectIndex < 0) {
                    ToastUtil.shortInstanceToast("请选择响应因子");
                    return;
                }
                if (factorAddSuccessCallback != null) {
                    factorAddSuccessCallback.onSave(factorCoefficientInfoList.get(selectIndex));
                }
            }
        });
        setSelectedValue();
        commonDialog.show();
    }

    private void setSelectedValue() {
        selectIndex = -1;
        FactorCoefficientInfo localFactorCoefficientInfo = CalibrationInfoController.getInstance().getFactor();
        if (localFactorCoefficientInfo != null) {
            if (factorCoefficientInfoList != null && factorCoefficientInfoList.size() > 0) {
                int i = 0;
                while (i < factorCoefficientInfoList.size()) {
                    if (factorCoefficientInfoList.get(i).getId().equals(localFactorCoefficientInfo.getId())) {
                        selectIndex = i;
                        if (adapter != null) {
                            listView.post(() -> listView.smoothScrollToPosition(selectIndex + 5));
                            adapter.setSelectItem(selectIndex);
                            adapter.notifyDataSetChanged();
                            break;
                        }
                        break;
                    }
                    i += 1;
                }
            }
        }
        if (selectIndex < 0) {
            ckbNo.setChecked(true);
            return;
        }
        ckbNo.setChecked(false);
    }

    public interface FactorAddSuccessCallback {
        void onSave(FactorCoefficientInfo factorCoefficientInfo);
    }
}
