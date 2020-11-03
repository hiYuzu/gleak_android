package com.hb712.gleak_android.callback;

import com.hb712.gleak_android.pojo.SeriesInfo;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/10/21 10:49
 */
public abstract interface SeriesAddSuccessCallback {
    public abstract void onSave(SeriesInfo seriesInfo);
}
