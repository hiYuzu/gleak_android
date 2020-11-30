package com.hb712.gleak_android.util;

import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;

import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;

import java.util.List;


/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/9/19 10:56
 */

public class WebViewCookiesUtils {
    private static final String TAG = WebViewCookiesUtils.class.getSimpleName();

    public static void saveCookie(String url, String key, String value) {
        CookieManager cookieManager = CookieManager.getInstance();
        String cookieStr = String.format("%s=%s", key, JavaScriptEscapeUtils.escape(value));
        LogUtil.debugOut(TAG, "saveCookie: 保存cookie \"" + cookieStr +"\"" );
        cookieManager.setCookie(url, cookieStr);
        cookieManager.flush();
    }


    public static void syncCookieFromHttpClient(String url, AbstractHttpClient hc) {
        LogUtil.debugOut(TAG, "syncCookieFromHttpClient: " + url);
        CookieManager cookieManager = CookieManager.getInstance() ;
        List<Cookie> cookies = hc.getCookieStore().getCookies();
        if(!cookies.isEmpty())
        {
            for (Cookie cookie : cookies) {
                String cookieStr = cookie.getName() + "=" + cookie.getValue();
                LogUtil.debugOut(TAG, "syncCookieFromHttpClient: save cookie \"" + cookieStr +"\"" );
                cookieManager.setCookie(url, cookieStr);
            }
        }
        cookieManager.flush();
    }

    public static void clearAllCookies() {
        CookieManager cookieManager = CookieManager.getInstance();
        LogUtil.debugOut(TAG, "clearAllCookies: ");
        cookieManager.removeAllCookies(new ValueCallback<Boolean>() {
            @Override
            public void onReceiveValue(Boolean value) {
                LogUtil.debugOut(TAG, "Remove Cookie onReceiveValue: " + String.valueOf(value));
            }
        });
        cookieManager.flush();
    }

    public static String getWebViewCookies(String url) {
        CookieManager cookieManager = CookieManager.getInstance();
        return cookieManager.getCookie(url);
    }
}
