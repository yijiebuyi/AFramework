package com.callme.platform.util;

/*
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 *
 * 功能描述：Glide加载图片
 * 作者：huangyong
 * 创建时间：2017/12/1
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.StringSignature;

public class ExpandGlide {
    //    是否检查已加载，若已加载则不再加在
    private boolean mIsLoaded;
    private int mPlaceHoldResId;
    private int mErrorResId;
    private Context mContext;
    private Uri mUri;
    private boolean mCenterCrop = false;
    private AsyncTask mAsyncTask;

    public static synchronized ExpandGlide getInstance() {
        return new ExpandGlide();
    }

    public ExpandGlide with(Context context) {
        mContext = context;
        return this;
    }

    public ExpandGlide load(Uri uri) {
        mUri = uri;
        return this;
    }

    public ExpandGlide load(String uri) {
        try {
            mUri = Uri.parse(uri);
        } catch (Exception e) {
        }
        return this;
    }

    public void into(final ImageView imageView) {
        into(imageView, null);
    }

    public synchronized void into(final ImageView imageView, final OnBitmapLoaded bitmapLoaded) {
        if (mUri == null) {
            imageView.setImageResource(mPlaceHoldResId > 0 ? mPlaceHoldResId : mErrorResId);
            return;
        }
        if (mIsLoaded && imageView.getTag() != null) {
            if (TextUtils.equals(mUri.getPath(), imageView.getTag().toString())) {
                return;
            }
        }
        //load img
        mAsyncTask = new AsyncTask<Void, Integer, Bitmap>() {

            @Override
            protected void onPreExecute() {
                imageView.setImageResource(mPlaceHoldResId);
            }

            @Override
            protected Bitmap doInBackground(Void... params) {
                try {
                    return Glide.with(mContext)
                            .load(mUri)
                            .asBitmap()
                            .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                            .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                            .get();
                } catch (Exception e) {
                    String errorMsg = e != null ? e.getLocalizedMessage() : "null";
                    if (TextUtils.isEmpty(errorMsg)) {
                        errorMsg = "null";
                    }
                }

                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                try {
                    if (bitmap != null) {
                        if (mCenterCrop) {
                            bitmap = BitmapUtils.circleCrop(bitmap, true);
                        }
                        imageView.setImageBitmap(bitmap);
                        if (mIsLoaded) {
                            imageView.setTag(mUri.getPath());
                        }
                    } else {
                        imageView.setImageResource(mErrorResId);
                        if (mIsLoaded) {
                            imageView.setTag(null);
                        }
                    }

                    if (bitmapLoaded != null) {
                        bitmapLoaded.onLoaded(bitmap);
                    }
                } catch (Exception e) {
                    String errorMsg = e != null ? e.getLocalizedMessage() : "null";
                    if (TextUtils.isEmpty(errorMsg)) {
                        errorMsg = "null";
                    }
                }
            }
        }.execute();
    }

    public ExpandGlide isCheckLoaded(boolean isLoaded) {
        mIsLoaded = isLoaded;
        return this;
    }

    public ExpandGlide error(int resId) {
        mErrorResId = resId;
        return this;
    }


    public ExpandGlide placeHolder(int resId) {
        mPlaceHoldResId = resId;
        return this;
    }

    public ExpandGlide centerCrop() {
        mCenterCrop = true;
        return this;
    }

    public synchronized void cancel() {
        if (mAsyncTask != null) {
            mAsyncTask.cancel(true);
        }
    }

    public interface OnBitmapLoaded {
        void onLoaded(Bitmap bmp);
    }
}
