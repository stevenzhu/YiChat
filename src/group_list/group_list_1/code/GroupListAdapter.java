package group_list.group_list_1.code;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hyphenate.chat.EMGroup;
import com.shorigo.yichat.R;

/**
 * 群组列表adapter
 * 
 * @author peidongxu
 * 
 */
public class GroupListAdapter extends ArrayAdapter<EMGroup> {

	private Context context;
	private LayoutInflater inflater;

	public GroupListAdapter(Context context, int res, List<EMGroup> groups) {
		super(context, res, groups);
		this.context = context;
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return super.getCount();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.group_list_1_item, parent, false);
			holder = new ViewHolder();
			holder.tv_item_name = (TextView) convertView.findViewById(R.id.tv_item_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (getItem(position) != null) {
			holder.tv_item_name.setText(getItem(position).getGroupName());
		}

		return convertView;
	}

	private class ViewHolder {
		TextView tv_item_name;
	}
}
