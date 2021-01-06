package com.hb712.gleak_android.fragment;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.hb712.gleak_android.R;
import com.hb712.gleak_android.adapter.SeriesLimitAdapter;
import com.hb712.gleak_android.controller.SeriesInfoController;
import com.hb712.gleak_android.dao.DBManager;
import com.hb712.gleak_android.entity.SeriesInfo;
import com.hb712.gleak_android.entity.SeriesLimitInfo;
import com.hb712.gleak_android.util.LogUtil;
import com.hb712.gleak_android.util.ToastUtil;

import java.util.List;

public class LimitSettingFragment extends Fragment {
    private static final String TAG = LimitSettingFragment.class.getSimpleName();
    private View fragment;
    private Spinner seriesSettingSp;
    private EditText limitValue;
    private ListView seriesLimitListView;

    private List<SeriesInfo> seriesInfoList;
    private SeriesLimitAdapter seriesLimitAdapter;
    private List<SeriesLimitInfo> seriesLimitInfoList;
    private int selectIndex = -1;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        fragment = inflater.inflate(R.layout.fragment_limit_setting, container, false);
        initView(fragment);
        updateData();
        return fragment;
    }

    private void initView(View fragment) {
        limitValue = fragment.findViewById(R.id.limitValue);
        seriesLimitListView = fragment.findViewById(R.id.seriesLimitList);
        seriesLimitListView.setOnItemClickListener((parent, view, position, id) -> {
            selectIndex = position;
            seriesLimitAdapter.setSelectItem(position);
            seriesLimitAdapter.notifyDataSetChanged();
        });
        Button add = fragment.findViewById(R.id.addNewLimitSetting);
        add.setOnClickListener(v -> {
            String limitValue = this.limitValue.getText().toString().trim();
            if (limitValue.isEmpty()) {
                ToastUtil.longToastShow("请输入限值！");
                return;
            }
            int position = seriesSettingSp.getSelectedItemPosition();
            double d = Double.parseDouble(limitValue);
            SeriesLimitInfo seriesLimitInfo = new SeriesLimitInfo();
            seriesLimitInfo.setSeriesId(seriesInfoList.get(position).getId());
            seriesLimitInfo.setSeriesInfo(seriesInfoList.get(position));
            seriesLimitInfo.setMaxValue(d);
            boolean isHave = false;
            for (SeriesLimitInfo temp : seriesLimitInfoList) {
                if (temp.getSeriesId() == seriesLimitInfo.getSeriesId()) {
                    temp.setMaxValue(seriesLimitInfo.getMaxValue());
                    seriesLimitInfo.setId(temp.getId());
                    isHave = true;
                    break;
                }
            }
            if (!isHave) {
                seriesLimitInfoList.add(seriesLimitInfo);
            }
            try {
                DBManager.getInstance().getWritableSession().getSeriesLimitInfoDao().save(seriesLimitInfo);
            } catch (Exception e) {
                ToastUtil.longToastShow("本地数据库发生错误！");
                LogUtil.assertOut(TAG, e, "SeriesInfoDao");
            }
            selectIndex = -1;
            seriesLimitAdapter.setSelectItem(selectIndex);
            seriesLimitAdapter.notifyDataSetChanged();
            this.limitValue.setText("");
        });
        Button del = fragment.findViewById(R.id.deleteLimitSetting);
        del.setOnClickListener(v -> {
            if (selectIndex > -1) {
                SeriesLimitInfo seriesLimitInfo = seriesLimitInfoList.get(selectIndex);
                seriesLimitInfoList.remove(selectIndex);
                selectIndex = -1;
                try {
                    DBManager.getInstance().getWritableSession().getSeriesLimitInfoDao().delete(seriesLimitInfo);
                } catch (Exception e) {
                    ToastUtil.longToastShow("本地数据库发生错误！");
                    LogUtil.assertOut(TAG, e, "SeriesLimitInfoDao");
                }
                seriesLimitAdapter.setSelectItem(selectIndex);
                seriesLimitAdapter.notifyDataSetChanged();
                return;
            }
            ToastUtil.longToastShow("请选择要删除的项目！");
        });
    }

    private void updateData() {
        seriesInfoList = SeriesInfoController.getAll();
        String[] seriesNames = new String[seriesInfoList.size()];
        for (int i = 0; i < seriesInfoList.size(); i++) {
            seriesNames[i] = seriesInfoList.get(i).getSeriesName();
        }
        seriesSettingSp = fragment.findViewById(R.id.seriesSettingSp);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.item_spinner_normal, seriesNames);
        seriesSettingSp.setAdapter(arrayAdapter);

        try {
            seriesLimitInfoList = DBManager.getInstance().getReadableSession().getSeriesLimitInfoDao().loadAll();
        } catch (Exception e) {
            ToastUtil.longToastShow("本地数据库发生错误！");
            LogUtil.assertOut(TAG, e, "SeriesLimitInfoDao");
        }

        seriesLimitAdapter = new SeriesLimitAdapter(getContext(), seriesLimitInfoList);
        seriesLimitListView.setAdapter(seriesLimitAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        seriesLimitAdapter.notifyDataSetChanged();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            updateData();
        }
    }
}