package com.callme.platform.api.callback;

import java.io.Serializable;
import java.util.List;

/**
 * Desc:
 * Created by zhanghui on 2017/11/16.
 * Modify by huangyong 2019/7/16
 */

public class ResultBean<T> implements Serializable {
    public int code = RequestCallback.CODE_UNSPECIFIED;//异常码
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
    public int error = RequestCallback.CODE_UNSPECIFIED;
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
