package com.hb712.gleak_android.message.net;

import com.hb712.gleak_android.base.BaseMessage;

/**
 * 上传漏点检测数据
 */
public class LeakDataRequest extends BaseMessage {

    private String type;
    private Data data;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
