package com.hb712.gleak_android.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hb712.gleak_android.R;
import com.hb712.gleak_android.pojo.DetectInfo;
import com.hb712.gleak_android.util.UnitManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/10/23 12:18
 */
public class HistoryAdapter extends BaseAdapter {
    private Context context;
    private List<DetectInfoView> detectInfoViewList = new ArrayList<>();
    private boolean unitPPM = false;
    private int selectItem = -1;

    public HistoryAdapter(Context context, List<DetectInfo> detectInfoList) {
        this.context = context;
        AddData(detectInfoList);
    }

    private void AddData(List<DetectInfo> detectInfoList) {
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

    public int getCount() {
        return detectInfoViewList != null ? detectInfoViewList.size() : 0;
    }

    public Object getItem(int paramInt) {
        return detectInfoViewList != null && paramInt < detectInfoViewList.size() ? detectInfoViewList.get(paramInt) : null;
    }

    public long getItemId(int paramInt) {
        if (detectInfoViewList != null && detectInfoViewList.size() > paramInt) {
            DetectInfoView detectInfoView = detectInfoViewList.get(paramInt);
            if (detectInfoView != null) {
                return detectInfoView.detectInfo.getId();
            }
            return 0L;
        }
        return 0L;
    }

    private double getUnitValue(double paramDouble) {
        return unitPPM ? paramDouble : UnitManager.getMg(paramDouble);
    }

    @SuppressLint("InflateParams")
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.history_list_content, null);
            viewHolder = new ViewHolder();
            viewHolder.number = convertView.findViewById(R.id.number);
            viewHolder.sampleName = convertView.findViewById(R.id.sampleName);
            viewHolder.sampleDetectTime = convertView.findViewById(R.id.sampleDetectTime);
            viewHolder.sampleDetectValue = convertView.findViewById(R.id.sampleDetectValue);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = ((ViewHolder) convertView.getTag());
        }
        DetectInfoView detectInfoView = detectInfoViewList.get(position);
        viewHolder.number.setText(String.valueOf(detectInfoView.number));
        viewHolder.sampleName.setText(detectInfoView.detectInfo.getSampleName());
        viewHolder.sampleDetectTime.setText(detectInfoView.detectInfo.getSampleDetectTime());
        viewHolder.sampleDetectValue.setText(new DecimalFormat("0.0").format(getUnitValue(detectInfoView.detectInfo.getSampleDetectValue())));
        if (position == selectItem) {
            convertView.setBackgroundColor(context.getResources().getColor(R.color.ff80cbc4));
            return convertView;
        }
        convertView.setBackgroundColor(0);
        return convertView;
    }

    public void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }

    public void setUnitPPM(boolean unitPPM) {
        this.unitPPM = unitPPM;
    }

    public static class DetectInfoView {
        public int number;
        DetectInfo detectInfo = new DetectInfo();
    }

    private final class ViewHolder {
        TextView number;
        TextView sampleName;
        TextView sampleDetectTime;
        TextView sampleDetectValue;

        ViewHolder() {
        }
    }
}
