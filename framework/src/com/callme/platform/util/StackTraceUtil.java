package com.callme.platform.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：堆栈信息工具类
 * 作者：huangyong
 * 创建时间：2019/5/15
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class StackTraceUtil {
    /**
     * 调用堆栈的最小索引（除去2个native方法）
     */
    private static final int MIN_STACK_OFFSET = 2;
    /**
     * 异常堆栈的方法数
     */
    private static final int CALL_STACK_COUNT = 2;


    /**
     * 获取调用堆栈的信息
     *
     * @param callSackTrace 调用栈
     * @param excludeCls    排除的类
     * @param methodCount   调用栈的方法数
     * @param methodOffset  调用栈的方法偏移
     * @return
     * @see #getStackMsg(StackTraceElement[], Class, int, int, StackTraceElement[])
     */
    public static String getStackMsg(StackTraceElement[] callSackTrace, Class excludeCls,
                                     int methodCount, int methodOffset) {
        return getStackMsg(callSackTrace, excludeCls, methodCount, methodOffset, null);
    }

    /**
     * 获取调用堆栈和异常堆栈的信息
     *
     * @param callSackTrace 调用栈
     * @param excludeCls    排除的类
     * @param methodCount   调用栈的方法数
     * @param methodOffset  调用栈的方法偏移
     * @param exStackTrace  异常堆栈
     * @return
     */
    public static String getStackMsg(StackTraceElement[] callSackTrace, Class excludeCls,
                                     int methodCount, int methodOffset,
                                     StackTraceElement[] exStackTrace) {
        if (methodCount <= 0 || methodOffset < 0) {
            return "";
        }

        String level = "";
        StringBuilder builder = new StringBuilder();

        if (exStackTrace != null && exStackTrace.length > 0) {
            for (int i = CALL_STACK_COUNT - 1; i >= 0; i--) {
                int stackIndex = i;
                if (stackIndex >= exStackTrace.length) {
                    continue;
                }
                buildMsg(builder, exStackTrace, level, stackIndex, "ex:");
                level += "   ";
            }
        }

        int stackOffset = getStackOffset(excludeCls, callSackTrace) + methodOffset;
        if (methodCount + stackOffset > callSackTrace.length) {
            methodCount = callSackTrace.length - stackOffset - 1;
        }
        for (int i = methodCount; i > 0; i--) {
            int stackIndex = i + stackOffset;
            if (stackIndex >= callSackTrace.length) {
                continue;
            }
            buildMsg(builder, callSackTrace, level, stackIndex, "call:");
            level += "   ";
        }


        return builder.toString();
    }

    private static void buildMsg(StringBuilder builder, StackTraceElement[] stackTrace,
                                 String level, int stackIndex, String label) {
        builder.append(' ')
                .append(level)
                .append(TextUtils.isEmpty(label) ? "" : label)
                .append(getSimpleClassName(stackTrace[stackIndex].getClassName()))
                .append(".")
                .append(stackTrace[stackIndex].getMethodName())
                .append(" ")
                .append(" (")
                .append(stackTrace[stackIndex].getFileName())
                .append(":")
                .append(stackTrace[stackIndex].getLineNumber())
                .append(")")
                .append("\n");
    }

    private static int getStackOffset(Class excludeCls, @NonNull StackTraceElement[] trace) {
        for (int i = MIN_STACK_OFFSET; i < trace.length; i++) {
            StackTraceElement e = trace[i];
            String name = e.getClassName();
            if (!name.equals(excludeCls.getName())) {
                return --i;
            }
        }
        return -1;
    }

    @NonNull
    static <T> T checkNotNull(@Nullable final T obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
        return obj;
    }

    private static String getSimpleClassName(@NonNull String name) {
        checkNotNull(name);

        int lastIndex = name.lastIndexOf(".");
        return name.substring(lastIndex + 1);
    }
}
