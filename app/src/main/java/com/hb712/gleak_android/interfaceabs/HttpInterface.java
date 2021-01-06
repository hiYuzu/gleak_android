package com.hb712.gleak_android.interfaceabs;

import android.app.Activity;

/**
 * HttpUtils的必传项,主要是用来解决activity.finish时崩溃的问题
 *
 * @author hiYuzu
 * @version V1.0
 * @date 2020/12/21 14:57
 */
public interface HttpInterface {

    /**
     * 获取 activity 实例
     * @return {@link Activity}
     */
    Activity getActivity();

    /**
     * 丢弃http请求的数据数据
     * @return result
     */
    boolean isDiscardHttp();
}
