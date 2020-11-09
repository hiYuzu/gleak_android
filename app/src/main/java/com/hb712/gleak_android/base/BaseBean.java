package com.hb712.gleak_android.base;

import java.io.Serializable;

import okhttp3.Headers;

public class BaseBean implements Serializable {
    public final String TAG = getClass().getSimpleName();
    public int code;//后台给的状态码
    public String message, detailMessage;//错误语,错误详情
    public int httpCode;//http链接的状态码
    public String response, httpUrl;
    public Headers httpHeader;//请求回来的头

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
