package withdrawal_list.withdrawal_list_2.code;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shorigo.custom_style.StyleUtils;
import com.shorigo.utils.TimeUtil;
import com.shorigo.yichat.R;

/**
 * 提现记录列表adapter
 * 
 * @author peidongxu
 * 
 */
public class WithdrawalListAdapter extends BaseAdapter {

	private Context context;
	private List<Map<String, String>> listMap;

	public WithdrawalListAdapter(Context context, List<Map<String, String>> listMap) {
		this.context = context;
		this.listMap = listMap;
	}

	public void setData(List<Map<String, String>> listMap) {
		this.listMap = listMap;
		notifyDataSetChanged();
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
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(R.layout.withdrawal_list_2_item, null);
			holder.z_item_all = (LinearLayout) convertView.findViewById(R.id.z_item_all);
			holder.tv_item_money_desc = (TextView) convertView.findViewById(R.id.tv_item_money_desc);
			holder.tv_item_money = (TextView) convertView.findViewById(R.id.tv_item_money);
			holder.tv_item_time = (TextView) convertView.findViewById(R.id.tv_item_time);
			holder.tv_item_state = (TextView) convertView.findViewById(R.id.tv_item_state);
			convertView.setTag(holder);
			StyleUtils.setLayoutStyle(context, convertView);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Map<String, String> map = listMap.get(position);

		holder.tv_item_money.setText(map.get("amount") + "元");
		String time = TimeUtil.getData("yyyy-MM-dd HH:mm:ss", map.get("time"));
		holder.tv_item_time.setText(time);
		holder.tv_item_state.setText(map.get("status"));
		
		return convertView;
	}

	private class ViewHolder {
		LinearLayout z_item_all;
		TextView tv_item_money_desc;
		TextView tv_item_money;
		TextView tv_item_time;
		TextView tv_item_state;
	}
}
