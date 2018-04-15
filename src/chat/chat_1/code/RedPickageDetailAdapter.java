package chat.chat_1.code;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shorigo.utils.BitmapHelp;
import com.shorigo.utils.TimeUtil;
import com.shorigo.utils.Utils;
import com.shorigo.yichat.R;

public class RedPickageDetailAdapter extends BaseAdapter {
	private Context context;
	private List<Map<String, String>> listMap;
	private String max_sn;

	public RedPickageDetailAdapter(Context context, List<Map<String, String>> listMap, String max_sn) {
		this.context = context;
		this.listMap = listMap;
		this.max_sn = max_sn;
	}

	public void setData(List<Map<String, String>> listMap, String max_sn) {
		this.listMap = listMap;
		this.max_sn = max_sn;
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
			convertView = LayoutInflater.from(context).inflate(R.layout.ease_activity_redpickage_detail_item, null);
			holder.z_item_all = (LinearLayout) convertView.findViewById(R.id.z_item_all);
			holder.iv_item_avatar = (ImageView) convertView.findViewById(R.id.iv_item_avatar);
			holder.tv_item_name = (TextView) convertView.findViewById(R.id.tv_item_name);
			holder.tv_item_money = (TextView) convertView.findViewById(R.id.tv_item_money);
			holder.tv_item_time = (TextView) convertView.findViewById(R.id.tv_item_time);
			holder.tv_item_best = (TextView) convertView.findViewById(R.id.tv_item_best);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final Map<String, String> map = listMap.get(position);

		BitmapHelp.loadImg(context, holder.iv_item_avatar, map.get("receive_user_avatar"), R.drawable.default_avatar_angle);
		holder.tv_item_name.setText(map.get("receive_user_nick"));
		holder.tv_item_money.setText(map.get("money") + "元");
		String add_time = TimeUtil.getData("yyyy年MM月dd日 HH:mm:ss", map.get("time"));
		holder.tv_item_time.setText(add_time);
		if (!Utils.isEmity(max_sn) && max_sn.equals(map.get("sn"))) {
			holder.tv_item_best.setVisibility(View.VISIBLE);
		} else {
			holder.tv_item_best.setVisibility(View.GONE);
		}

		return convertView;
	}

	private class ViewHolder {
		LinearLayout z_item_all;
		ImageView iv_item_avatar;
		TextView tv_item_name;
		TextView tv_item_money;
		TextView tv_item_time;
		TextView tv_item_best;
	}

}
