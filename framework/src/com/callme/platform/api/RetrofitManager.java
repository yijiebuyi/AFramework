package com.callme.platform.api;

import com.alibaba.fastjson.support.retrofit.Retrofit2ConverterFactory;

import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：
 * 作者：huangyong
 * 创建时间：2019/4/24
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class RetrofitManager {

    public static Retrofit getRetrofit(String server, OkHttpClient client) {
        return getRetrofit(server, client, new Retrofit2ConverterFactory());
    }

    public static Retrofit getRetrofit(String server, OkHttpClient client, Converter.Factory convertFactory) {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(server)
                .client(client);
        if (convertFactory != null) {
            builder.addConverterFactory(convertFactory);
        }

        return builder.build();
    }
}
