package video_choose.video_choose_1.code;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shorigo.utils.BitmapHelp;
import com.shorigo.yichat.R;

public class VideoChooseAdapter extends BaseAdapter {
	private Context context;
	private List<VideoFileBean> listVideoFile;

	public VideoChooseAdapter(Context context, List<VideoFileBean> data) {
		this.context = context;
		this.listVideoFile = data;
	}

	public void setData(List<VideoFileBean> data) {
		this.listVideoFile = data;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (listVideoFile == null) {
			return 0;
		}
		return listVideoFile.size();
	}

	@Override
	public Object getItem(int position) {
		return listVideoFile.get(position);
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
			convertView = LayoutInflater.from(context).inflate(R.layout.video_choose_1_item, null);
			holder.thumb = (ImageView) convertView.findViewById(R.id.iv_thumb);
			holder.duration = (TextView) convertView.findViewById(R.id.tv_duration);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final VideoFileBean videoFileBean = listVideoFile.get(position);

		BitmapHelp.loadImg(context, holder.thumb, videoFileBean.getThumbPath());
		holder.duration.setText(videoFileBean.getDuration());

		return convertView;
	}

	private class ViewHolder {
		ImageView thumb;
		TextView duration;
	}

}
