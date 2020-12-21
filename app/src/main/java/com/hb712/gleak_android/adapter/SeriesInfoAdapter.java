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

import java.util.List;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/12/21 9:50
 */
public class SeriesInfoAdapter extends BaseAdapter {
    private final Context context;
    public List<SeriesInfo> seriesInfoList;
    private int selectIndex = -1;

    public SeriesInfoAdapter(Context context, List<SeriesInfo> seriesInfoList) {
        this.context = context;
        this.seriesInfoList = seriesInfoList;
    }

    public void clear() {
        seriesInfoList.clear();
    }

    @Override
    public int getCount() {
        if (seriesInfoList == null) {
            return 0;
        }
        return seriesInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        if (seriesInfoList != null && position < seriesInfoList.size()) {
            return seriesInfoList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        if (seriesInfoList != null && position < seriesInfoList.size()) {
            return seriesInfoList.get(position).getId();
        }
        return 0;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        class ViewHolder {
            public TextView seriesName;
        }
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.series_info_content, null);
            viewHolder = new ViewHolder();
            viewHolder.seriesName = convertView.findViewById(R.id.seriesNameTextView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        SeriesInfo seriesInfo = seriesInfoList.get(position);
        viewHolder.seriesName.setText(seriesInfo.getSeriesName());
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
