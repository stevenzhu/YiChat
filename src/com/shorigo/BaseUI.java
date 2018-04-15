package com.shorigo;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.aitangba.swipeback.SwipeBackActivity;
import com.shorigo.custom_style.StyleUtils;
import com.shorigo.utils.Constants;
import com.shorigo.yichat.R;

import yichat.util.StatusBarCompat;

/**
 * 程序基类
 * 
 * @author peidongxu
 * 
 */
public abstract class BaseUI extends SwipeBackActivity implements OnClickListener {
	// 退出时间间隔
	private long exitTime = 0;
	private String isExit = "0";
	private Activity activity;
	public boolean isShow = true;
	private InputMethodManager inputManager;

	protected void setImmerseLayout() {
		//第二个参数是想要设置的颜色
		StatusBarCompat.compat(this, getResources().getColor(R.color.color_orange));
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		super.onCreate(savedInstanceState);
		setImmerseLayout();
		AppManager.getAppManager().addActivity(this);
		activity = this;
		inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

		init();
		isExit = getIntent().getStringExtra("isExit");
		findView_AddListener();
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
	 * 初始化
	 */
	protected void init() {
		loadViewLayout();
	}

	/**
	 * 退出程序
	 */
	protected void exit() {
		if ((System.currentTimeMillis() - exitTime) > 2000) {
			MyApplication.getInstance().showToast("再按一次退出返回桌面");
			exitTime = System.currentTimeMillis();
		} else {
			finish();
		}
	}

	/**
	 * 加载布局
	 */
	protected abstract void loadViewLayout();

	/**
	 * 加载布局ID
	 */
	protected abstract void findView_AddListener();

	/**
	 * 数据逻辑处理
	 */
	protected abstract void prepareData();

	/**
	 * 点击事件
	 */
	protected abstract void onMyClick(View v);

	/**
	 * 返回事件
	 */
	protected abstract void back();

	@Override
	public void onClick(View v) {
		onMyClick(v);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			String monudle = this.toString().split("@")[0];
			if (Constants.listActivity.contains(monudle) && "1".equals(isExit)) {
				exit();
			} else {
				back();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * 点击空白处隐藏软键盘
	 * 
	 * @param event
	 * @return
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
				inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), inputManager.HIDE_NOT_ALWAYS);
			}
		}
		return super.onTouchEvent(event);
	}


	/**
	 * 设置左侧图标
	 */
	public void setLeftImageResource(int resid) {
		ImageView iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_back.setImageResource(resid);
	}

	/**
	 * 设置标题
	 */
	public void setTitle(String title) {
		TextView tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTitle.setText(title);
	}

	/**
	 * 设置标题、字体颜色
	 */
	public void setTitle(String title, int text_color) {
		TextView tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTitle.setText(title);
		tvTitle.setTextColor(text_color);
	}

	/**
	 * 设置标题背景
	 */
	public void setTitleBg(int resid) {
		View z_title = (View) findViewById(R.id.z_title);
		z_title.setBackgroundResource(resid);
	}

	/**
	 * 设置返回按钮显示隐藏
	 */
	public void setBackVisible(int visible) {
		View z_back = findViewById(R.id.z_back);
		z_back.setVisibility(visible);
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
	 * 设置右侧按钮-文字、颜色
	 */
	public void setRightButton(String name, int text_color) {
		TextView tv_right = (TextView) findViewById(R.id.tv_right);
		tv_right.setText(name);
		tv_right.setTextColor(text_color);
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

	@Override
	public void onBackPressed() {
		AppManager.getAppManager().finishActivity(activity);
	}

}
