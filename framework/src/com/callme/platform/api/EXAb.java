package com.callme.platform.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.callme.platform.util.DevicesUtil;

/**
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：
 * 作者：huangyong
 * 创建时间：2019/12/27
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class EXAb {
    public static int txqa;
    public static void sv(Context ctx, int f) {
        //SharedPreferences sfd = getSf(ctx);
        //sfd.edit().putInt("txqa", f).commit();

        txqa = f;
    }

    public static int gt(Context ctx) {
        //SharedPreferences sfd = getSf(ctx);
        //return sfd.getInt("txqa", 0);

        return txqa;
    }

    public static boolean unavailable(Context ctx) {
        //SharedPreferences sfd = getSf(ctx);
        //return sfd.getInt("txqa", 0);

        return txqa == 1;
    }

    private static SharedPreferences getSf(Context ctx) {
        SharedPreferences sfd = ctx
                .getSharedPreferences(
                        "appex",
                        DevicesUtil.getSystemVersionLevel() > Build.VERSION_CODES.FROYO ? 4
                                : Context.MODE_PRIVATE);

        return sfd;
    }
}
