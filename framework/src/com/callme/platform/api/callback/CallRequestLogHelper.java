package com.callme.platform.api.callback;

import android.text.TextUtils;
import android.util.Log;

import com.callme.platform.BuildConfig;
import com.callme.platform.util.IOUtils;
import com.callme.platform.util.LogCacheUtil;
import com.callme.platform.util.StackTraceUtil;
import com.callme.platform.util.TimeUtil;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import java.io.EOFException;
import java.lang.reflect.Field;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：http请求，日志辅助类
 * 作者：huangyong
 * 创建时间：2019/5/9
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class CallRequestLogHelper {
    private final static Charset UTF8 = Charset.forName("UTF-8");

    /**
     * 接口错误时处理日志
     *
     * @param call
     * @see #loggerOnFailure(Call, int, String, StackTraceElement[], boolean)
     */
    public static void onFailure(Call call, Response response) {
        if (response == null) {
            loggerOnFailure(call, ErrorCode.HTTP_EX, "未知错误", null, false);
            return;
        }

        String msg = null;
        StackTraceElement[] exStack = null;
        int code = ErrorCode.HTTP_UNKNOWN;
        try {
            if (response.code() == 200) {
                code = ParseUtil.getErrorCode(response);
                msg = ParseUtil.getErrorMsg(response);
            } else {
                ResponseBody errBody = response.errorBody();
                msg = errBody != null ? errBody.string() : "";
            }
        } catch (Exception e) {
            msg = e.getLocalizedMessage();
            exStack = e.getStackTrace();
        }

        if (TextUtils.isEmpty(msg)) {
            msg = response.message();
        }
        if (TextUtils.isEmpty(msg)) {
            msg = "未知错误";
        }

        loggerOnFailure(call, code, msg, exStack, false);
    }

    /**
     * 接口错误时处理日志
     *
     * @param call
     * @param code
     * @see #loggerOnFailure(Call, int, String, StackTraceElement[], boolean)
     */
    public static void onFailure(Call call, int code, Throwable t) {
        boolean connectEx = t instanceof ConnectException || t instanceof SocketTimeoutException;
        loggerOnFailure(call, code, t.getMessage(), t.getStackTrace(), connectEx);
    }

    /**
     * 接口错误时处理日志
     *
     * @param call
     * @param code
     * @param msg
     */
    private static void loggerOnFailure(Call call, int code, String msg,
                                        StackTraceElement[] exStackTrace,
                                        boolean connectEx) {
        try {
            Request request = call != null ? call.request() : null;
            if (request == null) {
                request = getRequestByReflect(call);
            }

            StringBuilder sb = new StringBuilder();
            String errorMsg = "--> code: " + code + ",  msg: " + msg;
            if (request != null) {
                String requestInfo = "==> " + request.method() + " " + request.url() + " \n";
                String requestBody = readContent(request);
                if (requestBody == null) {
                    requestBody = getContent(request);
                }
                requestBody = "--> requestBody: " + requestBody + " \n";
                sb.append(requestInfo);
                sb.append(requestBody);
            }
            sb.append(errorMsg);
            sb.append("\n");
            sb.append("--> connectEx:" + connectEx);
            handleLog(sb, exStackTrace, connectEx);
            sb = null;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("CallRequestHelper", "" + e.getMessage());
        }
    }

    /**
     * 处理日志
     *
     * @param sb
     */
    private final static void handleLog(StringBuilder sb, StackTraceElement[] exStackTrace,
                                        boolean connectEx) {
        StackTraceElement[] callSackTrace = Thread.currentThread().getStackTrace();
        String lineInfo = StackTraceUtil.getStackMsg(callSackTrace, CallRequestLogHelper.class,
                1, 0, exStackTrace);

        long millis = System.currentTimeMillis();
        String time =
                TimeUtil.formatDate(millis, TimeUtil.TIME_YYYY_MM_DD_HH_MM_SS) + ", ts:" + millis + "\n";
        sb.insert(0, lineInfo);
        sb.insert(0, time);

        if (BuildConfig.DEBUG) {
            Logger.clearLogAdapters();
            Logger.addLogAdapter(new AndroidLogAdapter());
            Logger.w(sb.toString());
        }

        sb.insert(0, "\n");
        LogCacheUtil.getInstance().updateMsg(null, !BuildConfig.DEBUG, true,
                LogCacheUtil.LABEL_API, sb.toString());
        sb = null;
    }

    /**
     * 获取请求(反射)
     *
     * @param call
     * @return
     */
    private static Request getRequestByReflect(Call call) {
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
                    return getRequestByReflect(delegateCall);
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
                    Charset charset = UTF8;
                    MediaType contentType = requestBody.contentType();
                    if (contentType != null) {
                        charset = contentType.charset(UTF8);
                    }

                    if (isMultipart(contentType)) {
                        return "upload file content ...";
                    }

                    byte[] bytes = (byte[]) field.get(requestBody);
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
            Charset charset = UTF8;
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF8);
            }

            if (isMultipart(contentType)) {
                return "upload file content ...";
            }

            buffer = new Buffer();
            requestBody.writeTo(buffer);
            if (isPlaintext(buffer)) {
                String content = buffer.readString(charset);
                return content;
            }
        } catch (Exception e) {

        } finally {
            if (buffer != null) {
                buffer.clear();
            }
            IOUtils.closeQuietly(buffer);
        }

        return null;
    }

    /**
     * 是否是文件上传
     *
     * @param contentType
     * @return
     */
    private static boolean isMultipart(MediaType contentType) {
        String contentTypeStr = contentType != null ? contentType.toString() : "";
        if (!TextUtils.isEmpty(contentTypeStr)) {
            if (contentTypeStr.toLowerCase().contains("multipart/form-data")) {
                return true;
            }
        }

        return false;
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
