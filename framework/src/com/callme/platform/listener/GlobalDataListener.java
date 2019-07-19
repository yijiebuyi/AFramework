package com.callme.platform.listener;

import android.os.Bundle;

/**
 * Copyright (C) 2018 重庆呼我出行网络科技有限公司
 * <p>
 * 版权所有
 * <p>
 * 功能描述：全局数据监听器，用于处理用户信息被回收
 * <p>
 * <p>
 * 作者：Created by tgl on 2018/12/12.
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */

public interface GlobalDataListener {

    /**
     * 存储
     *
     * @param outState
     */
    public abstract void onSaveInstanceState(Bundle outState);

    /**
     * 恢复
     *
     * @param savedInstanceState
     */
    public abstract void onRestoreInstanceState(Bundle savedInstanceState);
}
