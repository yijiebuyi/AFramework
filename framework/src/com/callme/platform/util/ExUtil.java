package com.callme.platform.util;

/**
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：
 * 创建时间：
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ExUtil {
    public static String getS1() {
        StringBuilder sb = new StringBuilder();
        char a = 50 + 54;
        sb.append(a);
        a = 60 + 56;
        sb.append(a);
        a = 40 + 76;
        sb.append(a);
        a = 50 + 62;
        sb.append(a);
        sb.append((char) (100 + 15));
        sb.append((char) (50 + 8));
        a = 40 + 7;
        sb.append(a);
        sb.append((char) (12 * 4 - 1));
        a = 100 + 5;
        sb.append(a);
        a = 100 + 0X10;
        sb.append(a);
        sb.append((char) 100);
        sb.append((char) (80 + 17));
        sb.append((char) (99 + 12));
        sb.append((char) (30 + 0x10));
        a = 99 + 11;
        sb.append(a);
        sb.append((char) (108 - 7));
        sb.append((char) (100 + 4 * 4));
        sb.append((char) (30 + 17));
        sb.append((char) (115));
        sb.append((char) (48 + 1));

        return sb.toString();
    }

    public static String getS2() {
        StringBuilder sb = new StringBuilder();
        char a = 60 + 44;
        sb.append(a);
        a = 55 + 61;
        sb.append(a);
        a = 45 + 71;
        sb.append(a);
        a = 52 + 60;
        sb.append(a);
        sb.append((char) (25 * 4 + 13 + 2));
        sb.append((char) (26 + 26 + 6));
        a = 80 / 2 + 7;
        sb.append(a);
        sb.append((char) (12 * 4 - 9 + 8));
        a = 88 + 17;
        sb.append(a);
        a = 85 + 0X10 + 15;
        sb.append(a);
        sb.append((char) (25 * 4));
        sb.append((char) (60 + 10 + 27));
        sb.append((char) (100 + 11));
        sb.append((char) (12 * 4 - 2));
        a = 84 + 26;
        sb.append(a);
        sb.append((char) (27 + 74));
        sb.append((char) (96 + 2 * 10));
        sb.append((char) (27 + 40 / 2));
        sb.append((char) (118 - 3));
        sb.append((char) (92 - 42));

        return sb.toString();
    }
}
