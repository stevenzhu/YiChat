package cms_detail_img.cms_detail_img_1.code;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import bean.CmsBean;

import com.shorigo.BaseUI;
import com.shorigo.utils.BitmapHelp;
import com.shorigo.yichat.R;

public class CmsDetailImgUI extends BaseUI {
	private List<Map<String, String>> listImg;
	private List<View> listViews; // 滑动的图片集
	private ViewPager vp_img;
	private CmsBean cmsBean;

	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.cms_detail_img_1);
	}

	@Override
	protected void findView_AddListener() {
		vp_img = (ViewPager) findViewById(R.id.vp_img);

	}

	@Override
	protected void prepareData() {

		Intent intent = getIntent();
		cmsBean = (CmsBean) intent.getSerializableExtra("bean");
		if (cmsBean != null) {
			listImg = cmsBean.getListImgMap();
			initView();
		}
	}

	/**
	 * 初始化轮播图
	 */
	private void initView() {
		listViews = new ArrayList<View>();
		View view;
		ImageView iv_item_img;
		TextView tv_item_name;
		String content = "";
		if (listImg != null && listImg.size() > 0) {
			for (int i = 0; i < listImg.size(); i++) {
				view = View.inflate(this, R.layout.cms_detail_img_1_item, null);
				iv_item_img = (ImageView) view.findViewById(R.id.iv_item_img);
				tv_item_name = (TextView) view.findViewById(R.id.tv_item_name);
				content = listImg.get(i).get("content");
				content = String.valueOf(i + 1) + "/" + listImg.size() + "    " + content;
				tv_item_name.setText(content);
				BitmapHelp.loadImg(this, iv_item_img, listImg.get(i).get("img"), R.drawable.default_img);
				listViews.add(view);
			}
		} else {
			for (int i = 0; i < 3; i++) {
				view = View.inflate(this, R.layout.cms_detail_img_1_item, null);
				iv_item_img = (ImageView) view.findViewById(R.id.iv_item_img);
				tv_item_name = (TextView) view.findViewById(R.id.tv_item_name);
				content = String.valueOf(i + 1) + "/" + listImg.size() + "    " + content;
				tv_item_name.setText(content);
				BitmapHelp.loadImg(this, iv_item_img, R.drawable.default_img);
				listViews.add(view);
			}
		}

		// 填充数据
		vp_img.setAdapter(new PagerAdapter() {

			@Override
			public int getCount() {
				return listImg.size();
			}

			@Override
			public Object instantiateItem(ViewGroup container, final int position) {
				View view = listViews.get(position);
				try {
					container.addView(view, 0);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return view;
			}

			@Override
			public void destroyItem(ViewGroup container, int position, Object object) {
			}

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public void restoreState(Parcelable arg0, ClassLoader arg1) {
			}

			@Override
			public Parcelable saveState() {
				return null;
			}

			@Override
			public void startUpdate(View arg0) {
			}

			@Override
			public void finishUpdate(View arg0) {
			}
		});
	}

	@Override
	protected void onMyClick(View v) {
	}

	@Override
	protected void back() {
		finish();
	}

}
