package com.shorigo;

import java.util.Calendar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public abstract class BaseFragmentUI extends Fragment implements OnClickListener {
	/** 视图 */
	protected View view;

	/**
	 * 描述：加载视图
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		init(inflater, container);
		findView_AddListener();
		prepareData();
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	/**
	 * 描述：初始化操作
	 */
	protected void init(LayoutInflater inflater, ViewGroup container) {
		loadViewLayout(inflater, container);
	}

	/**
	 * 描述：加载视图
	 */
	protected abstract void loadViewLayout(LayoutInflater inflater, ViewGroup container);

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

	public static final int MIN_CLICK_DELAY_TIME = 1000;
	private long lastClickTime = 0;

	@Override
	public void onClick(View v) {
		// 拦截双击事件
		long currentTime = Calendar.getInstance().getTimeInMillis();
		if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
			lastClickTime = currentTime;
			onMyClick(v);
		}
	}

}
