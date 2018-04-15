package com.shorigo.utils;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class BitmapHelp {

	/**
	 * 加载本地图片
	 * 
	 * @param context
	 * @param imageView
	 *            图片控件
	 * @param res_id
	 *            本地资源图片
	 */
	public static void loadImg(final Context context, ImageView imageView, int res_id) {
		if (context == null || imageView == null) {
			return;
		}
		Glide.with(context).load(res_id).centerCrop().into(imageView);
	}

	/**
	 * 加载本地图片-圆形图片
	 * 
	 * @param context
	 * @param view
	 *            图片控件
	 * @param res_id
	 *            本地资源图片
	 * @param widget_id_name
	 *            控件ID名称
	 */
	public static void loadCircleImg(Context context, ImageView imageView, int res_id) {
		if (context == null || imageView == null) {
			return;
		}
		Glide.with(context).load(res_id).centerCrop().transform(new GlideCircleTransform(context)).into(imageView);
	}

	/**
	 * 加载本地图片-可设置角度
	 * 
	 * @param context
	 * @param view
	 *            图片控件
	 * @param res_id
	 *            本地资源图片
	 * @param widget_id_name
	 *            控件ID名称
	 */
	public static void loadImg(Context context, ImageView imageView, Integer res_id, int corner) {
		if (context == null || imageView == null) {
			return;
		}
		Glide.with(context).load(res_id).centerCrop().transform(new GlideRoundTransform(context, corner)).into(imageView);
	}

	/**
	 * 加载网络图片
	 * 
	 * @param context
	 * @param view
	 *            控件
	 * @param url
	 *            图片地址
	 */
	public static void loadImg(Context context, ImageView imageView, String url) {
		if (context == null || imageView == null) {
			return;
		}
		Glide.with(context).load(url).centerCrop().into(imageView);
	}

	/**
	 * 加载网络图片--默认图片
	 * 
	 * @param context
	 * @param view
	 * @param url
	 *            网络图片链接
	 * @param defaultImg
	 *            默认图片
	 */
	public static void loadImg(Context context, ImageView imageView, String url, int defaultImg) {
		if (context == null || imageView == null) {
			return;
		}
		Glide.with(context).load(url).centerCrop().error(defaultImg).into(imageView);
	}

	/**
	 * 加载圆形图片--默认图片
	 * 
	 * @param context
	 * @param view
	 * @param url
	 *            网络图片链接
	 * @param defaultImg
	 *            默认图片
	 */
	public static void loadCircleImg(Context context, ImageView imageView, String url) {
		if (context == null || imageView == null) {
			return;
		}
		Glide.with(context).load(url).centerCrop().transform(new GlideCircleTransform(context)).into(imageView);
	}

	/**
	 * 加载圆形图片--默认图片
	 * 
	 * @param context
	 * @param view
	 * @param url
	 *            网络图片链接
	 * @param defaultImg
	 *            默认图片
	 */
	public static void loadCircleImg(Context context, ImageView imageView, String url, int defaultImg) {
		if (context == null || imageView == null) {
			return;
		}
		Glide.with(context).load(url).centerCrop().transform(new GlideCircleTransform(context)).error(defaultImg).into(imageView);
	}

	/**
	 * 加载原图
	 */
	public static void loadOriginalImg(Context context, ImageView imageView, String url) {
		if (context == null || imageView == null) {
			return;
		}
		Glide.with(context).load(url).into(imageView);
	}

	/**
	 * 加载图片--16:9
	 * 
	 * @param context
	 * @param imageView
	 *            控件
	 * @param url
	 *            图片地址
	 * @param rate
	 *            比例 1：（默认1:1）2：（16:9）
	 * @param defaultImg
	 *            默认图片
	 */
	public static void loadImgRate(Context context, final ImageView imageView, final String url, final int rate, int defaultImg) {
		if (context == null || imageView == null) {
			return;
		}
		Glide.with(context).load(url).listener(new RequestListener<String, GlideDrawable>() {
			@Override
			public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
				return false;
			}

			@Override
			public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
				if (imageView == null) {
					return false;
				}
				if (imageView.getScaleType() != ImageView.ScaleType.FIT_XY) {
					imageView.setScaleType(ImageView.ScaleType.FIT_XY);
				}
				ViewGroup.LayoutParams params = imageView.getLayoutParams();
				int vw = imageView.getWidth() - imageView.getPaddingLeft() - imageView.getPaddingRight();
				if (2 == rate) {
					params.height = vw * 9 / 16;
				} else {
					params.height = vw * 1 / 1;
				}
				imageView.setLayoutParams(params);
				return false;
			}
		}).error(defaultImg).into(imageView);
	}

}
