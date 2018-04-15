package contacts_list.contacts_list_1.code;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.shorigo.yichat.R;

/**
 * 描述：侧边栏显示
 * 
 * @author peidongxu
 * 
 */
public class PopSB {
	private PopupWindow popupWindow;
	private View v;
	private TextView tv_pop_sb;

	/**
	 * 
	 * @param v
	 *            点击的控件
	 * @param context
	 * @param arrStr
	 *            要加载的数据
	 * @param handler
	 *            接收返回值的handler
	 * @param tag
	 *            回调时handler用来判断返回值
	 */
	public PopSB(View v, Context context) {
		this.v = v;

		View view = LayoutInflater.from(context).inflate(R.layout.contacts_list_1_pop_sb, null);
		// 设置popwindow弹出大小
		popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		// 弹出popwindow
		showAsDropDown();
		context = null;

		tv_pop_sb = (TextView) view.findViewById(R.id.tv_pop_sb);

		int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		tv_pop_sb.measure(w, h);
		tv_pop_sb.setWidth(tv_pop_sb.getMeasuredHeight());

	}

	/**
	 * 下拉式 弹出 pop菜单 parent 右下角
	 * 
	 * @param parent
	 */
	public void showAsDropDown() {
		// 设置弹出位置
		popupWindow.showAtLocation(v, Gravity.TOP, 0, 0);
		// 刷新状态
		popupWindow.update();

	}

	/**
	 * 隐藏菜单
	 */
	public void dismiss() {
		popupWindow.dismiss();

	}

	public void setText(String s) {
		Message msg = new Message();
		msg.obj = s;
		handler.sendMessage(msg);
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			tv_pop_sb.setText(msg.obj.toString());
		}
	};
}
