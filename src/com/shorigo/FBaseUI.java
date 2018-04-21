package com.shorigo;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.aitangba.swipeback.SwipeBackActivity;
import com.shorigo.utils.Constants;
import com.shorigo.yichat.R;

import yichat.util.StatusBarCompat;

/**
 * 基类
 * 
 * @author peidongxu
 * 
 */
public abstract class FBaseUI extends SwipeBackActivity implements OnClickListener {
	private String isExit;
	protected void setImmerseLayout() {
		//第二个参数是想要设置的颜色
		StatusBarCompat.compat(this, getResources().getColor(R.color.color_orange));
	}
	/**
	 * 描述：创建
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		super.onCreate(savedInstanceState);
		setImmerseLayout();

		init();
		findView_AddListener();
		isExit = getIntent().getStringExtra("isExit");
		View z_back = findViewById(R.id.z_back);
		if (z_back != null) {
			String monudle = this.toString().split("@")[0];
			if (Constants.listActivity.contains(monudle) && "1".equals(isExit)) {
				z_back.setVisibility(View.GONE);
			}
			z_back.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					back();
				}
			});
		}
		prepareData();

	}

	/**
	 * 描述：初始化操作
	 */
	protected void init() {
		loadViewLayout();
	}

	@Override
	public void onClick(View v) {
		onMyClick(v);
	}

	/**
	 * 描述：加载视图
	 */
	protected abstract void loadViewLayout();

	/**
	 * 描述：初始化控件，添加事件
	 */
	protected abstract void findView_AddListener();

	/**
	 * 描述：准备数据
	 */
	protected abstract void prepareData();

	/**
	 * 描述：点击事件
	 */
	protected abstract void onMyClick(View v);

	/**
	 * 返回事件
	 */
	protected abstract void back();

	/**
	 * 设置左侧图标
	 */
	public void setLeftImageResource(int resid) {
		ImageView iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_back.setImageResource(resid);
	}

	/**
	 * 只设置标题背景
	 */
	public void setTitleBg(int resid) {
		View z_title = (View) findViewById(R.id.z_title);
		z_title.setBackgroundResource(resid);
	}

	/**
	 * 只设置标题
	 */
	public void setTitle(String title) {
		TextView tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTitle.setText(title);
	}

	/**
	 * 设置标题和标题颜色
	 */
	public void setTitle(String title, int color) {
		TextView tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTitle.setTextColor(color);
		tvTitle.setText(title);
	}

	/**
	 * 设置标题，并隐藏返回键
	 */
	public void setTitleHideBack(String title) {
		TextView tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTitle.setText(title);
		View z_back = findViewById(R.id.z_back);
		z_back.setVisibility(View.GONE);
	}

	/**
	 * 设置右侧按钮-文字
	 */
	public void setRightButton(String name) {
		TextView tv_right = (TextView) findViewById(R.id.tv_right);
		tv_right.setText(name);
		tv_right.setVisibility(View.VISIBLE);
		View z_right = findViewById(R.id.z_right);
		z_right.setOnClickListener(this);
	}

	/**
	 * 设置右侧按钮-图片
	 */
	public void setRightButton(int resId) {
		ImageView iv_right = (ImageView) findViewById(R.id.iv_right);
		iv_right.setImageResource(resId);
		iv_right.setVisibility(View.VISIBLE);
		View z_right = findViewById(R.id.z_right);
		z_right.setOnClickListener(this);
	}

	/**
	 * 设置左侧按钮-图片
	 */
	public void setLeftButton(int resId) {
		ImageView back = (ImageView) findViewById(R.id.iv_back);
		back.setImageResource(resId);
		back.setVisibility(View.VISIBLE);
		View z_back = findViewById(R.id.z_back);
		if (z_back != null) {
			z_back.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					back();
				}
			});
		}
	}

}
