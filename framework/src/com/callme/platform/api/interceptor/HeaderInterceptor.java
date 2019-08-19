package com.callme.platform.api.interceptor;

import android.text.TextUtils;

import com.callme.platform.api.HttpConfigure;
import com.callme.platform.api.HttpHeader;
import com.callme.platform.util.ApnUtil;
import com.callme.platform.util.DevicesUtil;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：http Header拦截器
 * 常量引用于{@link com.callme.platform.api.HttpHeader}
 * 作者：huangyong
 * 创建时间：2018/8/22
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class HeaderInterceptor implements Interceptor {
    /**
     * keys
     */
    private static final String KEY_ACCEPT_ENCODING = "Accept-Encoding";
    private static final String KEY_CONTENT_TYPE = "Content-Type";
    private static final String KEY_USER_AGENT = "User-Agent";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_NET = "c-nw"; // 网络类型 "unknow"; "wifi";"2G";"3G";"4G";
    private static final String KEY_API_LEVEL = "c-iv"; // 客户端请求的接口版本 2.3.3 三位数
    private static final String KEY_CLIENT = "c-cv"; // 客户端版本
    private static final String KEY_CLIENT_TYPE = "c-ct"; // 客户端类型 1:安卓，2:IOS
    private static final String KEY_SCREEN_WIDTH = "c-cw"; // 屏幕宽度
    private static final String KEY_SCREEN_HEIGHT = "c-ch"; // 屏幕高度
    private static final String KEY_CHANNEL = "c-sr"; // 渠道   默认是0，其他渠道后续定义
    private static final String KEY_LNG = "c-lng"; // 经度
    private static final String KTY_LAT = "c-lat"; // 纬度
    private static final String KEY_BRAND = "c-br"; // 品牌名,比如华为，小米，苹果x
    private static final String KEY_MODEL = "c-mo"; // The end-user-visible name for the end product.
    private static final String KEY_SDK_VERSION = "c-sv"; // The user-visible version string.
    private static final String KEY_IMEI = "c-im"; // imei号.
    private static final String KEY_CLIENT_SUB_TYPE = "c-st"; // 客户端子类型 1:用户，2:司机 3：出租车司机

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();

        //赋值静态常量
        builder.addHeader(KEY_CONTENT_TYPE, HttpHeader.DEFAULT_CONTENT_TYPE)
                .addHeader(KEY_ACCEPT_ENCODING, HttpHeader.ENCODING_GZIP)
                .addHeader(KEY_USER_AGENT, HttpHeader.USER_AGENT)
                .addHeader(KEY_BRAND, HttpHeader.DEVICE_BRAND)
                .addHeader(KEY_MODEL, HttpHeader.DEVICE_MODEL)
                .addHeader(KEY_CLIENT_TYPE, HttpHeader.CLIENT_TYPE)
                .addHeader(KEY_SDK_VERSION, HttpHeader.VERSION_RELEASE)
                .addHeader(KEY_API_LEVEL, HttpHeader.API_VERSION)
                .addHeader(KEY_CLIENT, HttpHeader.CLIENT_VERSION)
                .addHeader(KEY_CHANNEL, HttpHeader.CHANNEL_ID)
                .addHeader(KEY_CLIENT_SUB_TYPE, HttpHeader.CLIENT_SUB_TYPE)
                .addHeader(KEY_SCREEN_WIDTH, HttpHeader.SCREEN_WIDTH + "")
                .addHeader(KEY_SCREEN_HEIGHT, HttpHeader.SCREEN_HEIGHT + "");

        //赋值静态可变量
        builder.addHeader(KEY_TOKEN, HttpHeader.getToken())
                .addHeader(KEY_LNG, HttpHeader.LONGITUDE + "")
                .addHeader(KTY_LAT, HttpHeader.LATITUDE + "")
                .addHeader(KEY_NET, getNetType())
                .addHeader(KEY_IMEI, getIMEI());

        Request request = builder.build();
        return chain.proceed(request);
    }

    /**
     * 获取当前的网络类型
     *
     * @return
     */
    private String getNetType() {
        return ApnUtil.getNetTypeName(HttpConfigure.getAppContext());
    }

    /**
     * 获取设备IMEI号
     *
     * @return
     */
    private String getIMEI() {
        String imei = HttpHeader.IMEI;
        if (TextUtils.isEmpty(imei)) {
            imei = DevicesUtil.getIMEI(HttpConfigure.getAppContext());
            HttpHeader.IMEI = imei;
        }

        return imei;
    }
}
