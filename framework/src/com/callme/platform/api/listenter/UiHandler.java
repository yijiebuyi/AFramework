package com.callme.platform.api.listenter;

import android.content.Context;
import android.view.View;

/**
 * Copyright (C) 2018 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：
 * <p>
 * <p>
 * 作者：zhanghui 2019/8/28
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public interface UiHandler {
    void showProgressDialog(boolean cancelable);

    void dismissProgressDialog();

    void showFailedView(View.OnClickListener listener);

    Context getApplicationContext();
}
