package com.callme.platform.api.interceptor;

import android.content.Context;
import android.os.Build;

import com.callme.platform.api.HttpHeader;
import com.callme.platform.util.ApnUtil;
import com.callme.platform.util.DevicesUtil;
import com.callme.platform.util.OtherUtils;
import com.callme.platform.util.ResourcesUtil;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：http头拦截器
 * 作者：huangyong
 * 创建时间：2018/8/22
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class HeaderInterceptor implements Interceptor {
    private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    private static final String ENCODING_GZIP = "identity";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String DEFAULT_CONTENT_TYPE = "application/json; charset=utf-8";
    private static final String USER_AGENT = "User-Agent";
    private static final String TOKEN = "token";
    private static final String NET_KEY = "c-nw"; // 网络类型 "unknow"; "wifi";"2G";"3G";"4G";
    private static final String API_KEY = "c-iv"; // 客户端请求的接口版本 2.3.3 三位数
    private static final String CLIENT_KEY = "c-cv"; // 客户端版本
    private static final String CLIENT_TYPE = "c-ct"; // 客户端类型 1:安卓，2:IOS
    private static final String SCREEN_WIDTH = "c-cw"; // 屏幕宽度
    private static final String SCREEN_HEIGHT = "c-ch"; // 屏幕高度
    private static final String SRC_KEY = "c-sr"; // 渠道   默认是0，其他渠道后续定义
    private static final String LNG_KEY = "c-lng"; // 经度
    private static final String LAT_KEY = "c-lat"; // 纬度
    private static final String BRAND_KEY = "c-br"; // 品牌名,比如华为，小米，苹果x
    private static final String MODEL_KEY = "c-mo"; // The end-user-visible name for the end product.
    private static final String SDK_VERSION_KEY = "c-sv"; // The user-visible version string.
    private static final String IMEI_KEY = "c-im"; // imei号.
    private static final String CLIENT_SUB_TYPE = "c-st"; // 客户端子类型 1:用户，2:司机 3：出租车司机

    private Context mContext;

    public HeaderInterceptor(Context context) {
        mContext = context.getApplicationContext();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request()
                .newBuilder()
                .addHeader(CONTENT_TYPE, DEFAULT_CONTENT_TYPE)
                .addHeader(HEADER_ACCEPT_ENCODING, ENCODING_GZIP)
                .addHeader(USER_AGENT, OtherUtils.getUserAgent() + "")
                .addHeader(NET_KEY, ApnUtil.getNetTypeName(mContext) + "")
                .addHeader(API_KEY, HttpHeader.API_LEVEL + "")
                .addHeader(CLIENT_TYPE, "1")
                .addHeader(CLIENT_KEY, HttpHeader.CLIENT_VERSION + "")
                .addHeader(SCREEN_WIDTH, ResourcesUtil.getScreenWidth() + "")
                .addHeader(SCREEN_HEIGHT, ResourcesUtil.getScreenHeight() + "")
                .addHeader(SRC_KEY, HttpHeader.CHANNEL_ID + "")
                .addHeader(TOKEN, HttpHeader.USER_TOKEN + "")
                .addHeader(LNG_KEY, HttpHeader.LONGITUDE + "")
                .addHeader(LAT_KEY, HttpHeader.LATITUDE + "")
                .addHeader(BRAND_KEY, Build.BRAND + "")
                .addHeader(MODEL_KEY, Build.MODEL + "")
                .addHeader(SDK_VERSION_KEY, Build.VERSION.RELEASE + "")
                .addHeader(IMEI_KEY, DevicesUtil.getIMEI(mContext) + "")
                .addHeader(CLIENT_SUB_TYPE, HttpHeader.CLIENT_SUB_TYPE)
                .build();
        return chain.proceed(request);
    }
}
