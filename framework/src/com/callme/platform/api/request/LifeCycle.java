package com.callme.platform.api.request;

/**
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：
 * 作者：huangyong
 * 创建时间：2018/8/29
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public interface LifeCycle {
    void addRequestLifecycle(RequestLifecycle lifecycle);

    void removeRequestLifecycle(RequestLifecycle lifecycle);
}
