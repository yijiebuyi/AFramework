package com.callme.platform.api.callback;

import android.text.TextUtils;

import com.callme.platform.util.IOUtils;

import java.io.EOFException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;
import retrofit2.Call;

/**
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：
 * 作者：huangyong
 * 创建时间：2019/5/9
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class CallRequestHelper {
    private final static Charset UTF8 = Charset.forName("UTF-8");

    /**
     * 获取请求(反射)
     *
     * @param call
     * @return
     */
    public static Request getRequest(Call call) {
        if (call == null) {
            return null;
        }

        Field[] fields = call.getClass().getDeclaredFields();
        if (fields == null || fields.length == 0) {
            return null;
        }

        try {
            for (Field field : fields) {
                field.setAccessible(true);
                String name = field.getName();
                if (TextUtils.equals("delegate", name)) {
                    Call delegateCall = (Call) field.get(call);
                    return getRequest(delegateCall);
                } else if (TextUtils.equals("rawCall", name)) {
                    okhttp3.Call rawCall = (okhttp3.Call) field.get(call);
                    return rawCall.request();
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取请求参数(反射)
     *
     * @param request
     * @return
     */
    private static String getContent(Request request) {
        RequestBody requestBody = null;
        if (request == null || (requestBody = request.body()) == null) {
            return "";
        }

        Field[] fields = requestBody.getClass().getDeclaredFields();
        if (fields == null || fields.length == 0) {
            return null;
        }

        try {
            for (Field field : fields) {
                field.setAccessible(true);
                String name = field.getName();
                if (TextUtils.equals("val$content", name)) {
                    byte[] bytes = (byte[]) field.get(requestBody);

                    Charset charset = UTF8;
                    MediaType contentType = requestBody.contentType();
                    if (contentType != null) {
                        charset = contentType.charset(UTF8);
                    }
                    return new String(bytes, charset);
                }
            }
        } catch (IllegalAccessException e) {

        }

        return null;
    }

    /**
     * 从流中读取请求参数
     *
     * @param request
     * @return
     */
    private static String readContent(Request request) {
        RequestBody requestBody = null;
        if (request == null || (requestBody = request.body()) == null) {
            return null;
        }

        Buffer buffer = null;
        try {
            buffer = new Buffer();
            requestBody.writeTo(buffer);

            Charset charset = UTF8;
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF8);
            }

            if (isPlaintext(buffer)) {
                String content = buffer.readString(charset);
                return content;
            }
        } catch (Exception e) {

        } finally {
            buffer.clear();
            IOUtils.closeQuietly(buffer);
        }

        return null;
    }

    /***
     *
     * @param buffer
     * @return
     */
    private static boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }
}
