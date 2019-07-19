package com.callme.platform.api;

import com.alibaba.fastjson.support.retrofit.Retrofit2ConverterFactory;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * Desc:
 * Created by zhanghui on 2017/11/9.
 */
public class RetrofitManager {
    public static Retrofit getRetrofit(String server, OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(server)
                .addConverterFactory(new Retrofit2ConverterFactory())
                .client(client)
                .build();
    }
}
