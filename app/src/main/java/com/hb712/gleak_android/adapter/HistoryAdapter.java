package com.hb712.gleak_android.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hb712.gleak_android.MainApplication;
import com.hb712.gleak_android.R;
import com.hb712.gleak_android.entity.DetectInfo;
import com.hb712.gleak_android.util.UnitUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.RequiresApi;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/10/23 12:18
 */
public class HistoryAdapter extends BaseAdapter {
    private final Context context;
    public List<DetectInfoView> detectInfoViewList;
    private boolean unitPpm = false;
    private int selectItem = -1;

    public HistoryAdapter(Context context, List<DetectInfo> detectInfoList) {
        this.context = context;
        detectInfoViewList = new ArrayList<>();
        addData(detectInfoList);
    }

    public void addData(List<DetectInfo> detectInfoList) {
        if (detectInfoList != null) {
            for (int i = 0; i < detectInfoList.size(); i++) {
                DetectInfoView detectInfoView = new DetectInfoView();
                detectInfoView.number = (i + 1);
                detectInfoView.detectInfo = detectInfoList.get(i);
                detectInfoViewList.add(detectInfoView);
            }
        }
    }

    public void clear() {
        detectInfoViewList.clear();
    }

    @Override
    public int getCount() {
        return detectInfoViewList.size();
    }

    @Override
    public Object getItem(int paramInt) {
        return paramInt < detectInfoViewList.size() ? detectInfoViewList.get(paramInt) : null;
    }

    @Override
    public long getItemId(int paramInt) {
        if (detectInfoViewList.size() > paramInt) {
            DetectInfoView detectInfoView = detectInfoViewList.get(paramInt);
            if (detectInfoView != null) {
                return detectInfoView.detectInfo.getId();
            }
            return 0L;
        }
        return 0L;
    }

    private double getUnitValue(double paramDouble) {
        return unitPpm ? paramDouble : UnitUtil.getMg(paramDouble);
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        class ViewHolder {
            TextView number;
            TextView leakName;
            TextView monitorValue;
            TextView monitorTime;
        }
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.history_list_content, null);
            viewHolder = new ViewHolder();
            viewHolder.number = convertView.findViewById(R.id.number);
            viewHolder.leakName = convertView.findViewById(R.id.leakNameList);
            viewHolder.monitorValue = convertView.findViewById(R.id.monitorValue);
            viewHolder.monitorTime = convertView.findViewById(R.id.monitorTime);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = ((ViewHolder) convertView.getTag());
        }
        DetectInfoView detectInfoView = detectInfoViewList.get(position);
        viewHolder.number.setText(String.valueOf(detectInfoView.number));
        viewHolder.leakName.setText(detectInfoView.detectInfo.getLeakName());
        viewHolder.monitorValue.setText(new DecimalFormat("0.0").format(getUnitValue(detectInfoView.detectInfo.getMonitorValue())));
        viewHolder.monitorTime.setText(detectInfoView.detectInfo.getMonitorTime());
        if (position == selectItem) {
            convertView.setBackgroundColor(context.getResources().getColor(R.color.ff80cbc4, null));
            return convertView;
        }
        convertView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        return convertView;
    }

    public void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }

    public void setUnitPpm(boolean unitPpm) {
        this.unitPpm = unitPpm;
    }

    public static class DetectInfoView {
        public int number;
        DetectInfo detectInfo = new DetectInfo();
    }
}
