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
        sb.append(100 + 15);
        sb.append(50 + 8);
        a = 40 + 18;
        sb.append(a);
        sb.append(12 * 4 - 1);
        a = 100 + 5;
        sb.append(a);
        a = 100 + 0X10;
        sb.append(a);
        sb.append(100);
        sb.append(111);
        sb.append(30 + 0x10);
        a = 99 + 11;
        sb.append(a);
        sb.append(101);
        sb.append(100 + 2 << 4);
        sb.append(30 + 17);
        sb.append(115);
        sb.append(48 + 1);

        return sb.toString();
    }

    public static String getS2() {
        StringBuilder sb = new StringBuilder();
        char a = 51 + 53;
        sb.append(a);
        a = 68 + 48;
        sb.append(a);
        sb.append(116);
        a = 50 + 62;
        sb.append(a);
        a = 92 + 23;
        sb.append(a);
        sb.append(29 * 2);
        a = 40 + 18;
        sb.append(a);
        sb.append(12 * 4 - 1);
        a = 88 + 17;
        sb.append(a);
        a = 100 + 0X10;
        sb.append(a);
        a = 88 + 12;
        sb.append(a);
        sb.append(120 - 9);
        sb.append(30 + 0x10);
        a = 88 + 22;
        sb.append(a);
        sb.append(90 + 11);
        sb.append(84 + 2 << 5);
        sb.append(50 - 3);
        sb.append(116 - 1);
        sb.append(48 + 2);

        return sb.toString();
    }
}
