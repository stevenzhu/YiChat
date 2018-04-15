package contacts_add.contacts_add_1.code;

import java.util.List;
import java.util.Map;

import user_card.user_card_1.code.UserCardUI;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shorigo.utils.BitmapHelp;
import com.shorigo.yichat.R;

public class ContactsAddAdapter extends BaseAdapter {
	private Context context;
	private List<Map<String, String>> listMap;
	private Handler mHandler;

	public ContactsAddAdapter(Context context, List<Map<String, String>> listMap, Handler mHandler) {
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
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.contacts_add_1_item, null);
			holder.z_item_all = (LinearLayout) convertView.findViewById(R.id.z_item_all);
			holder.iv_item_avatar = (ImageView) convertView.findViewById(R.id.iv_item_avatar);
			holder.tv_item_nick = (TextView) convertView.findViewById(R.id.tv_item_nick);
			holder.tv_item_add = (TextView) convertView.findViewById(R.id.tv_item_add);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final Map<String, String> map = listMap.get(position);

		BitmapHelp.loadImg(context, holder.iv_item_avatar, map.get("avatar"), R.drawable.default_avatar_angle);

		holder.tv_item_nick.setText(map.get("user_nick"));
		holder.z_item_all.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(context, UserCardUI.class);
				intent.putExtra("user_id", map.get("user_id"));
				context.startActivity(intent);
			}
		});
		holder.tv_item_add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Message msg = new Message();
				msg.what = 1;
				msg.arg1 = position;
				mHandler.sendMessage(msg);
			}
		});

		return convertView;
	}

	final static class ViewHolder {
		LinearLayout z_item_all;
		ImageView iv_item_avatar;
		TextView tv_item_nick;
		TextView tv_item_add;
	}
}
