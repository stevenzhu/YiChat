package group_create.group_create_1.code;

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

import com.shorigo.utils.BitmapHelp;
import com.shorigo.yichat.R;

/**
 * 创建群组_添加成员abapter
 * 
 * @author peidongxu
 * 
 */
public class GroupCreateAdapter extends BaseAdapter {
	private Context context;
	private List<Map<String, String>> listMap;
	private Handler mHandler;

	public GroupCreateAdapter(Context context, List<Map<String, String>> listMap, Handler mHandler) {
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.group_create_1_item, null);
			holder.z_item_all = (RelativeLayout) convertView.findViewById(R.id.z_item_all);
			holder.iv_item_avatar = (ImageView) convertView.findViewById(R.id.iv_item_avatar);
			holder.tv_item_name = (TextView) convertView.findViewById(R.id.tv_item_name);
			holder.iv_item_del = (ImageView) convertView.findViewById(R.id.iv_item_del);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Map<String, String> map = listMap.get(position);
		BitmapHelp.loadImg(context, holder.iv_item_avatar, map.get("avatar"), R.drawable.default_avatar_angle);
		holder.tv_item_name.setText(map.get("user_nick"));

		holder.iv_item_del.setOnClickListener(new OnClickListener() {

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

	private class ViewHolder {
		RelativeLayout z_item_all;
		ImageView iv_item_avatar;
		TextView tv_item_name;
		ImageView iv_item_del;
	}
}
