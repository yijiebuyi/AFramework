package com.callme.platform.util.permission;

import java.util.List;

/*
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 *
 * 功能描述：权限接口
 * 作者：zyl
 * 创建时间：on 2018/6/6.
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public interface OnPermissionListener {
    /**
     * 允许
     */
    void onGranted(List<String> data);

    /**
     * 拒绝,返回true将不会执行默认处理
     */
    boolean onDenied(List<String> data);
}
