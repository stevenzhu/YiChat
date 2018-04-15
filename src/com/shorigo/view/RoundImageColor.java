package com.shorigo.view;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.shorigo.utils.Utils;

/**
 * 颜色转bitmap 并设置角度
 * 
 * @author peidongxu
 * 
 */
@SuppressLint("NewApi")
public class RoundImageColor extends Drawable {

	private Paint mPaint;
	private RectF rectF;
	private View view;
	private int radius = 0;

	public RoundImageColor(View view, String color, String radius) {
		this.view = view;
		if (!Utils.isEmity(radius)) {
			this.radius = Integer.parseInt(radius) * 3;
		}
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(Color.parseColor(color));
	}

	@Override
	public void setBounds(int left, int top, int right, int bottom) {
		super.setBounds(left, top, right, bottom);
		rectF = new RectF(left, top, right, bottom);
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
