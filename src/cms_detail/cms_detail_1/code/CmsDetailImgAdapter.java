package cms_detail.cms_detail_1.code;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.shorigo.utils.BitmapHelp;
import com.shorigo.yichat.R;

public class CmsDetailImgAdapter extends BaseAdapter {
	private Context context;
	private List<String> listImg;

	public CmsDetailImgAdapter(Context context, List<String> listImg) {
		this.context = context;
		this.listImg = listImg;
	}

	public void setData(List<String> listImg) {
		this.listImg = listImg;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (listImg == null) {
			return 0;
		}
		return listImg.size();
	}

	@Override
	public Object getItem(int position) {
		return listImg.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.cms_detail_1_item_img, null);
			holder.iv_item_img = (ImageView) convertView.findViewById(R.id.iv_item_img);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String img_url = listImg.get(position);

		BitmapHelp.loadImg(context, holder.iv_item_img, img_url, R.drawable.default_img);

		return convertView;
	}

	public class ViewHolder {
		ImageView iv_item_img;
	}

}
