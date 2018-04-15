package feedback.feedback_1.code;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shorigo.custom_style.StyleUtils;
import com.shorigo.yichat.R;

public class FeedBackAdapter extends BaseAdapter {
	private Context context;
	private List<Map<String, String>> listMap;

	public FeedBackAdapter(Context context, List<Map<String, String>> listMap) {
		this.context = context;
		this.listMap = listMap;
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
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.feedback_1_item, null);
			holder.tv_item_name = (TextView) convertView.findViewById(R.id.tv_item_name);
			holder.iv_item_state = (ImageView) convertView.findViewById(R.id.iv_item_state);
			convertView.setTag(holder);
			StyleUtils.setLayoutStyle(context, convertView);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Map<String, String> map = listMap.get(position);

		holder.tv_item_name.setText(map.get("name"));
		// 0: 未选中 1:选中
		String state = listMap.get(position).get("state");
		if ("0".equals(state)) {
			holder.iv_item_state.setVisibility(View.GONE);
		} else if ("1".equals(state)) {
			holder.iv_item_state.setVisibility(View.VISIBLE);
		}
		return convertView;
	}

	private class ViewHolder {
		TextView tv_item_name;
		ImageView iv_item_state;
	}
}
