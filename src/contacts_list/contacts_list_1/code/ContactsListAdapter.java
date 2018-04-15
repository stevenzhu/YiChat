package contacts_list.contacts_list_1.code;

import java.util.List;
import java.util.Map;

import user_card.user_card_1.code.UserCardUI;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.shorigo.utils.BitmapHelp;
import com.shorigo.yichat.R;

/**
 * 联系人adapter
 * 
 * @author peidongxu
 * 
 */
public class ContactsListAdapter extends BaseAdapter implements SectionIndexer {
	private Context context;
	private List<Map<String, String>> listMap;

	public ContactsListAdapter(Context context, List<Map<String, String>> listMap) {
		this.context = context;
		this.listMap = listMap;
	}

	public void setData(List<Map<String, String>> listMap) {
		this.listMap = listMap;
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
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.contacts_list_1_item, null);
			holder.z_item_all = (LinearLayout) convertView.findViewById(R.id.z_item_all);
			holder.tv_item_sort = (TextView) convertView.findViewById(R.id.tv_item_sort);
			holder.iv_item_avatar = (ImageView) convertView.findViewById(R.id.iv_item_avatar);
			holder.tv_item_nick = (TextView) convertView.findViewById(R.id.tv_item_nick);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final Map<String, String> map = listMap.get(position);

		// 根据position获取分类的首字母的Char ascii值
		int section = getSectionForPosition(position);
		// 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
		if (position == getPositionForSection(section)) {
			holder.tv_item_sort.setVisibility(View.VISIBLE);
			holder.tv_item_sort.setText(map.get("sort"));
		} else {
			holder.tv_item_sort.setVisibility(View.GONE);
		}

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
		return convertView;
	}

	final static class ViewHolder {
		LinearLayout z_item_all;
		TextView tv_item_sort;
		ImageView iv_item_avatar;
		TextView tv_item_nick;
	}

	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		return listMap.get(position).get("sort").charAt(0);
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = listMap.get(i).get("sort");
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public Object[] getSections() {
		return null;
	}

}
