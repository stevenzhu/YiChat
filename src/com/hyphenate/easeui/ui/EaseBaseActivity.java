package com.hyphenate.easeui.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.aitangba.swipeback.SwipeBackActivity;
import com.hyphenate.easeui.EaseUI;
import com.shorigo.yichat.R;

import yichat.util.StatusBarCompat;

@SuppressLint({ "NewApi", "Registered" })
public class EaseBaseActivity extends SwipeBackActivity {

	protected InputMethodManager inputMethodManager;
	protected void setImmerseLayout() {
		//第二个参数是想要设置的颜色
		StatusBarCompat.compat(this, getResources().getColor(R.color.color_orange));
	}
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		// http://stackoverflow.com/questions/4341600/how-to-prevent-multiple-instances-of-an-activity-when-it-is-launched-with-differ/
		// should be in launcher activity, but all app use this can avoid the
		// problem
		setImmerseLayout();
		if (!isTaskRoot()) {
			Intent intent = getIntent();
			String action = intent.getAction();
			if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && action.equals(Intent.ACTION_MAIN)) {
				finish();
				return;
			}
		}
		inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// cancel the notification
		EaseUI.getInstance().getNotifier().reset();
	}

	protected void hideSoftKeyboard() {
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	/**
	 * back
	 * 
	 * @param view
	 */
	public void back(View view) {
		finish();
	}
}
