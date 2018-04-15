package card_list_del.card_list_del_1.code;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import bean.CardBean;

import com.shorigo.custom_style.StyleUtils;
import com.shorigo.utils.Utils;
import com.shorigo.yichat.R;

/**
 * 银行卡删除列表adapter
 * 
 * @author peidongxu
 * 
 */
public class CardDelListAdapter extends BaseAdapter {

	private Context context;
	private List<CardBean> listCardBean;

	public CardDelListAdapter(Context context, List<CardBean> listCardBean) {
		this.context = context;
		this.listCardBean = listCardBean;
	}

	public void setData(List<CardBean> listCardBean) {
		this.listCardBean = listCardBean;
		notifyDataSetChanged();
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
	public View getView(final int position, View convertView, ViewGroup arg2) {

		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.card_list_del_1_item, null);
			holder.z_item_all = (LinearLayout) convertView.findViewById(R.id.z_item_all);
			holder.iv_item_state = (ImageView) convertView.findViewById(R.id.iv_item_state);
			holder.tv_item_card_name = (TextView) convertView.findViewById(R.id.tv_item_card_name);
			holder.tv_item_card_khr_name = (TextView) convertView.findViewById(R.id.tv_item_card_khr_name);
			holder.tv_item_card_num = (TextView) convertView.findViewById(R.id.tv_item_card_num);
			convertView.setTag(holder);
			StyleUtils.setLayoutStyle(context, convertView);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final CardBean cardBean = listCardBean.get(position);

		holder.tv_item_card_name.setText(cardBean.getBankMap().get("name"));
		holder.tv_item_card_khr_name.setText(cardBean.getUser_name());

		String card = cardBean.getCard_code();
		if (card.indexOf(" ") != -1) {
			holder.tv_item_card_num.setText(card);
		} else {
			String cha = "**** ";
			String content = "";
			if (!Utils.isEmity(card)) {
				int length = card.length();
				int num = length / 4;
				int tail = length - num * 4;
				if (tail <= 0) {
					for (int i = 0; i < num - 1; i++) {
						content = content + cha;
					}
					String substring = card.substring((num - 1) * 4, length);
					holder.tv_item_card_num.setText(content + substring);
				} else {
					for (int i = 0; i < num - 1; i++) {
						content = content + cha;
					}
					String substring = card.substring(length - tail, length);
					if (tail == 1) {
						String substring2 = card.substring(length - tail - 3, length - tail);
						content = content + "*" + substring2 + " ";
					} else if (tail == 2) {
						String substring2 = card.substring(length - tail - 2, length - tail);
						content = content + "**" + substring2 + " ";
					} else {
						String substring2 = card.substring(length - tail - 1, length - tail);
						content = content + "***" + substring2 + " ";
					}
					holder.tv_item_card_num.setText(content + substring);
				}
			}
		}

		holder.iv_item_state.setSelected(cardBean.isCheck());
		holder.iv_item_state.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				boolean checked = !holder.iv_item_state.isSelected();
				holder.iv_item_state.setSelected(checked);
				cardBean.setCheck(checked);
			}
		});
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				boolean checked = !holder.iv_item_state.isSelected();
				holder.iv_item_state.setSelected(checked);
				cardBean.setCheck(checked);
			}
		});

		return convertView;
	}

	private class ViewHolder {
		LinearLayout z_item_all;
		ImageView iv_item_state;
		TextView tv_item_card_name;
		TextView tv_item_card_khr_name;
		TextView tv_item_card_num;
	}

}
