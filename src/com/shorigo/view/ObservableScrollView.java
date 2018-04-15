package com.shorigo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class ObservableScrollView extends ScrollView {
	private ScrollViewListener scrollViewListener = null;

	public ObservableScrollView(Context context) {
		super(context);
	}

	public ObservableScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ObservableScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setScrollViewListener(ScrollViewListener scrollViewListener) {
		this.scrollViewListener = scrollViewListener;
	}

	@Override
	protected void onScrollChanged(int x, int y, int oldx, int oldy) {
		super.onScrollChanged(x, y, oldx, oldy);

		if (Math.abs(y - oldy) > 5) {
			if (oldy > y) {
				// 上滑动
				if (scrollViewListener != null) {
					scrollViewListener.onUpSlide();
				}
			} else {
				// 下滑动
				if (scrollViewListener != null) {
					scrollViewListener.onDownSlide();
				}
			}
		}
		if (y == 0) {
			// 上滑动
			if (scrollViewListener != null) {
				scrollViewListener.onUpSlide();
			}
		}
	}

	public interface ScrollViewListener {
		void onUpSlide();

		void onDownSlide();
	}

}
