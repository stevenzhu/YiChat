package com.hyphenate.easeui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.easeui.R;
import com.shorigo.utils.AsyncTopImgLoadTask;

/**
 * 聊天标题栏
 * 
 * @author peidongxu
 */
public class EaseTitleBar extends RelativeLayout {
	Context context;
	protected RelativeLayout z_title;
	protected RelativeLayout z_back;
	protected ImageView iv_back;
	protected RelativeLayout z_right;
	protected ImageView iv_right;
	protected TextView tv_title;

	public EaseTitleBar(Context context, AttributeSet attrs, int defStyle) {
		this(context, attrs);
	}

	public EaseTitleBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public EaseTitleBar(Context context) {
		super(context);
		init(context, null);
	}

	private void init(Context context, AttributeSet attrs) {
		this.context = context;
		LayoutInflater.from(context).inflate(R.layout.ease_widget_title_bar, this);
		z_title = (RelativeLayout) findViewById(R.id.z_title);
		z_back = (RelativeLayout) findViewById(R.id.z_back);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		z_right = (RelativeLayout) findViewById(R.id.z_right);
		iv_right = (ImageView) findViewById(R.id.iv_right);
		tv_title = (TextView) findViewById(R.id.tv_title);

		parseStyle(context, attrs);
	}

	private void parseStyle(Context context, AttributeSet attrs) {
		if (attrs != null) {
			TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.EaseTitleBar);
			String title = ta.getString(R.styleable.EaseTitleBar_titleBarTitle);
			tv_title.setText(title);

			Drawable leftDrawable = ta.getDrawable(R.styleable.EaseTitleBar_titleBarLeftImage);
			if (null != leftDrawable) {
				iv_back.setImageDrawable(leftDrawable);
			}
			Drawable rightDrawable = ta.getDrawable(R.styleable.EaseTitleBar_titleBarRightImage);
			if (null != rightDrawable) {
				iv_right.setImageDrawable(rightDrawable);
			}

			Drawable background = ta.getDrawable(R.styleable.EaseTitleBar_titleBarBackground);
			if (null != background) {
				z_title.setBackgroundDrawable(background);
			}

			ta.recycle();
		}
	}

	public void setLeftImageResource(int resId) {
		iv_back.setImageResource(resId);
		iv_back.setVisibility(View.VISIBLE);
	}

	public void setRightImageResource(int resId) {
		iv_right.setImageResource(resId);
		iv_right.setVisibility(View.VISIBLE);
	}

	public void setLeftLayoutClickListener(OnClickListener listener) {
		z_back.setOnClickListener(listener);
	}

	public void setRightLayoutClickListener(OnClickListener listener) {
		z_right.setOnClickListener(listener);
	}

	public void setLeftLayoutVisibility(int visibility) {
		z_back.setVisibility(visibility);
	}

	public void setRightLayoutVisibility(int visibility) {
		z_right.setVisibility(visibility);
	}

	public void setTitle(String title) {
		new AsyncTopImgLoadTask(context, title, tv_title, null).execute();
	}
	
	public void setGroupTitle(String title) {
		tv_title.setText(title);
	}

	public void setBackgroundResource(int color) {
		z_title.setBackgroundResource(color);
	}

	public void setBackgroundColor(int color) {
		z_title.setBackgroundColor(color);
	}

	public RelativeLayout getLeftLayout() {
		return z_back;
	}

	public RelativeLayout getRightLayout() {
		return z_right;
	}
}
