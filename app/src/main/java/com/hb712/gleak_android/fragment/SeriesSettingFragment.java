package com.hb712.gleak_android.fragment;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.hb712.gleak_android.R;
import com.hb712.gleak_android.adapter.SeriesInfoAdapter;
import com.hb712.gleak_android.controller.SeriesInfoController;
import com.hb712.gleak_android.dao.CalibrationInfoDao;
import com.hb712.gleak_android.dao.DBManager;
import com.hb712.gleak_android.dao.SeriesInfoDao;
import com.hb712.gleak_android.dao.SeriesLimitInfoDao;
import com.hb712.gleak_android.entity.SeriesInfo;
import com.hb712.gleak_android.util.LogUtil;
import com.hb712.gleak_android.util.ToastUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class SeriesSettingFragment extends Fragment {
    private static final String TAG = SeriesSettingFragment.class.getSimpleName();
    private EditText newSeriesNameEt;

    private List<SeriesInfo> seriesInfoList;
    private SeriesInfoAdapter seriesInfoAdapter;

    private int selectIndex = -1;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_series_setting, container, false);
        initView(fragment);
        return fragment;
    }

    private void initView(View fragment) {
        newSeriesNameEt = fragment.findViewById(R.id.seriesNameSetting);
        seriesInfoList = SeriesInfoController.getAllEdits();
        ListView listView = fragment.findViewById(R.id.seriesNameList);
        seriesInfoAdapter = new SeriesInfoAdapter(getContext(), seriesInfoList);
        listView.setAdapter(seriesInfoAdapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            selectIndex = position;
            seriesInfoAdapter.setSelectItem(position);
            seriesInfoAdapter.notifyDataSetChanged();
        });
        Button addNewSeriesBtn = fragment.findViewById(R.id.addNewSeriesSetting);
        addNewSeriesBtn.setOnClickListener(v -> {
            String seriesName = newSeriesNameEt.getText().toString().trim();
            if (seriesName.isEmpty()) {
                ToastUtil.toastWithoutLog("请输入曲线名称！");
                return;
            }
            SeriesInfo seriesInfo = new SeriesInfo();
            seriesInfo.setStdSeries(false);
            seriesInfo.setSeriesName(seriesName);
            boolean isHave = false;
            for (SeriesInfo temp : seriesInfoList) {
                if (temp.getSeriesName().equals(seriesInfo.getSeriesName())) {
                    seriesInfo.setId(temp.getId());
                    isHave = true;
                    break;
                }
            }
            if (!isHave) {
                seriesInfoList.add(seriesInfo);
            }
            try {
                DBManager.getInstance().getWritableSession().getSeriesInfoDao().save(seriesInfo);
            } catch (Exception e) {
                ToastUtil.toastWithoutLog("本地数据库发生错误！");
                LogUtil.assertOut(TAG, e, "SeriesInfoDao");
            }
            selectIndex = -1;
            seriesInfoAdapter.setSelectItem(selectIndex);
            seriesInfoAdapter.notifyDataSetChanged();
        });
        Button deleteSeriesBtn = fragment.findViewById(R.id.deleteSeriesSetting);
        deleteSeriesBtn.setOnClickListener(v -> {
            int i = selectIndex;
            if (i > -1) {
                SeriesInfo seriesInfo = seriesInfoList.get(i);
                if (seriesInfo.isStdSeries()) {
                    ToastUtil.toastWithoutLog("标准曲线不能删除！");
                } else {
                    seriesInfoList.remove(selectIndex);
                    selectIndex = -1;
                    try {
                        DBManager.getInstance().getWritableSession().getSeriesInfoDao().delete(seriesInfo);
                    } catch (Exception e) {
                        ToastUtil.toastWithoutLog("本地数据库发生错误！");
                        LogUtil.assertOut(TAG, e, "SeriesInfoDao");
                    }
                    deleteInvokeSeriesData(seriesInfo);
                    seriesInfoAdapter.setSelectItem(selectIndex);
                    seriesInfoAdapter.notifyDataSetChanged();
                }
                return;
            }
            ToastUtil.toastWithoutLog("请选择要删除的项目！");
        });
    }

    private void deleteInvokeSeriesData(SeriesInfo seriesInfo) {
        try {
            DBManager.getInstance().getWritableSession().getSeriesLimitInfoDao().queryBuilder().where(SeriesLimitInfoDao.Properties.SERIES_ID.eq(seriesInfo.getId()), new WhereCondition[0]).buildDelete().executeDeleteWithoutDetachingEntities();
            DBManager.getInstance().getWritableSession().getCalibrationInfoDao().queryBuilder().where(CalibrationInfoDao.Properties.SERIES_ID.eq(seriesInfo.getId()), new WhereCondition[0]).buildDelete().executeDeleteWithoutDetachingEntities();
        } catch (Exception e) {
            ToastUtil.toastWithoutLog("本地数据库发生错误！");
            LogUtil.assertOut(TAG, e, "SeriesLimitInfoDao | CalibrationInfoDao");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        seriesInfoAdapter.notifyDataSetChanged();
    }
}