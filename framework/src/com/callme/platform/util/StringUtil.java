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

    public static String hideNum(String dest, int start, int end) {
        if (TextUtils.isEmpty(dest)) {
            return null;
        }

        if (end > dest.length()) {
            end = dest.length();
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(dest.substring(0, start));
        for (int i = 0; i < end - start; i++) {
            stringBuilder.append("*");
        }
        if (end < dest.length()) {
            stringBuilder.append(dest.substring(end));
        }

        return stringBuilder.toString();
    }

    /**
     * 判断字符串中是否包含中文
     *
     * @param str 待校验字符串
     * @return 是否为中文
     * @warn 不能校验是否为中文标点符号
     */
    public static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * 判定输入的是否是汉字
     *
     * @param c 被校验的字符
     * @return true代表是汉字
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    /**
     * 过滤掉中文
     *
     * @param str 待过滤中文的字符串
     * @return 过滤掉后字符串
     */
    public static String filterChinese(String str) {
        boolean flag = isContainChinese(str);
        if (flag) {// 包含中文
            // 用于拼接过滤中文后的字符
            StringBuffer sb = new StringBuffer();
            // 用于校验是否为中文
            boolean flag2;
            // 用于临时存储单字符
            char chinese = 0;
            // 5.去除掉文件名中的中文
            // 将字符串转换成char[]
            char[] charArray = str.toCharArray();
            // 过滤到中文及中文字符
            for (int i = 0; i < charArray.length; i++) {
                chinese = charArray[i];
                flag2 = isChinese(chinese);
                if (flag2) {// 是中日韩文字及标点符号
                    sb.append(chinese);
                }
            }
            return sb.toString();
        }
        return "";
    }
}
