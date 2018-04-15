package withdrawal.withdrawal_1.code;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import bean.CardBean;

import com.shorigo.yichat.R;

/**
 * 选择银行显示框adapter
 * 
 * @author peidongxu
 * 
 */
public class WithdrawalDialogAdapter extends BaseAdapter {
	private Context context;
	private List<CardBean> listCardBean;

	public WithdrawalDialogAdapter(Context context, List<CardBean> listCardBean) {
		this.context = context;
		this.listCardBean = listCardBean;
	}

	@Override
	public int getCount() {
		if (listCardBean == null)
			return 0;
		return listCardBean.size();
	}

	@Override
	public Object getItem(int position) {
		return listCardBean.get(position);
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
			convertView = View.inflate(context, R.layout.withdrawal_1_bank_item, null);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_dialog_bank_item_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		CardBean cardBean = listCardBean.get(position);

		holder.tv_name.setText(cardBean.getBankMap().get("name"));

		return convertView;
	}

	private class ViewHolder {
		TextView tv_name;
	}

}
