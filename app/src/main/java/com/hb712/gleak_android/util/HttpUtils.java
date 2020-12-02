package com.hb712.gleak_android.util;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.hb712.gleak_android.MainApplication;
import com.hb712.gleak_android.interfaceabs.HttpInterface;
import com.hb712.gleak_android.interfaceabs.OKHttpListener;

import org.apache.http.params.HttpParams;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtils {
    private static final String TAG = HttpUtils.class.getSimpleName();

    public static final String KEY_USERID = "userId";
    public static final String RESULT = "result";
    public static final String CODE = "code";
    public static final String MESSAGE = "message";

    private static final int SUCCESS_CODE = 200;

    private static boolean isSendVideo = false;

    public static final OkHttpClient mClient = new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build();

    public static void get(HttpInterface httpInterface, String httpUrl, @NonNull OKHttpListener listener) {
        httpExecute(httpInterface, httpUrl, new Request.Builder().get(), listener);
    }

    public static void post(HttpInterface httpInterface, String httpUrl, String jsonString, File file, @NonNull OKHttpListener listener) {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (file != null) {
            isSendVideo = true;
            builder.addPart(MultipartBody.Part.createFormData("video", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file)));
        }

        if (jsonString != null) {
            builder.addPart(MultipartBody.Part.createFormData("data", jsonString));
        }
        httpExecute(httpInterface, httpUrl, new Request.Builder().post(builder.build()), listener);
    }

    public static void post(HttpInterface httpInterface, String httpUrl, String jsonString, @NonNull OKHttpListener listener) {
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"), jsonString);
        httpExecute(httpInterface, httpUrl, new Request.Builder().post(requestBody), listener);
    }

    private static void httpExecute(HttpInterface httpInterface, String httpUrl, Request.Builder builder, OKHttpListener listener) {
        new AsyncTask<Void, Void, Bundle>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                listener.onStart();
            }

            @Override
            protected void onPostExecute(Bundle bundle) {
                super.onPostExecute(bundle);
                if (httpInterface != null && httpInterface.isDiscardHttp()) {
                    LogUtil.infoOut(TAG, httpInterface.getActivity().getLocalClassName() + "：传输中断");
                    return;
                }
                if (bundle.getBoolean(RESULT)) {
                    if (bundle.getInt(CODE) == SUCCESS_CODE) {
                        listener.onSuccess(bundle);
                    } else {
                        listener.onServiceError(bundle);
                    }
                } else {
                    listener.onNetworkError(bundle);
                }
                listener.onNext(bundle);
            }

            @Override
            protected Bundle doInBackground(Void... params) {
                Bundle result = new Bundle();
                builder.tag(httpUrl).url(httpUrl);
                if (isSendVideo) {
                    builder.addHeader("Content-Type", "multipart/form-data");
                    isSendVideo = false;
                }

                if (MainApplication.getInstance().isLogin()) {
                    builder.addHeader(GlobalParam.KEY_USERTOKEN, MainApplication.getInstance().getToken());
                }
                try {
                    Response response = mClient.newCall(builder.build()).execute();
                    result.putInt(CODE, response.code());
                    result.putBoolean(RESULT, true);
                    if (response.code() != SUCCESS_CODE) {
                        result.putString(MESSAGE, "服务器响应异常：" + response.code());
                    } else {
                        result.putString(MESSAGE, response.body().string());
                    }
                } catch (IOException ioe) {
                    LogUtil.errorOut(TAG, ioe, "");
                    result.putBoolean(RESULT, false);
                    result.putString(MESSAGE, "无法连接到服务器，请检查网络设置");
                } catch (Exception e) {
                    LogUtil.errorOut(TAG, e, "");
                    result.putBoolean(RESULT, false);
                    result.putString(MESSAGE, e.getMessage());
                }
                return result;
            }
        }.execute();
    }
}
