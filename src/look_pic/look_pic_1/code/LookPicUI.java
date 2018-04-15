package look_pic.look_pic_1.code;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
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
import com.shorigo.utils.Utils;
import com.shorigo.view.CircleProgressView;
import com.shorigo.view.photoview.PhotoViewAttacher;
import com.shorigo.view.photoview.PhotoViewAttacher.OnPhotoTapListener;
import com.shorigo.yichat.R;

/**
 * 查看大图
 * 
 * @author peidongxu
 * 
 */
public class LookPicUI extends BaseUI implements DialogClickCallBack {
	private ImageView ivPictrue;
	private String imgUrl;
	private PhotoViewAttacher mAttacher;
	private CircleProgressView pb_look_pic;

	private DialogUtil dialogUtil;
	private Bitmap mBitmap;

	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.look_pic_1);
	}

	@Override
	protected void findView_AddListener() {
		ivPictrue = (ImageView) findViewById(R.id.iv_look_pic);
		pb_look_pic = (CircleProgressView) findViewById(R.id.pb_look_pic);
	}

	@Override
	protected void prepareData() {

		Intent intent = getIntent();
		imgUrl = intent.getStringExtra("img");

		dialogUtil = new DialogUtil();
		dialogUtil.setCallBack(this);
		dialogUtil.setDialogWidth(Constants.width, true);

		if (!Utils.isEmity(imgUrl)) {
			BitmapUtils bitmapUtils = new BitmapUtils(this);
			bitmapUtils.display(ivPictrue, imgUrl, new BitmapLoadCallBack<View>() {
				@Override
				public void onLoadCompleted(View arg0, String arg1, Bitmap bm, BitmapDisplayConfig arg3, BitmapLoadFrom arg4) {
					pb_look_pic.setVisibility(View.GONE);
					mBitmap = bm;
					ivPictrue.setImageBitmap(mBitmap);
					mAttacher = new PhotoViewAttacher(ivPictrue);
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
							dialogUtil.showLookImgDialog(LookPicUI.this);
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
		}
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
			intent.putExtra("img", imgUrl);
			startActivity(intent);
			break;
		case R.id.tv_dialog_look_img_save:
			// 保存
			dialogUtil.dismissDialog();
			FileUtils.saveImageToGallery(this, mBitmap);
			MyApplication.getInstance().showToast("图片已存至手机相册");
			break;
		default:
			break;
		}
	}

	@Override
	protected void back() {
		finish();
	}
}
