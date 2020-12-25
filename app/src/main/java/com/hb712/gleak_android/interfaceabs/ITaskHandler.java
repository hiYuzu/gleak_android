package com.hb712.gleak_android.interfaceabs;

import android.os.Bundle;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/12/23 16:13
 */
public interface ITaskHandler {
    /**
     * 回调：start task
     */
    void onStart();

    /**
     * 回调：成功
     * @param bundle 返回Bundle
     */
    void onSuccess(Bundle bundle);

    /**
     * 回调：失败
     * @param failMsg 失败信息
     */
    void onFailed(String failMsg);
}
