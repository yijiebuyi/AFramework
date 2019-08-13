package com.callme.platform.api.callback;

import android.text.TextUtils;

import com.callme.platform.api.listenter.RequestListener;
import com.callme.platform.util.JsonUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import retrofit2.Response;

/**
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：http响应解析工具类
 * 作者：huangyong
 * 创建时间：2019/7/24
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ParseUtil {
    /**
     * 获取错误码(版本间的兼容)
     * 3.0之前的接口，使用error字段
     * 3.0只有的接口，使用code字段
     *
     * @param response
     * @return
     */
    public static int getErrorCode(Response response) {
        Object body = null;
        if (response == null || (body = response.body()) == null) {
            return ErrorCode.HTTP_UNKNOWN;
        }

        if (body instanceof ResultBean) {
            int error = ((ResultBean) body).error;
            if (error != ErrorCode.HTTP_UNSPECIFIC) {
                return error;
            } else if (TextUtils.equals("0", ((ResultBean) body).result)) {
                return 0;
            }

            return ((ResultBean) body).code;
        }

        return ErrorCode.HTTP_UNKNOWN;
    }

    /**
     * 获取错误码(版本间的兼容)
     * 3.0以前认证接口，使用result字段
     * 其他使用message
     *
     * @param response
     * @return
     */
    public static String getErrorMsg(Response response) {
        Object body = null;
        if (response == null || (body = response.body()) == null) {
            return "";
        }

        if (body instanceof ResultBean) {
            String result = ((ResultBean) body).result;
            if (!TextUtils.isEmpty(result)) {
                return result;
            } else {
                return ((ResultBean) body).message;
            }
        }

        return "";
    }

    /**
     * 解析data数据
     * 数据类型：监听器所带的泛型类型
     *
     * @param data
     * @return
     */
    public static Object parseData(Object data, RequestListener listener) {
        if (data == null || TextUtils.isEmpty(data.toString())) {
            return null;
        }

        Type type = null;
        try {
            Type[] dataTypes = listener != null ? ((ParameterizedType) listener.getClass()
                    .getGenericSuperclass()).getActualTypeArguments() : null;
            type = dataTypes != null && dataTypes.length > 0 ? dataTypes[0] : null;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (JsonUtils.isCanParsableJsonObject(type, data)) {
            data = JsonUtils.parseObject(data.toString(), type);
        }

        return data;
    }

    /**
     * 解析ResultBean
     * {@link com.callme.platform.net.callback.ResultBean}
     *
     * @param response
     * @return
     */
    public static ResultBean parseResultBean(Response response, RequestListener listener) {
        ResultBean result = null;
        Object body = response.body();
        if (body instanceof ResultBean) {
            result = (ResultBean) body;
            result.data = parseData(result.data, listener);
        } else {
            result = JsonUtils.parseObject(body.toString(), ResultBean.class);
            result.data = parseData(result.data, listener);
        }

        return result;
    }
}
