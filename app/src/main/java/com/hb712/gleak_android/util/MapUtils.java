package com.hb712.gleak_android.util;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;


@MainThread
public final class MapUtils extends JSONObject implements Serializable {
    private static MapUtils mMap = new MapUtils();

    private MapUtils() {
    }

    /**
     * 返回自己
     */
    @Override
    public MapUtils put(@NonNull String k, Object v) {
        if (v instanceof CharSequence) {//CharSequence说明是字符串
            v = v.toString();
        }
        super.put(k, v);
        return this;
    }

    @Nullable
    public Object get(@NonNull String k) {
        return super.get(k);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 以下是获取方法
    ///////////////////////////////////////////////////////////////////////////

    //获得一个新的实例
    public static MapUtils getNewInstance() {
        return new MapUtils();
    }

    /**
     * 只能使用在网络请求,其他地方请使用{@link JSONObject}或{@link #getNewInstance}
     * 获得单一实例,注意代码执行顺序,当是list的时候使用{@link #getNewInstance}或直接使用{@link com.alibaba.fastjson.JSONArray}
     */
    public static MapUtils getHttpInstance() {
        mMap.clear();
        return mMap;
    }
}
