package com.hb712.gleak_android.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hb712.gleak_android.R;
import com.hb712.gleak_android.entity.FactorCoefficientInfo;

import java.util.List;

import androidx.annotation.RequiresApi;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/10/21 14:02
 */
public class FactorAdapter extends BaseAdapter {
    private Context context;
    private List<FactorCoefficientInfo> factorCoefficientInfoList;
    private int selectItem = -1;

    public FactorAdapter(Context context, List<FactorCoefficientInfo> factorCoefficientInfoList) {
        this.context = context;
        this.factorCoefficientInfoList = factorCoefficientInfoList;
    }

    public void clear() {
        factorCoefficientInfoList.clear();
    }

    public int getCount() {
        return factorCoefficientInfoList == null ? 0 : factorCoefficientInfoList.size();
    }

    public FactorCoefficientInfo getItem(int paramInt) {
        if (factorCoefficientInfoList != null && paramInt < factorCoefficientInfoList.size()) {
            return factorCoefficientInfoList.get(paramInt);
        }
        return null;
    }

    public long getItemId(int paramInt) {
        if (factorCoefficientInfoList != null && factorCoefficientInfoList.size() > paramInt) {
            return (factorCoefficientInfoList.get(paramInt)).getId();
        }
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("InflateParams")
    @Override
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
        ViewHolder viewHolder;
        if (paramView == null) {
            paramView = LayoutInflater.from(context).inflate(R.layout.factor_content, null);
            viewHolder = new ViewHolder();
            viewHolder.factorTV = paramView.findViewById(R.id.factorTextView);
            paramView.setTag(viewHolder);
        } else {
            viewHolder = ((ViewHolder) paramView.getTag());
        }
        viewHolder.factorTV.setText(factorCoefficientInfoList.get(paramInt).getFactorName());
        if (paramInt == this.selectItem) {
            paramView.setBackgroundColor(context.getResources().getColor(R.color.ff80cbc4, null));
            return paramView;
        }
        paramView.setBackgroundColor(0);
        return paramView;
    }

    public void setSelectItem(int paramInt) {
        this.selectItem = paramInt;
    }

    private final class ViewHolder {
        TextView factorTV;

        ViewHolder() {
        }
    }
}
