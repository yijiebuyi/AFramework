package com.callme.platform.api.callback;

/**
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：错误码
 * <p>
 * 1.成功 0
 * 2.通用错误码1
 * 3.业务错误码(501, 10)
 * <p>
 * 作者：huangyong
 * 创建时间：2019/4/23
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ErrorCode {
    //================================LOCAL <0====================================
    /**
     * HTTP 不确定
     */
    public static final int HTTP_UNSPECIFIC = -1000000;
    /**
     * CODE 未知
     */
    public static final int HTTP_UNKNOWN = -1000001;
    /**
     * HTTP失败
     */
    public static final int HTTP_EX = -1;
    /**
     * 类型转换失败
     */
    public static final int CAST_EX = -2;
    /**
     * 下载失败
     */
    public final static int FILE_DOWNLOAD_FAIL = -3;
    /**
     * 上传失败
     */
    public final static int FILE_UPLOAD_FAIL = -4;


    //================================SUCCESS 0=================================
    /**
     * 成功
     */
    public final static int SUCCESS = 0;

    //================================FAILURE 1=================================
    /**
     * 失败
     */
    public final static int FAILURE = 1;


    //===============================ERROR xx====================================
    /**
     * 未授权
     */
    public final static int TOKEN_INVALID = 501;
    /**
     * APP需要升级
     */
    public final static int APP_UPGRADE = 10;



    //===============================OTHRE ERROR xx===============================
    /**
     * 参数无效
     */
    //public final static int PARAMS_INVALID = 400;

    /**
     * 未登录
     */
    //public final static int NOT_LOGIN = 401;

    /**
     * 未授权
     */
    //public final static int UNAUTHORIZED = 402;

    /**
     * Token无效
     */
    //public final static int TOKEN_INVALID = 403;

    /**
     * 非法访问
     */
    //public final static int ILLEGAL_ACCESS = 404;

    /**
     * 系统升级维护
     */
    //public final static int SYSTEM_UPGRADE_MAINTENANCE = 405;

    /**
     * 业务调整
     */
    //public final static int BUSINESS_ADJUSTMENT = 406;

    /**
     * APP需要升级
     */
    //public final static int APP_UPGRADE = 407;

    /**
     * 请求时间太短（太频繁）
     */
    //public final static int APP_REQUEST_FREQUENTLY = 408;

}
