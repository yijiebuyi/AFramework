package com.callme.platform.util.permission;

/*
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 *
 * 功能描述：dialog控件监听
 * 作者：zyl
 * 创建时间：on 2018/6/15.
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public interface OnDialogClickListener {

    /**
     * "确定"
     */
    void onPositive();

    /**
     * "取消"
     */
    void onNegative();
}
