package com.hb712.gleak_android.service;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import com.hb712.gleak_android.MainApplication;
import com.hb712.gleak_android.util.LogUtil;
import com.hb712.gleak_android.util.WebViewCookiesUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/9/22 10:20
 */
public class WebServiceClient {

    private static final String TAG = WebServiceClient.class.getSimpleName();
    private final AbstractHttpClient mHttpClient;

    private static final String RESULT = "result";
    private static final String SUCCESS_ENTITY = "entity";
    private static final String ERROR_MSG = "error_msg";

    public interface TaskHandler {
        void onStart();

        void onSuccess(JSONObject obj);

        void onFailed(String errMsg);
    }

    AbstractHttpClient getHttpClient() {
        return mHttpClient;
    }

    public WebServiceClient() {
        mHttpClient = new DefaultHttpClient();
    }

    public void runLoginTask(TaskHandler handler, String username, String password) {
        new LoginTask(this, handler).execute(username, password);
    }

    public void runCheckUpdateTask(TaskHandler handler, String appId) {
        new CheckUpdateTask(this, handler).execute(appId);
    }

    /**
     * 抽象类，定义通用接口
     */
    private static abstract class BaseTask extends AsyncTask<String, String, Bundle> {
        private final TaskHandler mHandler;
        private final WebServiceClient mClient;

        static class TaskException extends Exception {
            TaskException(@Nullable Exception e, String message) {
                super(message);
                LogUtil.errorOut(TAG, e, message);
            }
        }

        BaseTask(WebServiceClient serviceClient, TaskHandler handler) {
            super();
            mClient = serviceClient;
            mHandler = handler;
        }

