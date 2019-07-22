package com.callme.platform.util;

import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Copyright (C)
 * 版权所有
 *
 * 功能描述：字符串相关的工具类
 * 作者：mikeyou
 * 创建时间：2017-10-6
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class StringUtil {

    /**
     * 将字节型数据转化为16进制字符串
     */
    public static String byteToHexString(byte[] bytes) {
        if (bytes == null || bytes.length <= 0)
            return null;

        StringBuffer buf = new StringBuffer(bytes.length * 2);

        for (int i = 0; i < bytes.length; i++) {
            if (((int) bytes[i] & 0xff) < 0x10) {
                buf.append("0");
            }
            buf.append(Long.toString((int) bytes[i] & 0xff, 16));
        }
        return buf.toString();
    }

    public static String inputStreamToString(InputStream in) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int count = -1;
        try {
            while ((count = in.read(buffer)) != -1) {
                out.write(buffer, 0, count);
            }
            return new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean hasContain(List<String> list, String content) {
        if (list != null && list.size() > 0) {
            for (String l : list) {
                if (!TextUtils.isEmpty(l) && l.equalsIgnoreCase(content)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Spanned getCData(int resId, Object... formatArgs) {
        return Html.fromHtml(String.format(ResourcesUtil.getString(resId, formatArgs)));
    }

    /**
     * 替换换行字符
     */
    public static String replace(String text) {
        if (!TextUtils.isEmpty(text)) {
            if (text.length() >= 50) {
                text = text.substring(0, 50);
            }
            text = text.replace("\n", "");
        }
        return text;
    }

    /**
     * 是否纯中文
     *
     * @param name
     * @return
     */
    public static boolean isMatchingChiness(String name) {
        int n = 0;
        for (int i = 0; i < name.length(); i++) {
            n = (int) name.charAt(i);
            if (!(19968 <= n && n < 40869)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 功能：判断字符串是否为数字
     *
     * @param str
     * @return
     */
    private static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (isNum.matches()) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * 是否同时包含字母和数字
     *
     * @param text
     * @return
     */
    public static boolean hasLettersAndNumbers(String text) {
        String regex = "(\\d+)";
        String regex2222 = "[a-zA-Z]+";
        Matcher m = Pattern.compile(regex).matcher(text);
        if (m.find()) {
            Matcher w = Pattern.compile(regex2222).matcher(text);
            if (w.find()) {
                return true;
            }
        }
        return false;
    }
}
