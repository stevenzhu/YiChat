package main.main_1.code;

import java.util.Map;

import login.login_1.code.LoginUI;
import user_card.user_card_1.code.UserCardUI;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.shorigo.utils.BitmapHelp;
import com.shorigo.utils.MyConfig;
import com.shorigo.utils.Utils;
import com.shorigo.yichat.R;

/**
 * 弹出的用户框
 * 
 * @author peidongxu
 * 
 */
public class MapPersonalPop implements OnClickListener {
	private final PopupWindow popupWindow;
	private final Context context;
	private final Map<String, String> userMap;

	/**
	 * @param v
	 *            点击的控件
	 * @param context
	 */
	public MapPersonalPop(View v, Context context, Map<String, String> userMap) {
		this.context = context;
		this.userMap = userMap;
		View view = LayoutInflater.from(context).inflate(R.layout.main_1_pop, null);

		ImageView iv_avatar = (ImageView) view.findViewById(R.id.iv_avatar);
		TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
//		ImageView iv_sex = (ImageView) view.findViewById(R.id.iv_sex);
//		TextView tv_age = (TextView) view.findViewById(R.id.tv_age);
		TextView tvSexAge = (TextView) view.findViewById(R.id.tvSexAge);
		TextView tv_family = (TextView) view.findViewById(R.id.tv_family);
		TextView tv_distance = (TextView) view.findViewById(R.id.tv_distance);
		LinearLayout z_all_pop = (LinearLayout) view.findViewById(R.id.z_all_pop);
		z_all_pop.setOnClickListener(this);

		BitmapHelp.loadImg(context, iv_avatar, userMap.get("avatar"), R.drawable.default_avatar_angle);
		tv_name.setText(userMap.get("user_nick"));
		// 1: 男 2: 女
		boolean isBoy = "1".equals(userMap.get("sex"));
//		if ("1".equals(sex)) {
//			iv_sex.setBackgroundResource(R.drawable.icon_boy);
//		} else if ("2".equals(sex)) {
//			iv_sex.setBackgroundResource(R.drawable.icon_girl);
//		}
		tvSexAge.setSelected(!isBoy);
		tvSexAge.setText(userMap.get("age"));
		tv_family.setText(userMap.get("family_name"));
		tv_distance.setText(userMap.get("distance") + "km");

		// 设置popwindow弹出大小
		popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		// 弹出popwindow
		showAsDropDown(v);
		context = null;
	}

	/**
	 * 下拉式 弹出 pop菜单 parent 右下角
	 * 
	 * @param parent
	 */
	public void showAsDropDown(View parent) {
		// 这个是为了点击“返回Back”也能使其消失
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		// 设置允许在外点击消失
		popupWindow.setOutsideTouchable(false);
		// 使其聚集
		popupWindow.setFocusable(true);
		// 设置弹出位置
		popupWindow.showAsDropDown(parent, 0, 0);
		// 刷新状态
		popupWindow.update();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.z_all_pop:
			// 点击跳转
			if (Utils.isEmity(MyConfig.getToken(context))) {
				context.startActivity(new Intent(context, LoginUI.class));
				return;
			}
			dismissPop();
			if (userMap != null) {
				Intent intent = new Intent(context, UserCardUI.class);
				intent.putExtra("user_id", userMap.get("user_id"));
				context.startActivity(intent);
			}
			break;
		default:
			break;
		}
	}

	public void dismissPop() {
		if (popupWindow.isShowing()) {
			popupWindow.dismiss();
		}
	}

	public boolean getPopIsShow() {
		return popupWindow.isShowing();
	}

}
