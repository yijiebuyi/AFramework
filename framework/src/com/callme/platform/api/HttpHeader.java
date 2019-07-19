package com.callme.platform.api;

/**
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：
 * 作者：huangyong
 * 创建时间：2018/8/22
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class HttpHeader {
    public static String USER_TOKEN = ""; // 用户token
    public static String API_LEVEL = ""; // 服务端接口版本
    public static String CLIENT_VERSION = ""; // 客户端版本
    public static String CHANNEL_ID = "0";
    /**
     * 客户端子类型 1:用户，2:司机 3：出租车司机
     */
    public static String CLIENT_SUB_TYPE = "";
    public static double LATITUDE = 0; //维度
    public static double LONGITUDE = 0; //精度
    public static String COOKIE_VALUE;
}
