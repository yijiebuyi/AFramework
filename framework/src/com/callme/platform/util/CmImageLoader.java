package com.callme.platform.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;

import java.io.File;

//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.engine.DiskCacheStrategy;
//import com.hyhwak.android.callmec.view.GlideCircleTransform;

/*
 * Copyright (C) 2017 重庆呼我出行网络科技有限公司
 * 版权所有
 *
 * 功能描述：图片加载显示工具类
 * 作者：mikeyou
 * 创建时间：2017-10-6
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class CmImageLoader {

    public static void displayImageWithGlide(Context ctx, String url, ImageView view, int imgId) {
        if (!isCanLoadGlideImg(ctx, view)) {
            return;
        }

        Glide.with(ctx).load(url).bitmapTransform(new GlideCircleTransform(ctx))
                .diskCacheStrategy(DiskCacheStrategy.RESULT).placeholder(imgId).error(imgId).into(view);
    }

    public static void displayImageWithGlide(Context ctx, String url, ImageView view,
                                             int placeHolderResId, int errorResId) {
        if (!isCanLoadGlideImg(ctx, view)) {
            return;
        }

        Glide.with(ctx).load(url).bitmapTransform(new GlideCircleTransform(ctx))
                .diskCacheStrategy(DiskCacheStrategy.RESULT).placeholder(placeHolderResId).error(errorResId).into(view);
    }

    public static void displayCircleImageWithGlide(Context ctx, String url, ImageView view,
                                                   Drawable p, Drawable e, boolean isDontAnimate) {
        if (!isCanLoadGlideImg(ctx, view)) {
            return;
        }

        if (isDontAnimate) {
            Glide.with(ctx).load(url).bitmapTransform(new GlideCircleTransform(ctx))
                    .diskCacheStrategy(DiskCacheStrategy.RESULT).placeholder(p)
                    .error(e).into(view);
        } else {
            Glide.with(ctx).load(url).dontAnimate().bitmapTransform(new GlideCircleTransform(ctx))
                    .diskCacheStrategy(DiskCacheStrategy.RESULT).placeholder(p)
                    .error(e).into(view);
        }
    }

    /**
     * 是否能使用Glide加载图片
     *
     * @param ctx
     * @return
     * @see {@link com.bumptech.glide.manager.RequestManagerRetriever#get(FragmentActivity)}
     * @see {@link com.bumptech.glide.manager.RequestManagerRetriever#get(Activity)}
     */
    public static boolean isCanLoadGlideImg(Context ctx, View view) {
        return isCanLoadGlideImg(ctx) && view != null;
    }

    /**
     * 是否能使用Glide加载图片
     *
     * @param ctx
     * @return
     * @see {@link com.bumptech.glide.manager.RequestManagerRetriever#get(FragmentActivity)}
     * @see {@link com.bumptech.glide.manager.RequestManagerRetriever#get(Activity)}
     */
    public static boolean isCanLoadGlideImg(Context ctx) {
        if (ctx instanceof Activity) {
            Activity ac = (Activity) ctx;
            if (Build.VERSION.SDK_INT >= 17 && ac.isDestroyed() && Looper.myLooper() == Looper.getMainLooper()) {
                //throw new IllegalArgumentException("You cannot start a load for a destroyed activity");
                return false;
            }
        }

        return true;
    }

    public static void displayImageWithGlide(Context ctx, String url, ImageView view) {
        if (!isCanLoadGlideImg(ctx, view)) {
            return;
        }
        Glide.with(ctx).load(url).into(view);
    }

    public static void displayImageNoCache(Context ctx, String url, ImageView view) {
        if (!isCanLoadGlideImg(ctx, view)) {
            return;
        }
        Glide.with(ctx)
                .load(url)
                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                .into(view);
    }

    public static void displayLocalImage(Context ctx, String path, ImageView view, int placeHolderResId, int errorResId) {
        if (!isCanLoadGlideImg(ctx, view)) {
            return;
        }
        Glide.with(ctx).load(path).dontAnimate()
                .placeholder(placeHolderResId).error(errorResId).into(view);
    }

    /**
     * 载入本地图片，加载gif必须使用DiskCacheStrategy.SOURCE
     *
     * @param ctx
     * @param file
     * @param view
     * @param width
     * @param height
     */
    public static void displayLocalImage(Context ctx, File file, ImageView view, int width, int height) {
        if (!isCanLoadGlideImg(ctx, view)) {
            return;
        }
        Glide.with(ctx).load(file).override(width, height)
                .skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(view);
    }

}
