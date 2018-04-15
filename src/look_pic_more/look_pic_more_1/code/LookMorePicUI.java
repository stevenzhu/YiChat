package look_pic_more.look_pic_more_1.code;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import chat.chat_1.code.ForwardMessageUI;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.shorigo.BaseUI;
import com.shorigo.MyApplication;
import com.shorigo.utils.Constants;
import com.shorigo.utils.DialogUtil;
import com.shorigo.utils.DialogUtil.DialogClickCallBack;
import com.shorigo.utils.FileUtils;
import com.shorigo.view.CircleProgressView;
import com.shorigo.view.ViewPagerFixed;
import com.shorigo.view.photoview.PhotoViewAttacher;
import com.shorigo.view.photoview.PhotoViewAttacher.OnPhotoTapListener;
import com.shorigo.yichat.R;

/**
 * 查看大图
 * 
 * @author peidongxu
 * 
 */
public class LookMorePicUI extends BaseUI implements OnPageChangeListener, DialogClickCallBack {
	private LinearLayout llDot;
	// ViewPager用于显示图片作品的控件
	private ViewPagerFixed vp;
	// 记录前一个点的位置
	private int prePosition = 0;
	// 存放数据源的集合
	private List<View> mList;
	private int position;

	private ArrayList<String> imgList;
	private LookMorePicAdapter lookMorePicAdapter;
	private PhotoViewAttacher mAttacher;

	private DialogUtil dialogUtil;
	private Bitmap[] arrBitmap;
	private BitmapUtils bitmapUtils;

	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.look_more_pic_1);
	}

	@Override
	protected void findView_AddListener() {
		llDot = (LinearLayout) findViewById(R.id.lldot_look_more_pic);
		vp = (ViewPagerFixed) findViewById(R.id.vp_look_more_pic);
	}

	@Override
	protected void prepareData() {
		Intent intent = getIntent();
		position = intent.getIntExtra("position", 1);
		imgList = intent.getStringArrayListExtra("img");

		dialogUtil = new DialogUtil();
		dialogUtil.setCallBack(this);
		dialogUtil.setDialogWidth(Constants.width, true);

		initView();
	}

	private void initView() {
		if (imgList == null || imgList.size() <= 0) {
			return;
		}
		mList = new ArrayList<View>();
		arrBitmap = new Bitmap[imgList.size()];
		for (int i = 0; i < imgList.size(); i++) {
			View child_view = View.inflate(this, R.layout.look_more_pic_1_item, null);
			final ImageView iv_pic = (ImageView) child_view.findViewById(R.id.iv_look_more_pic);
			final CircleProgressView pb_look_pic = (CircleProgressView) child_view.findViewById(R.id.pb_look_more_pic);
			bitmapUtils = new BitmapUtils(this);
			bitmapUtils.display(iv_pic, imgList.get(i), new BitmapLoadCallBack<View>() {
				@Override
				public void onLoadCompleted(View arg0, String arg1, Bitmap bm, BitmapDisplayConfig arg3, BitmapLoadFrom arg4) {
					pb_look_pic.setVisibility(View.GONE);
					iv_pic.setImageBitmap(bm);
					mAttacher = new PhotoViewAttacher(iv_pic);
					mAttacher.setScaleType(ScaleType.FIT_CENTER);
					mAttacher.setOnPhotoTapListener(new OnPhotoTapListener() {
						@Override
						public void onPhotoTap(View view, float x, float y) {
							back();
						}
					});
					mAttacher.setOnLongClickListener(new OnLongClickListener() {

						@Override
						public boolean onLongClick(View arg0) {
							dialogUtil.showLookImgDialog(LookMorePicUI.this);
							return false;
						}

					});
				}

				@Override
				public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
				}

				@Override
				public void onLoading(View container, String uri, BitmapDisplayConfig config, final long total, final long current) {
					super.onLoading(container, uri, config, total, current);
					pb_look_pic.setVisibility(View.VISIBLE);
					new Handler().post(new Runnable() {

						@Override
						public void run() {
							pb_look_pic.setProgressNotInUiThread((int) ((float) current / total * 100));
						}
					});
				}
			});
			mList.add(child_view);

			// 给线性布局加小圆点
			View view = new View(this);
			// 设置小圆点的宽高
			LayoutParams params = new LayoutParams(20, 20);
			// 设置圆点的间距
			params.leftMargin = 10;
			view.setLayoutParams(params);
			// 设置控件的默认状态
			view.setEnabled(false);
			// 利用背景选择器来作为背景图片
			view.setBackgroundResource(R.drawable.dots_selector);
			// 将小圆点加到线性布局中
			llDot.addView(view);
		}
		lookMorePicAdapter = new LookMorePicAdapter();
		lookMorePicAdapter.setData(mList, imgList);
		vp.setAdapter(lookMorePicAdapter);
		// 设置第一个小圆点为选中状态
		llDot.getChildAt(position).setEnabled(true);
		prePosition = position;
		vp.setCurrentItem(position);
		// ViewPager改变的监听
		vp.setOnPageChangeListener(this);
	}

	/**************************** ViewPager页面的监听 ************************************/
	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int position) {
		// 更改当前点和前一个点的状态
		llDot.getChildAt(prePosition).setEnabled(false);
		llDot.getChildAt(position).setEnabled(true);
		// 改变前一个点的索引值
		prePosition = position;
	}

	@Override
	protected void onMyClick(View v) {
	}

	@Override
	public void callBack(View v) {
		switch (v.getId()) {
		case R.id.tv_dialog_look_img_send:
			// 发送给朋友
			dialogUtil.dismissDialog();
			// 转发消息
			Intent intent = new Intent(this, ForwardMessageUI.class);
			intent.putExtra("img", imgList.get(prePosition));
			startActivity(intent);
			break;
		case R.id.tv_dialog_look_img_save:
			// 保存
			dialogUtil.dismissDialog();
			saveImg();
			break;
		default:
			break;
		}
	}

	/**
	 * 保存图片
	 */
	private void saveImg() {
		ImageView imageView = new ImageView(this);
		bitmapUtils.display(imageView, imgList.get(prePosition), new BitmapLoadCallBack<View>() {
			@Override
			public void onLoadCompleted(View arg0, String arg1, Bitmap bm, BitmapDisplayConfig arg3, BitmapLoadFrom arg4) {
				FileUtils.saveImageToGallery(LookMorePicUI.this, bm);
				MyApplication.getInstance().showToast("图片已存至手机相册");
			}

			@Override
			public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
			}
		});
	}

	@Override
	protected void back() {
		finish();
	}
}
