package com.callme.platform.api.interceptor;

import android.util.Log;

import com.callme.platform.api.HttpHeader;

import java.io.IOException;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：参数追加(token, 兼容老版本)
 * [ref]: https://www.jianshu.com/p/f77d379ebcfa
 * 作者：huangyong
 * 创建时间：2019/8/5
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ParamsInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        //拿到拥有以前的request里的url的那些信息的builder
        Request request = chain.request();
        boolean get = request.method().equals("GET");
        String url = request.url().toString();
        //Log.i("ParamsInterceptor", url);
        try {
            if (get) {
                HttpUrl.Builder builder = request
                        .url()
                        .newBuilder();

                HttpUrl newUrl = builder.addQueryParameter("token", HttpHeader.USER_TOKEN)
                        .build();

                Request newRequest = request
                        .newBuilder()
                        .url(newUrl)
                        .build();
                return chain.proceed(newRequest);
            } else {
                RequestBody oldBody = request.body();
                Request.Builder newRequestBuild = null;
                String postBodyString = "";
                if (oldBody instanceof FormBody) {
                    FormBody.Builder formBodyBuilder = new FormBody.Builder();
                    formBodyBuilder.add("token", HttpHeader.USER_TOKEN);
                    newRequestBuild = request.newBuilder();

                    RequestBody formBody = formBodyBuilder.build();
                    postBodyString = bodyToString(request.body());
                    postBodyString += ((postBodyString.length() > 0) ? "&" : "") + bodyToString(formBody);
                    newRequestBuild.post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8"), postBodyString));
                } else if (oldBody instanceof MultipartBody) {
                    MultipartBody oldBodyMultipart = (MultipartBody) oldBody;
                    List<MultipartBody.Part> oldPartList = oldBodyMultipart.parts();
                    MultipartBody.Builder builder = new MultipartBody.Builder();
                    builder.setType(MultipartBody.FORM);
                    RequestBody addRequestBody = RequestBody.create(MediaType.parse("text/plain"), HttpHeader.USER_TOKEN);
                    Headers headers = Headers.of("Content-Disposition", "form-data;name=\"token\"", "Content-Transfer-Encoding", "binary");
                    MultipartBody.Part addPart = MultipartBody.Part.create(headers, addRequestBody);
                    for (MultipartBody.Part part : oldPartList) {
                        builder.addPart(part);
                        //postBodyString += (bodyToString(part.body()) + "\n");
                    }
                    //postBodyString += (bodyToString(requestBody1) + "\n");
                    //builder.addPart(oldBody);  //不能用这个方法，因为不知道oldBody的类型，可能是PartMap过来的，
                    //也可能是多个Part过来的，所以需要重新逐个加载进去
                    builder.addPart(addPart);
                    newRequestBuild = request.newBuilder();
                    newRequestBuild.post(builder.build());
                    Log.e("", "MultipartBody," + request.url());
                } else {
                    newRequestBuild = request.newBuilder();
                }

                Request newRequest = newRequestBuild.build();

                Response response = chain.proceed(newRequest);
                MediaType mediaType = response.body().contentType();
                String content = response.body().string();
                return response.newBuilder()
                        .body(okhttp3.ResponseBody.create(mediaType, content))
                        .build();
            }
        } catch (Exception e) {
            return chain.proceed(request);
        }
    }

    private static String bodyToString(final RequestBody request) {
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            if (copy != null)
                copy.writeTo(buffer);
            else
                return "";
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }
}
