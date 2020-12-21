package com.hb712.gleak_android.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hb712.gleak_android.R;
import com.hb712.gleak_android.entity.SeriesInfo;
import com.hb712.gleak_android.entity.SeriesLimitInfo;

import java.util.List;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/12/21 14:57
 */
public class SeriesLimitAdapter extends BaseAdapter {
    private Context context;
    private List<SeriesLimitInfo> seriesLimitInfoList;
    private int selectIndex = -1;

    public SeriesLimitAdapter(Context context, List<SeriesLimitInfo> seriesLimitInfoList) {
        this.context = context;
        this.seriesLimitInfoList = seriesLimitInfoList;
    }

    public void clear() {
        seriesLimitInfoList.clear();
    }

    @Override
    public int getCount() {
        if (seriesLimitInfoList == null) {
            return 0;
        }
        return seriesLimitInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        if (seriesLimitInfoList != null && position < seriesLimitInfoList.size()) {
            return seriesLimitInfoList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        if (seriesLimitInfoList != null && position < seriesLimitInfoList.size()) {
            return seriesLimitInfoList.get(position).getId();
        }
        return 0;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        class ViewHolder {
            public TextView seriesName;
            public TextView limitValue;
        }
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.series_info_content, null);
            viewHolder = new ViewHolder();
            viewHolder.seriesName = convertView.findViewById(R.id.seriesNameTv);
            viewHolder.limitValue = convertView.findViewById(R.id.seriesLimitTv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        SeriesLimitInfo seriesLimitInfo = seriesLimitInfoList.get(position);
        if (seriesLimitInfo.getSeriesInfo() == null) {
            viewHolder.seriesName.setText("");
        } else {
            viewHolder.seriesName.setText(seriesLimitInfo.getSeriesInfo().getSeriesName());
        }
        viewHolder.limitValue.setText(String.valueOf(seriesLimitInfo.getMaxValue()));
        convertView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        if (position == selectIndex) {
            convertView.setBackgroundColor(context.getResources().getColor(R.color.ff80cbc4, null));
            return convertView;
        }
        convertView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        return convertView;
    }

    public void setSelectItem(int position) {
        selectIndex = position;
    }
}
