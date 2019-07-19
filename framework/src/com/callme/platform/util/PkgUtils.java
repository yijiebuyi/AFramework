package com.callme.platform.util;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Copyright (C) 2018 重庆呼我出行网络科技有限公司
 * <p>
 * 版权所有
 * <p>
 * 功能描述：apk相关信息获取
 * <p>
 * <p>
 * 作者：Created by tgl on 2019/3/13.
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */

public class PkgUtils {
    /**
     * 获得versionName，返回值为版本名称，例如：1.3.1
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }
}
