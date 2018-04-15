package com.shorigo.view.refresh;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.shorigo.yichat.R;

/**
 * 类说明: 下拉刷新头部View
 * 
 * @Author: peidongxu
 */
public class HeadView extends LinearLayout implements PullRefreshView.OnHeadStateListener {

	ImageView iv_header_head_logo;

	// 均匀旋转动画
	private RotateAnimation refreshingAnimation;

	private PullRefreshView pullRefreshView;

	public HeadView(Context context) {
		super(context);
		refreshingAnimation = (RotateAnimation) AnimationUtils.loadAnimation(context, R.anim.refresh_rotating);
		LinearInterpolator lir = new LinearInterpolator();
		refreshingAnimation.setInterpolator(lir);
		init(context);
	}

	public HeadView(Context context, PullRefreshView pullRefreshView) {
		super(context);
		this.pullRefreshView = pullRefreshView;
		refreshingAnimation = (RotateAnimation) AnimationUtils.loadAnimation(context, R.anim.refresh_rotating);
		LinearInterpolator lir = new LinearInterpolator();
		refreshingAnimation.setInterpolator(lir);
		init(context);
	}

	private void init(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View layout = inflater.inflate(R.layout.refresh_head_2, this, false);
		this.addView(layout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		iv_header_head_logo = (ImageView) findViewById(R.id.iv_header_head_logo);
		restore();
	}

	@Override
	public void onScrollChange(View head, int scrollOffset, int scrollRatio) {
		iv_header_head_logo.setRotation(scrollOffset);
	}

	@Override
	public void onRefreshHead(View head) {
	}

	@Override
	public void onRetractHead(View head) {
		// 头部收起
		iv_header_head_logo.startAnimation(refreshingAnimation);
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				if (pullRefreshView != null) {
					pullRefreshView.onHeadViewRefreshAniDone();
				}
				iv_header_head_logo.clearAnimation();
			}
		}, 1000);
	}

	private void restore() {
		iv_header_head_logo.setRotation(0);
	}
}