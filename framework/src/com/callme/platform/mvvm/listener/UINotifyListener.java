package com.callme.platform.mvvm.listener;

import android.app.Application;
import android.view.View;

/**
 * Copyright (C) 2018 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：
 * <p>
 * <p>
 * 作者：zhanghui 2019/8/23
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public interface UINotifyListener {
    /**
     * 显示loading
     *
     * @param cancelable back键是否可以关闭
     */
    void notifyShowDialog(boolean cancelable);

    /**
     * 关闭loading
     */
    void notifyCloseDialog();

    /**
     * 显示错误页
     */
    void notifyFailed();

}
