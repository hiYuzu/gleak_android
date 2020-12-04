package com.hb712.gleak_android.interfaceabs;

import android.app.Activity;

/**
 * HttpUtils的必传项,主要是用来解决activity.finish时崩溃的问题
 */
public interface HttpInterface {
    Activity getActivity();

    //是否丢弃http请求的数据数据
    boolean isDiscardHttp();
}