        @Override
        protected void onPostExecute(Bundle bundle) {
            super.onPostExecute(bundle);
            boolean result = bundle.getBoolean(RESULT);
            if (result) {
                try {
                    JSONObject retObj = new JSONObject(bundle.getString(SUCCESS_ENTITY, ""));
                    mHandler.onSuccess(retObj);
                } catch (JSONException e) {
                    LogUtil.errorOut(TAG, e, "内部错误：1003");
                    mHandler.onFailed("内部错误：1003");
                }
            } else {
                String errMsg = bundle.getString(ERROR_MSG, "");
                LogUtil.debugOut(TAG, errMsg);
                mHandler.onFailed(errMsg);
            }

        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected Bundle doInBackground(String... strings) {
            Bundle result = new Bundle();
            AbstractHttpClient hc = mClient.getHttpClient();
            HttpConnectionParams.setConnectionTimeout(hc.getParams(), 5000);
            try {
                HttpGet hg = createHttpGet(strings);
                onPreRequest(hc, hg);
                HttpResponse hr = hc.execute(hg);
                onPostedRequest(hc, hg);

                int statusCode = hr.getStatusLine().getStatusCode();
                if (statusCode != HttpStatus.SC_OK) {
                    result.putBoolean(RESULT, true);
                    result.putString(ERROR_MSG, onResponseFailed(statusCode));
                } else {
                    String resultStr = EntityUtils.toString(hr.getEntity());
                    LogUtil.debugOut(TAG, "BaseTask: Response Entity: " + resultStr);
                    result.putBoolean(RESULT, true);
                    result.putString(SUCCESS_ENTITY, onResponseSuccess(resultStr));
                }
            } catch (ClientProtocolException e) {
                LogUtil.errorOut(TAG, e, "内部错误: 1001");
                result.putBoolean(RESULT, false);
                result.putString(ERROR_MSG, "内部错误: 1001");
            } catch (IOException e) {
                LogUtil.errorOut(TAG, e, "无法连接到服务器，请检查网络配置");
                result.putBoolean(RESULT, false);
                result.putString(ERROR_MSG, "无法连接到服务器，请检查网络配置");
            } catch (TaskException e) {
                result.putBoolean(RESULT, false);
                result.putString(ERROR_MSG, e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mHandler.onStart();
        }

        protected abstract HttpEntity createRequestEntity(String... strings) throws TaskException;

        protected abstract HttpGet createHttpGet(String... strings);

        protected void onPreRequest(AbstractHttpClient httpClient, HttpRequest request) throws TaskException {
        }

        protected void onPostedRequest(AbstractHttpClient httpClient, HttpRequest request) throws TaskException {

        }

        protected String onResponseSuccess(String resultStr) throws TaskException {
            return resultStr;
        }

        String onResponseFailed(int responseCode) {
            return "服务器响应异常 :" + responseCode;
        }
    }

    private static class LoginTask extends BaseTask {
        private static final String LOGIN_URL = MainApplication.getInstance().baseUrl + "/api/login";

        LoginTask(WebServiceClient serviceClient, TaskHandler handler) {
            super(serviceClient, handler);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            WebViewCookiesUtils.clearAllCookies();
        }

        @Override
        protected HttpEntity createRequestEntity(String... strings) throws TaskException {
            return Utils.buildEntity(strings[0], strings[1]);
        }

        @Override
        protected HttpGet createHttpGet(String... strings) {
            return Utils.createHttpGet(strings[0], strings[1], getActionUrl());
        }

        private String getActionUrl() {
            return LOGIN_URL;
        }

        @Override
        protected void onPreRequest(AbstractHttpClient httpClient, HttpRequest request) throws TaskException {
            super.onPreRequest(httpClient, request);
            httpClient.getCookieStore().clear();
        }

        @Override
        protected void onPostedRequest(AbstractHttpClient httpClient, HttpRequest request) throws TaskException {
            super.onPostedRequest(httpClient, request);
            WebViewCookiesUtils.syncCookieFromHttpClient(MainApplication.getInstance().baseUrl, httpClient);
        }

        @Override
        protected String onResponseSuccess(String resultStr) throws TaskException {
            super.onResponseSuccess(resultStr);
            try {
                JSONObject responseObj = new JSONObject(resultStr);
                if (!responseObj.getBoolean("status")) {
                    throw new TaskException(null, "认证失败:" + responseObj.getString("msg"));
                }
                JSONObject retObj = new JSONObject();
                return retObj.put("token", responseObj.getJSONObject("data").getString("token"))
                        .put("userId", responseObj.getJSONObject("data").getString("userId")).toString();
            } catch (JSONException e) {
                throw new TaskException(e, "解析认证返回信息失败");
            }
        }

    }

    private static class CheckUpdateTask extends BaseTask {
        private static final String UPDATE_URL = MainApplication.getInstance().baseUrl + "/api/queryUpdate";

        CheckUpdateTask(WebServiceClient serviceClient, TaskHandler handler) {
            super(serviceClient, handler);
        }

        @Override
        protected HttpEntity createRequestEntity(String... strings) throws TaskException {
            return Utils.buildEntity(strings[0]);
        }

        @Override
        protected HttpGet createHttpGet(String... strings) {
            return Utils.createHttpGet(strings[0], getActionUrl());
        }

        private String getActionUrl() {
            return UPDATE_URL;
        }
    }



    static class Utils {

        private static HttpGet createHttpGet(String appId, String url) {
            return new HttpGet(url + "?appId=" + appId);
        }

        private static HttpGet createHttpGet(String username, String password, String url) {
            return new HttpGet(url + "?name=" + username + "&password=" + password);
        }

        private static HttpEntity buildEntity(String username, String password) throws BaseTask.TaskException {
            List<NameValuePair> valuePairs = new ArrayList<>();
            valuePairs.add(new BasicNameValuePair("name", username));
            valuePairs.add(new BasicNameValuePair("password", password));

            try {
                return new UrlEncodedFormEntity(valuePairs);
            } catch (UnsupportedEncodingException e) {
                throw new BaseTask.TaskException(e, "内部错误：1000");
            }
        }

        private static HttpEntity buildEntity(String appId) throws BaseTask.TaskException {
            List<NameValuePair> valuePairs = new ArrayList<>();
            valuePairs.add(new BasicNameValuePair("appId", appId));

            try {
                return new UrlEncodedFormEntity(valuePairs);
            } catch (UnsupportedEncodingException e) {
                throw new BaseTask.TaskException(e, "内部错误：1000");
            }
        }

    }
}
