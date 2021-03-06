package com.callme.platform.api.interceptor;

import android.content.Context;
import android.text.TextUtils;

import com.callme.platform.util.ApnUtil;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：Http 缓存拦截器
 * 作者：huangyong
 * 创建时间：2018/8/22
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class CacheInterceptor implements Interceptor {

    private Context mContext;

    public CacheInterceptor(Context context) {
        mContext = context.getApplicationContext();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        //有直接获取网络上面的数据，否则去缓存里面取数据(如果接口设置缓存的情况下)
        boolean networkAvailable = ApnUtil.isNetworkAvailable(mContext);
        String cacheControl = request.cacheControl().toString();
        if (!TextUtils.isEmpty(cacheControl)) {
            if (!networkAvailable) {
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build();
            }

            Response originalResponse = chain.proceed(request);
            if (ApnUtil.isNetworkAvailable(mContext)) {
                return originalResponse.newBuilder()
                        .header("Cache-Control", cacheControl)
                        .removeHeader("Pragma")
                        .build();
            } else {
                int maxTime = 2 * 60 * 60; //单位s
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxTime)
                        .removeHeader("Pragma")
                        .build();

            }
        } else {
            return chain.proceed(request);
        }
    }
}
