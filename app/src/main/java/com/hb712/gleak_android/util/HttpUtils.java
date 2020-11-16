package com.hb712.gleak_android.util;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.hb712.gleak_android.base.BaseBean;
import com.hb712.gleak_android.interfaceabs.DialogPopWindowInterface;
import com.hb712.gleak_android.interfaceabs.HttpInterface;
import com.hb712.gleak_android.interfaceabs.OKHttpListener;

import java.io.File;
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
    //json中的字段
    public static final String KEY_USERTOKEN = "userToken", KEY_USERID = "userId", KEY_PAGENUM = "pageNum", KEY_PAGESIZE = "pageSize", KEY_STOREID = "storeId";

    public static final OkHttpClient mClient = new OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build();

    //类型 utf-8
    public static final MediaType mMediaType = MediaType.parse("application/json;charset=utf-8");

    ///////////////////////////////////////////////////////////////////////////
    // 以下是http公共方法
    ///////////////////////////////////////////////////////////////////////////

    //get
    public static <T extends BaseBean> void getDefault(HttpInterface httpInterface, String httpUrl, Class<T> mClass,
                                                       @NonNull OKHttpListener<T> listener) {
        httpCustom(httpInterface, httpUrl, new Request.Builder(), null, mClass, mClient, listener);
    }

    //postDefault
    public static <T extends BaseBean> void postDefault(HttpInterface httpInterface, String httpUrl, MapUtils mapUtils,
                                                        Class<T> mClass, @NonNull OKHttpListener<T> listener) {

        FormBody.Builder builder = new FormBody.Builder();
        if (mapUtils != null && mapUtils.size() > 0) {
            for (Map.Entry entry : mapUtils.entrySet()) {
                builder.add(entry.getKey().toString(), entry.getValue().toString());
            }
        }
        postCustom(httpInterface, httpUrl, builder.build(), null, mClass, listener);
    }

    //postMultiple
    public static <T extends BaseBean> void postMultiple(HttpInterface httpInterface, String httpUrl, MapUtils mapUtils, MapUtils mapFileUtils,
                                                         Class<T> mClass, @NonNull OKHttpListener<T> listener) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        if (mapUtils != null && mapUtils.size() > 0) {
            for (Map.Entry entry : mapUtils.entrySet()) {
                builder.addFormDataPart(entry.getKey().toString(), entry.getValue().toString());
            }
        }
        if (mapFileUtils != null && mapFileUtils.size() > 0) {
            for (Map.Entry entry : mapFileUtils.entrySet()) {
                File file = (File) entry.getValue();
                String fileName = entry.getKey().toString();
                // MediaType.parse() 里面是上传的文件类型。
                RequestBody body = RequestBody.create(MediaType.parse("image/*"), file);
                // 参数分别为， 请求key ，文件名称 ， RequestBody
                builder.addFormDataPart("images", fileName, body);
            }
        }
        postCustom(httpInterface, httpUrl, builder.build(), null, mClass, listener);
    }

    //RequestBody  Dialog可以传null
    public static <T extends BaseBean> void postCustom(HttpInterface httpInterface, String httpUrl, RequestBody requestBody,
                                                       DialogPopWindowInterface dialog, Class<T> mClass, @NonNull OKHttpListener<T> listener) {
        httpCustom(httpInterface, httpUrl, new Request.Builder().post(requestBody), dialog, mClass, mClient, listener);
    }

    /**
     * 自定义异步请求，增加client入参，没有默认数据需要自己手动增加
     */
    public static <T extends BaseBean> void httpCustom(final HttpInterface httpInterface, final String httpUrl,
                                                       final Request.Builder builder, final DialogPopWindowInterface dialog,
                                                       final Class<T> mClass, final OkHttpClient client, final OKHttpListener<T> listener) {
        if (dialog != null) dialog.show();
        new AsyncTask<Void, Void, BaseBean>() {
            @Override
            protected void onPostExecute(BaseBean baseBean) {
                super.onPostExecute(baseBean);
                if (dialog != null && dialog.getActivity() != null && !dialog.getActivity().isFinishing()) {
                    try {
                        dialog.dismiss();
                    } catch (Exception ignored) {
                    }
                }
                //如果activity要求丢弃数据
                if (httpInterface != null && httpInterface.isDiscardHttp()) return;
                if (baseBean.httpCode == OKHttpListener.CODE_200) {
                    if (baseBean.code == OKHttpListener.CODE_SUCCESS)
                        listener.onSuccess((T) baseBean);
                    else listener.onServiceError(baseBean);
                } else {
                    listener.onNetworkError(baseBean);
                }
                listener.onNext(baseBean);
            }

            @Override
            protected BaseBean doInBackground(Void... params) {
                builder.tag(httpUrl).url(httpUrl);
                //添加头信息
//                .addHeader(KEY_USERTOKEN, "token")

                try {
                    Response response = client.newCall(builder.build()).execute();
                    String body = response.body().string();
                    if (response.code() == OKHttpListener.CODE_200) {
                        try {
                            BaseBean bean = JSON.parseObject(body, mClass, Feature.SupportNonPublicField);//支持私有变量
                            bean.httpCode = response.code();
                            bean.code = OKHttpListener.CODE_SUCCESS;
                            bean.response = body;
                            bean.httpUrl = httpUrl;
                            return bean;
                        } catch (Exception e) {
                            e.printStackTrace();
                            return new BaseBean(OKHttpListener.CODE_JSONEXCEPTION, null, body, httpUrl, response.headers());
                        }
                    } else {
                        return new BaseBean(response.code(), null, body, httpUrl, response.headers());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return new BaseBean(OKHttpListener.CODE_CONNECTXCEPTEION, null, null, httpUrl, null);
                }
            }
        }.execute();
    }
}
