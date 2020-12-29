package com.hb712.gleak_android.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hb712.gleak_android.R;

import java.util.List;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/12/29 11:16
 */
public class DeviceAdapter extends BaseAdapter {
    private final Context context;
    private final List<String> pairedDeviceList;

    public DeviceAdapter(Context context, List<String> pairedDeviceList) {
        this.context = context;
        this.pairedDeviceList = pairedDeviceList;
    }

    public void clear() {
        pairedDeviceList.clear();
    }

    @Override
    public int getCount() {
        return pairedDeviceList == null ? 0 : pairedDeviceList.size();
    }

    @Override
    public Object getItem(int position) {
        if (pairedDeviceList != null && position < pairedDeviceList.size()) {
            return pairedDeviceList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        class ViewHolder {
            ImageView imageView;
            TextView textView;
        }
        ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.device_name, null);
            viewHolder = new ViewHolder();
            viewHolder.imageView = view.findViewById(R.id.device_icon);
            viewHolder.textView = view.findViewById(R.id.device_info);
            view.setTag(viewHolder);
        } else {
            viewHolder = ((ViewHolder) view.getTag());
        }
        viewHolder.textView.setText(pairedDeviceList.get(position));
        view.setBackgroundColor(Color.WHITE);
        return view;
    }
}
