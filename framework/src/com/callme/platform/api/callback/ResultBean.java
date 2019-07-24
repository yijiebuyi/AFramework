package com.callme.platform.api.callback;

import java.io.Serializable;
import java.util.List;

/**
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：
 * 作者：zhanghui
 * 创建时间：2017/11/16
 * <p>
 * 修改人： huangyong
 * 修改描述：兼容3.0前的数据结构
 * 修改日期 2019/7/16
 */
public class ResultBean<T> implements Serializable {
    public int code = ErrorCode.HTTP_UNSPECIFIC;//异常码
    public String message;//一般用于错误提示
    public T data;
    public String sid;
    public boolean success;//接口是否成功
    public int total;//分页，总共多少条
    public long ts; //服务器当前时间

    /**
     * 兼容3.0以前HttpResponseBean & BaseResponseBean & PageBean
     */
    // 0：成功
    // 1：异常
    // -10：提示升级
    public int error = ErrorCode.HTTP_UNSPECIFIC;
    public String result;
    public String token;
    public T detail;
    public List<T> rows;
    public int orderCount;
    public int notReadCount;
    //接受数据为json格式，此格式不会加密响应数据，但会带有签名，客户端核对签名来判断是否被中间人攻击
    public String Sign;
    public String node;
    public String BankName;
    public double amount;
    public long BankNo;
    public int page;
    public int pageSize;
    public Object extraData;
    public long payInfoId;
}
