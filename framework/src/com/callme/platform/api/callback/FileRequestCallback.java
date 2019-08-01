package com.callme.platform.api.callback;

import android.os.Handler;

import com.callme.platform.util.CmRequestImpListener;
import com.callme.platform.util.FileUtil;
import com.callme.platform.util.thdpool.Future;
import com.callme.platform.util.thdpool.FutureListener;
import com.callme.platform.util.thdpool.ThreadPool;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 * <p>
 * 功能描述：文件下载回调
 * 作者：huangyong
 * 创建时间：2019/5/7
 * <p>
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class FileRequestCallback implements Callback {
    private CmRequestImpListener mListener;
    private Handler mHandler;
    private String mFilePath;

    public <T> FileRequestCallback(Handler handler, String filePath, CmRequestImpListener<T> listener) {
        mHandler = handler;
        mFilePath = filePath;
        mListener = listener;
    }

    @Override
    public void onResponse(final Call call, final Response response) {
        final boolean httpError = response == null;
        if (response == null || response.code() != 200) {
            onFailureCallback(httpError ? ErrorCode.HTTP_EX : response.code(), "文件下载失败", httpError);
            CallRequestLogHelper.onFailure(call, response);
            onLoadComplete();
            return;
        }

        ThreadPool.getInstance().submit(new ThreadPool.Job<Boolean>() {
            @Override
            public Boolean run(ThreadPool.JobContext jc) {
                return FileUtil.writeFile(mFilePath, ((ResponseBody) response.body()).byteStream());
            }
        }, new FutureListener<Boolean>() {
            @Override
            public void onFutureDone(final Future<Boolean> future) {
                if (mHandler == null || mListener == null || future == null) {
                    if (future == null) {
                        onLoadComplete();
                    }
                    return;
                }

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (future.get()) {
                                mListener.onSuccess(mFilePath);
                            } else {
                                onFailureCallback(ErrorCode.FILE_DOWNLOAD_FAIL, "文件下载失败!", httpError);
                                CallRequestLogHelper.onFailure(call, response);
                            }
                        } catch (Exception e) {
                            String msg = "文件下载失败";
                            onFailureCallback(ErrorCode.HTTP_UNSPECIFIC, msg, false);
                            CallRequestLogHelper.onFailure(call, ErrorCode.HTTP_UNSPECIFIC, e);
                        }

                        onLoadComplete();
                    }
                });
            }
        });
    }

    @Override
    public void onFailure(Call call, Throwable t) {
        onFailureCallback(ErrorCode.HTTP_EX, "文件下载失败", true);
        CallRequestLogHelper.onFailure(call, ErrorCode.HTTP_EX, t);

        onLoadComplete();
    }

    /**
     * 失败处理
     *
     * @param code
     * @param msg
     */
    private void onFailureCallback(int code, String msg, boolean httpError) {
        if (mListener != null) {
            mListener.onFailure(code, msg);
        }
    }

    /**
     * 加载完成
     */
    private void onLoadComplete() {
        if (mListener != null) {
            mListener.onLoadComplete();
        }
    }
}
