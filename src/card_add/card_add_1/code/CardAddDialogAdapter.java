package card_add.card_add_1.code;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shorigo.yichat.R;

/**
 * 选择银行显示框adapter
 * 
 * @author peidongxu
 * 
 */
public class CardAddDialogAdapter extends BaseAdapter {
	private Context context;
	private List<Map<String, String>> listBankMap;

	public CardAddDialogAdapter(Context context, List<Map<String, String>> listBankMap) {
		this.context = context;
		this.listBankMap = listBankMap;
	}

	@Override
	public int getCount() {
		if (listBankMap == null)
			return 0;
		return listBankMap.size();
	}

	@Override
	public Object getItem(int position) {
		return listBankMap.get(position);
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
			convertView = View.inflate(context, R.layout.card_add_1_bank_item, null);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_dialog_bank_item_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Map<String, String> map = listBankMap.get(position);

		holder.tv_name.setText(map.get("name"));

		return convertView;
	}

	private class ViewHolder {
		TextView tv_name;
	}

}
