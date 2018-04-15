package chat.chat_1.code;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shorigo.custom_style.StyleUtils;
import com.shorigo.utils.BitmapHelp;
import com.shorigo.yichat.R;

public class ForwardMessageAdapter extends BaseAdapter {
	private Context context;
	private List<Map<String, String>> listMap;
	private Handler mHandler;

	public ForwardMessageAdapter(Context context, List<Map<String, String>> listMap, Handler mHandler) {
		this.context = context;
		this.listMap = listMap;
		this.mHandler = mHandler;
	}

	public void setData(List<Map<String, String>> listMap) {
		this.listMap = listMap;
		notifyDataSetChanged();

	}

	@Override
	public int getCount() {
		if (listMap == null) {
			return 0;
		}
		return listMap.size();
	}

	@Override
	public Object getItem(int position) {
		return listMap.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.ease_activity_forward_message_item, null);
			holder.z_item_all = (RelativeLayout) convertView.findViewById(R.id.z_item_all);
			holder.iv_item_avatar = (ImageView) convertView.findViewById(R.id.iv_item_avatar);
			holder.tv_item_nick = (TextView) convertView.findViewById(R.id.tv_item_nick);
			holder.z_item_select = (RelativeLayout) convertView.findViewById(R.id.z_item_select);
			holder.iv_item_select = (ImageView) convertView.findViewById(R.id.iv_item_select);
			convertView.setTag(holder);
			StyleUtils.setTabBg(context, holder.iv_item_select, new int[] { R.drawable.group_add_member_1_icon_checkbox_down, R.drawable.group_add_member_1_icon_checkbox_up });
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final Map<String, String> map = listMap.get(position);

		BitmapHelp.loadImg(context, holder.iv_item_avatar, map.get("avatar"), R.drawable.default_avatar_angle);
		holder.tv_item_nick.setText(map.get("user_nick"));

		// 选中
		final String is_select = map.get("is_select");

		if ("1".equals(is_select)) {
			holder.z_item_select.setSelected(true);
		} else {
			holder.z_item_select.setSelected(false);
		}

		holder.z_item_select.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Message msg = new Message();
				msg.arg1 = position;
				if ("1".equals(is_select)) {
					msg.what = 1;
				} else {
					msg.what = 2;
				}
				mHandler.sendMessage(msg);
			}
		});

		return convertView;
	}

	private class ViewHolder {
		RelativeLayout z_item_all;
		ImageView iv_item_avatar;
		TextView tv_item_nick;
		RelativeLayout z_item_select;
		ImageView iv_item_select;
	}

}
