package cms_launch.cms_launch_1.code;

import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.shorigo.utils.BitmapHelp;
import com.shorigo.yichat.R;

public class CmsLaunchImgAdapter extends BaseAdapter {
	private Context context;
	private List<String> mLists;
	private Handler mHandler;

	public CmsLaunchImgAdapter(Context context, List<String> mLists, Handler handler) {
		this.mLists = mLists;
		this.context = context;
		this.mHandler = handler;
	}

	public void setData(List<String> mLists) {
		this.mLists = mLists;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mLists == null ? 0 : mLists.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mLists == null ? null : mLists.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	public View getView(final int position, View view, ViewGroup group) {
		ViewHolder holder;
		if (view == null) {
			holder = new ViewHolder();
			view = LayoutInflater.from(context).inflate(R.layout.cms_launch_1_img_item, null);
			holder.iv_item_img = (ImageView) view.findViewById(R.id.iv_item_img);
			holder.iv_item_del = (ImageView) view.findViewById(R.id.iv_item_img_del);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		String info = mLists.get(position);
		if (info != null) {
			if (info.equals("default")) {
				BitmapHelp.loadImg(context, holder.iv_item_img, R.drawable.circle_launch_1_icon_addimg);
				holder.iv_item_del.setVisibility(View.INVISIBLE);
			} else {
				holder.iv_item_del.setVisibility(View.VISIBLE);
				BitmapHelp.loadImg(context, holder.iv_item_img, info);
			}

			holder.iv_item_del.setTag(position);
			holder.iv_item_del.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Message msg = new Message();
					msg.what = 1;
					msg.arg1 = (Integer) arg0.getTag();
					mHandler.sendMessage(msg);
				}
			});
		}
		return view;
	}

	private class ViewHolder {
		ImageView iv_item_img;
		ImageView iv_item_del;
	}
}
