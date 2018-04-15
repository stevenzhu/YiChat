package chat.chat_1.code;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.shorigo.yichat.R;

/**
 * 消息长按菜单
 * 
 * @author peidongxu
 * 
 */
public class MenuPop implements OnClickListener {
	private PopupWindow popupWindow;
	private View v;
	private Handler handler;
	private View view = null;

	public static final int RESULT_CODE_COPY = 1;
	public static final int RESULT_CODE_DELETE = 2;
	public static final int RESULT_CODE_FORWARD = 3;

	/**
	 * @param v
	 *            点击的控件
	 * @param context
	 * @param handler
	 *            接收返回值的handler
	 */
	public MenuPop(View v, Context context, Handler handler, int type) {
		this.v = v;
		this.handler = handler;

		if (type == EMMessage.Type.TXT.ordinal()) {
			view = LayoutInflater.from(context).inflate(R.layout.ease_context_menu_for_text, null);
			TextView tv_forward = (TextView) view.findViewById(R.id.tv_context_menu_for_text_forward);
			tv_forward.setOnClickListener(this);
			TextView tv_copy = (TextView) view.findViewById(R.id.tv_context_menu_for_text_copy);
			tv_copy.setOnClickListener(this);
			TextView tv_delete = (TextView) view.findViewById(R.id.tv_context_menu_for_text_delete);
			tv_delete.setOnClickListener(this);
		} else if (type == EMMessage.Type.IMAGE.ordinal()) {
			view = LayoutInflater.from(context).inflate(R.layout.ease_context_menu_for_image, null);
			TextView tv_forward = (TextView) view.findViewById(R.id.tv_context_menu_for_image_forward);
			tv_forward.setOnClickListener(this);
			TextView tv_delete = (TextView) view.findViewById(R.id.tv_context_menu_for_image_delete);
			tv_delete.setOnClickListener(this);
		} else if (type == EMMessage.Type.VOICE.ordinal()) {
			view = LayoutInflater.from(context).inflate(R.layout.ease_context_menu_for_voice, null);
			TextView tv_delete = (TextView) view.findViewById(R.id.tv_context_menu_for_voice_delete);
			tv_delete.setOnClickListener(this);
		} else if (type == EMMessage.Type.FILE.ordinal()) {
			view = LayoutInflater.from(context).inflate(R.layout.ease_context_menu_for_file, null);
			TextView tv_delete = (TextView) view.findViewById(R.id.tv_context_menu_for_file_delete);
			tv_delete.setOnClickListener(this);
		} else if (type == EMMessage.Type.LOCATION.ordinal()) {
			view = LayoutInflater.from(context).inflate(R.layout.ease_context_menu_for_location, null);
			TextView tv_delete = (TextView) view.findViewById(R.id.tv_context_menu_for_loction_delete);
			tv_delete.setOnClickListener(this);
		} else if (type == EMMessage.Type.VIDEO.ordinal()) {
			view = LayoutInflater.from(context).inflate(R.layout.ease_context_menu_for_video, null);
			TextView tv_delete = (TextView) view.findViewById(R.id.tv_context_menu_for_video_delete);
			tv_delete.setOnClickListener(this);
		}

		// 设置popwindow弹出大小
		popupWindow = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		// 弹出popwindow
		showAsDropDown();

		context = null;
	}

	/**
	 * 弹出 pop菜单
	 */
	public void showAsDropDown() {
		view.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
		int popupWidth = view.getMeasuredWidth();
		int popupHeight = view.getMeasuredHeight();
		int[] location = new int[2];
		v.getLocationOnScreen(location);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setOutsideTouchable(true);
		popupWindow.setTouchable(true);
		// 设置弹出位置
		popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, (location[0] + v.getWidth() / 2) - popupWidth / 2, location[1] - popupHeight);
	}

	public void copy() {
		Message msg = new Message();
		msg.what = RESULT_CODE_COPY;
		handler.sendMessage(msg);
		dismissPop();
	}

	public void delete() {
		Message msg = new Message();
		msg.what = RESULT_CODE_DELETE;
		handler.sendMessage(msg);
		dismissPop();
	}

	public void forward() {
		Message msg = new Message();
		msg.what = RESULT_CODE_FORWARD;
		handler.sendMessage(msg);
		dismissPop();
	}

	// public void open() {
	// Message msg = new Message();
	// msg.what = ChatUI.RESULT_CODE_OPEN;
	// msg.arg1 = position;
	// handler.sendMessage(msg);
	// dismissPop();
	// }

	// public void download() {
	// Message msg = new Message();
	// msg.what = ChatUI.RESULT_CODE_DWONLOAD;
	// handler.sendMessage(msg);
	// dismissPop();
	// }

	// public void toCloud() {
	// Message msg = new Message();
	// msg.what = ChatUI.RESULT_CODE_TO_CLOUD;
	// handler.sendMessage(msg);
	// dismissPop();
	// }

	private void dismissPop() {
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_context_menu_for_text_forward:
		case R.id.tv_context_menu_for_image_forward:
			forward();
			break;
		case R.id.tv_context_menu_for_text_copy:
			copy();
			break;
		case R.id.tv_context_menu_for_text_delete:
		case R.id.tv_context_menu_for_image_delete:
		case R.id.tv_context_menu_for_voice_delete:
		case R.id.tv_context_menu_for_file_delete:
		case R.id.tv_context_menu_for_loction_delete:
		case R.id.tv_context_menu_for_video_delete:
			delete();
			break;
		default:
			break;
		}
	}
}
