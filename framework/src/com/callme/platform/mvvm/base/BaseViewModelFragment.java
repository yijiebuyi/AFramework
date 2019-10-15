package com.callme.platform.mvvm.base;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.view.View;

import com.callme.platform.base.BaseFragment;
import com.callme.platform.mvvm.viewmodel.BaseViewModel;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

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
public abstract class BaseViewModelFragment<VM extends BaseViewModel> extends BaseFragment {

    protected VM mViewModel;

    @Override
    public void initData() {
        initViewModel();
        initEvent();
    }

    private void initViewModel() {
        Class<BaseViewModel> modelClass;
        Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            modelClass = (Class<BaseViewModel>) ((ParameterizedType) type).getActualTypeArguments()[0];
        } else {
            //如果没有指定泛型参数，则默认使用BaseViewModel
            modelClass = BaseViewModel.class;
        }
        mViewModel = (VM) asViewModel(modelClass);
    }

    private void initEvent() {
        mViewModel.getShowDialogEvent().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                showProgressDialog(aBoolean);
            }
        });
        mViewModel.getDismissDialogEvent().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(@Nullable Void aVoid) {
                closeProgressDialog();
            }
        });
        mViewModel.getFailedEvent().observe(this, new Observer<View.OnClickListener>() {
            @Override
            public void onChanged(@Nullable View.OnClickListener listener) {
                showFailedView(listener);
            }
        });
    }

    protected <T extends ViewModel> T asViewModel(Class<T> t) {
        return ViewModelProviders.of(this).get(t);
    }
}
