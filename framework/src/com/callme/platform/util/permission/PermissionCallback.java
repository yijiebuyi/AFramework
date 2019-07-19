package com.callme.platform.util.permission;

/*
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 *
 * 功能描述：抽象类权限申请，dialog操作等监听
 * 作者：zyl
 * 创建时间：on 2018/6/6.
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public abstract class PermissionCallback extends DialogCallback
        implements OnPermissionListener {

    @Override
    public void onPositive() {

    }

    @Override
    public void onNegative() {

    }
}
