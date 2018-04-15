package chat_msg_list.chat_msg_list_4.code;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.shorigo.yichat.R;

/**
 * 更多pop
 * 
 * @author peidongxu
 * 
 */
public class MoreMenuPop implements OnClickListener {
	private PopupWindow popupWindow;
	private View v;
	private Handler handler;

	/**
	 * @param v
	 *            点击的控件
	 * @param context
	 * @param handler
	 *            接收返回值的handler
	 */
	public MoreMenuPop(View v, Activity activity, Handler handler) {
		this.v = v;
		this.handler = handler;
		View view = LayoutInflater.from(activity).inflate(R.layout.chat_msg_list_4_more_menu_pop, null);
		RelativeLayout z_all_pop = (RelativeLayout) view.findViewById(R.id.z_all_pop);
		z_all_pop.setOnClickListener(this);
		LinearLayout z_group_create = (LinearLayout) view.findViewById(R.id.z_group_create);
		z_group_create.setOnClickListener(this);
		LinearLayout z_contacts_add = (LinearLayout) view.findViewById(R.id.z_contacts_add);
		z_contacts_add.setOnClickListener(this);

		// 设置popwindow弹出大小
		popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		// 弹出popwindow
		showAsDropDown();
	}

	/**
	 * 下拉式 弹出 pop菜单 parent 右下角
	 * 
	 * @param parent
	 */
	public void showAsDropDown() {
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		// 使其聚集
		popupWindow.setFocusable(true);
		// 设置弹出位置
		popupWindow.showAsDropDown(v, 0, -2);
		// 刷新状态
		popupWindow.update();
	}

	@Override
	public void onClick(View v) {
		Message msg = new Message();
		switch (v.getId()) {
		case R.id.z_group_create:
			// 群组创建
			msg.what = 100;
			handler.sendMessage(msg);
			break;
		case R.id.z_contacts_add:
			// 添加好友
			msg.what = 101;
			handler.sendMessage(msg);
			break;
		default:
			break;
		}
		dismiss();
	}

	public void dismiss() {
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
		}
	}

}
