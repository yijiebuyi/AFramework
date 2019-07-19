package com.callme.platform.util;
/*
 * Copyright (C) 2018 重庆呼我出行网络科技有限公司

 * 版权所有
 *
 * 功能描述：fastjson工具类
 *
 *
 * 作者：Created by tgl on 2018/6/1.
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.lang.reflect.Type;
import java.util.List;

public class JsonUtils {

    /**
     * 是否可解析的JSONObject
     *
     * @param type
     * @param data
     * @return
     */
    public static boolean isCanParsableJsonObject(Type type, Object data) {
        return type != null && (data instanceof JSONObject || data instanceof JSONArray);
    }

    /**
     * java Bean转为String json字符串
     *
     * @param object
     * @return
     */
    public static String toJSONString(Object object) {
        return JSON.toJSONString(object);
    }

    /**
     * java Bean转为String json字符串
     *
     * @param object     javaBean
     * @param dateFormat 需要输入的时间格式
     * @return
     */
    public static String toJSONString(Object object, String dateFormat) {
        return JSON.toJSONStringWithDateFormat(object, dateFormat, SerializerFeature.WriteMapNullValue);
    }

    /**
     * 将JavaBean转换为JSONObject或者JSONArray
     *
     * @param object
     * @return
     */
    public static Object toJSON(Object object) {
        return JSON.toJSON(object);
    }

    /**
     * 把JSON文本parse成JSONObject
     *
     * @param json
     * @return
     */
    public static JSONObject parseObject(String json) {
        try {
            return JSON.parseObject(json);
        } catch (Exception e) {
            System.out.println("json format error");
        }
        return null;
    }

    /**
     * 把JSON文本parse为JavaBean
     *
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T parseObject(String json, Class<T> clazz) {
        try {
            return JSON.parseObject(json, clazz);
        } catch (Exception e) {
            System.out.println("json format error");
        }
        return null;
    }

    /**
     * 把JSON文本parse为JavaBean
     *
     * @param json
     * @param type
     * @param <T>
     * @return
     */
    public static <T> T parseObject(String json, Type type) {
        try {
            return JSON.parseObject(json, type);
        } catch (Exception e) {
            System.out.println("json format error");
        }
        return null;
    }

    /**
     * 把JSON文本parse为JavaBean 带泛型实体
     *
     * @param json
     * @param jsonType
     * @param <T>
     * @return
     */
    public static <T> T parseObject(String json, JsonType jsonType) {
        try {
            return JSON.parseObject(json, jsonType.getType());
        } catch (Exception e) {
            System.out.println("json format error");
        }
        return null;
    }

    /**
     * 把JSON文本parse成JSONArray
     *
     * @param json
     * @retur
     */
    public static JSONArray parseArray(String json) {
        try {
            return JSON.parseArray(json);
        } catch (Exception e) {
            System.out.println("json format error");
        }
        return null;
    }

    // 转换为List
    public static <T> List<T> parseList(String json, Class<T> clazz) {
        try {
            return JSON.parseArray(json, clazz);
        } catch (Exception e) {
            System.out.println("json format error");
        }
        return null;
    }

}
