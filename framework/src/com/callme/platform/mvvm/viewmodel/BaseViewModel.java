package com.callme.platform.mvvm.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;

/**
 * Copyright (C) 2018 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：ViewModel基类
 * <p>
 * <p>
 * 作者：zhanghui 2019/8/22
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class BaseViewModel extends UiViewModel {

    public BaseViewModel(@NonNull Application application) {
        super(application);
    }
}
