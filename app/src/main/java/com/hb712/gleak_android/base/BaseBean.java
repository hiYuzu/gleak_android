package com.hb712.gleak_android.base;

import java.io.Serializable;

import okhttp3.Headers;

public class BaseBean implements Serializable {
    public final String TAG = getClass().getSimpleName();
    // 后台给的状态码
    public int code;
    // 错误语
    public String message;
    // 错误详情
    public String detailMessage;
    // http链接的状态码
    public int httpCode;
    public String response;
    public String httpUrl;
    // 请求回来的头
    public Headers httpHeader;

    public BaseBean() {
    }

    public BaseBean(int httpCode, String message, String response, String httpUrl, Headers httpHeader) {
        this.httpCode = httpCode;
        this.message = message;
        this.response = response;
        this.httpUrl = httpUrl;
        this.httpHeader = httpHeader;
    }
}
