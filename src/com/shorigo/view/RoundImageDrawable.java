package com.shorigo.view;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.shorigo.utils.Utils;

/**
 * 本地图片转圆角图片
 * 
 * @author peidongxu
 * 
 */
@SuppressLint("NewApi")
public class RoundImageDrawable extends Drawable {

	private Paint mPaint;
	private RectF rectF;
	private View view;
	private int radius = 0;
	private Bitmap bitmap;

	public RoundImageDrawable(View view, Bitmap bitmap, String radius) {
		this.view = view;
		this.bitmap = bitmap;
		if (!Utils.isEmity(radius)) {
			this.radius = Integer.parseInt(radius) * 3;
		}
	}

	@Override
	public void setBounds(int left, int top, int right, int bottom) {
		super.setBounds(left, top, right, bottom);
		rectF = new RectF(left, top, right, bottom);

		if (bitmap != null) {
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			// 设置想要的大小
			int newWidth = right;
			int newHeight = bottom;
			// 计算缩放比例
			float scaleWidth = ((float) newWidth) / width;
			float scaleHeight = ((float) newHeight) / height;
			// 取得想要缩放的matrix参数
			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth, scaleHeight);
			// 得到新的图片
			// bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix,
			// true);
			BitmapShader bitmapShader = new BitmapShader(bitmap, TileMode.CLAMP, TileMode.CLAMP);
			bitmapShader.setLocalMatrix(matrix);
			mPaint = new Paint();
			mPaint.setAntiAlias(true);
			mPaint.setShader(bitmapShader);
			bitmap = null;
		}
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawRoundRect(rectF, radius, radius, mPaint);
	}

	@Override
	public int getIntrinsicWidth() {
		return view.getWidth();
	}

	@Override
	public int getIntrinsicHeight() {
		return view.getHeight();
	}

	@Override
	public void setAlpha(int alpha) {
		mPaint.setAlpha(alpha);
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		mPaint.setColorFilter(cf);
	}

	@Override
	public int getOpacity() {
		return PixelFormat.TRANSLUCENT;
	}

}
