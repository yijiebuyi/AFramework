package com.callme.platform.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * http://www.sjsjw.com/100/007044MYM000708/
 * <p>
 * 裁剪圆形图片
 *
 * @author zyl
 * @date 2016年6月15日10:34:52
 */
public class GlideCircleTransform extends BitmapTransformation {

    public GlideCircleTransform(Context context) {
        super(context);
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        return circleCrop(pool, toTransform);
    }

    public static Bitmap circleCrop(BitmapPool pool, Bitmap source) {
        if (source == null)
            return null;

        int size = Math.min(source.getWidth(), source.getHeight());
        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);
        Bitmap result = null;

        if (pool != null) {
            result = pool.get(size, size, Bitmap.Config.ARGB_8888);
        }

        if (result == null) {
            result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);
        float r = size / 2f;
        canvas.drawCircle(r, r, r, paint);
        return result;
    }

    public static Drawable circleCrop(Context context, Drawable drawable) {
        if (context == null || drawable == null)
            return null;

        return new BitmapDrawable(context.getResources(), circleCrop(null,
                ((BitmapDrawable) drawable).getBitmap()));
    }

    @Override
    public String getId() {
        return getClass().getName();
    }

}
