package register.register_2.code;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shorigo.yichat.R;

public class RegisterAdapter extends BaseAdapter {
	private Context context;
	private List<Map<String, String>> listMap;

	public RegisterAdapter(Context context, List<Map<String, String>> listMap) {
		this.context = context;
		this.listMap = listMap;
	}

	@Override
	public int getCount() {
		if (listMap == null)
			return 0;
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
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.register_2_dialog_item, null);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_dialog_item_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Map<String, String> map = listMap.get(position);
		holder.tv_name.setText(map.get("name"));

		return convertView;
	}

	private class ViewHolder {
		TextView tv_name;// 名称
	}

}
