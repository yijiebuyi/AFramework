package com.callme.platform.mvvm.model;

import java.io.Serializable;

/**
 * Copyright (C) 2018 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：
 * <p>
 * <p>
 * 作者：zhanghui 2019/8/6
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ResultModel<T> implements Serializable {
    //接口返回的消息
    public String message;
    //接口错误码
    public int error;
    //接口是否成功
    public boolean success;
    //接口返回的数据
    public T data;

    public static <T> ResultModel<T> create() {
        ResultModel<T> result = new ResultModel();
        result.setSuccess(false);
        return result;
    }

    public ResultModel<T> setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public ResultModel<T> success() {
        setSuccess(true);
        return this;
    }

    public ResultModel<T> success(T data) {
        setSuccess(true);
        this.data = data;
        return this;
    }

    public ResultModel<T> fail() {
        setSuccess(false);
        return this;
    }

    public ResultModel<T> fail(String message) {
        setSuccess(false);
        this.message = message;
        return this;
    }

    public ResultModel<T> fail(int error, String message) {
        setSuccess(false);
        this.error = error;
        this.message = message;
        return this;
    }

    public ResultModel<T> fail(int error) {
        setSuccess(false);
        this.error = error;
        return this;
    }

    public ResultModel<T> data(T data) {
        this.data = data;
        return this;
    }

    public ResultModel<T> message(String message) {
        this.message = message;
        return this;
    }
}
