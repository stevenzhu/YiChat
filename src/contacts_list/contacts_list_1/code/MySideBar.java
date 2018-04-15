package contacts_list.contacts_list_1.code;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.shorigo.yichat.R;

/**
 * 描述：重写SideBar 实现右侧菜单
 * 
 * @author peidongxu
 * 
 */
public class MySideBar extends View {
	/** 字母排序数组 */
	public static String[] LETTER = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#" };

	OnTouchingLetterChangedListener onTouchingLetterChangedListener;
	int choose = -1;
	Paint paint = new Paint();

	public MySideBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MySideBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MySideBar(Context context) {
		super(context);
	}

	/**
	 * 重写这个方法
	 */
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (showBkg) {
			canvas.drawColor(Color.parseColor("#40000000"));
		}

		int height = getHeight();
		int width = getWidth();
		int singleHeight = height / LETTER.length;
		for (int i = 0; i < LETTER.length; i++) {
			paint.setColor(Color.GRAY);
			// paint.setColor(Color.WHITE);
			paint.setTypeface(Typeface.DEFAULT_BOLD);
			paint.setAntiAlias(true);
			paint.setTextSize(getResources().getDimensionPixelSize(R.dimen.text_size12));
			if (i == choose) {
				paint.setColor(Color.parseColor("#525252"));
				// paint.setFakeBoldText(true);
			}
			float xPos = width / 2 - paint.measureText(LETTER[i]) / 2;
			float yPos = singleHeight * i + singleHeight;
			canvas.drawText(LETTER[i], xPos, yPos, paint);
			paint.reset();
		}

	}

	private boolean showBkg = false;

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		final float y = event.getY();
		final int oldChoose = choose;
		final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
		final int c = (int) (y / getHeight() * LETTER.length);

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			showBkg = true;
			if (oldChoose != c && listener != null) {
				if (c >= 0 && c < LETTER.length) {
					if (listener != null) {
						listener.onTouchingLetterChanged(LETTER[c]);
						listener.onTouchingLetterChanged(c);
					}
					choose = c;
					invalidate();
				}
			}

			break;
		case MotionEvent.ACTION_MOVE:
			if (oldChoose != c && listener != null) {
				if (c >= 0 && c < LETTER.length) {
					if (listener != null) {
						listener.onTouchingLetterChanged(LETTER[c]);
						listener.onTouchingLetterChanged(c);
					}
					choose = c;
					invalidate();
				}
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			showBkg = false;
			choose = -1;
			invalidate();
			if (listener != null)
				listener.onTouchingLetterUP();
			break;
		}
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}

	/**
	 * 向外公开的方法
	 * 
	 * @param onTouchingLetterChangedListener
	 */
	public void setOnTouchingLetterChangedListener(OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
		this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
	}

	/**
	 * 接口
	 * 
	 * @author coder
	 * 
	 */
	public interface OnTouchingLetterChangedListener {
		public void onTouchingLetterChanged(String s);

		public void onTouchingLetterChanged(int idx);

		public void onTouchingLetterUP();
	}

}
