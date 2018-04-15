package look_pic_more.look_pic_more_1.code;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * 图片查看器Adapter
 * 
 * @author peidongxu
 * 
 */
public class LookMorePicAdapter extends PagerAdapter {

	// 图片展示数据
	private List<View> mList;

	// 设置数据
	public void setData(List<View> list, List<String> urlList) {
		this.mList = list;
		notifyDataSetChanged();// 数据源改变通知
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object obj) {
		return view == obj;
	}

	@Override
	public Object instantiateItem(ViewGroup container, final int position) {

		View view = mList.get(position);
		try {
			container.addView(view, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return view;

	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(mList.get(position));
	}

}
