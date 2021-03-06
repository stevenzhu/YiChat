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
 * 类说明: 尾部上拉加载更多
 * 
 * @Author: peidongxu
 */
public class TailView extends LinearLayout implements PullRefreshView.OnTailStateListener {

	ImageView iv_header_head_logo;

	// 均匀旋转动画
	private RotateAnimation refreshingAnimation;

	private PullRefreshView pullRefreshView;

	private boolean isMore = true;

	public TailView(Context context) {
		super(context);
		refreshingAnimation = (RotateAnimation) AnimationUtils.loadAnimation(context, R.anim.refresh_rotating);
		LinearInterpolator lir = new LinearInterpolator();
		refreshingAnimation.setInterpolator(lir);
		init(context);
	}

	public TailView(Context context, PullRefreshView pullRefreshView) {
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
	public void onScrollChange(View tail, int scrollOffset, int scrollRatio) {
		if (isMore) {
			iv_header_head_logo.setRotation(scrollOffset);
		}
	}

	@Override
	public void onRefreshTail(View tail) {
	}

	@Override
	public void onRetractTail(View tail) {
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

	@Override
	public void onNotMore(View tail) {
		isMore = false;
	}

	@Override
	public void onHasMore(View tail) {
		isMore = true;
	}

	private void restore() {
		iv_header_head_logo.setRotation(0);
	}
}