package imageselector.imageselector_1.code;

import java.io.File;
import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.shorigo.FBaseUI;
import com.shorigo.utils.Constants;
import com.shorigo.yichat.R;

import yichat.util.ZUtil;

public class ImageSelectorActivity extends FBaseUI implements ImageSelectorFragment.Callback {

	public static final String EXTRA_RESULT = "select_result";
	private ArrayList<String> pathList = new ArrayList<String>();
	private ImageConfig imageConfig;
	private TextView tv_right;
	private File file;

	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.imageselector_activity);
	}

	@Override
	protected void findView_AddListener() {
		tv_right = (TextView) findViewById(R.id.tv_right);
		tv_right.setOnClickListener(this);
	}

	@Override
	protected void prepareData() {
		setTitle("选择图片");

		imageConfig = ImageSelector.getImageConfig();

		getSupportFragmentManager().beginTransaction().add(R.id.image_grid, Fragment.instantiate(this, ImageSelectorFragment.class.getName(), null)).commit();

		pathList = imageConfig.getPathList();

		if (imageConfig.isMutiSelect()) {
			// 多选
			if (pathList == null || pathList.size() <= 0) {
				tv_right.setText("完成");
			} else {
				tv_right.setText("完成" + "(" + pathList.size() + "/" + imageConfig.getMaxSize() + ")");
			}
			tv_right.setVisibility(View.VISIBLE);
		}

	}

	@Override
	protected void onMyClick(View v) {
		switch (v.getId()) {
		case R.id.tv_right:
			if (pathList != null && pathList.size() > 0) {
				Intent data = new Intent();
				data.putStringArrayListExtra(EXTRA_RESULT, pathList);
				setResult(imageConfig.getRequestCode(), data);
				finish();
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onSingleImageSelected(String path) {
		if (imageConfig.isCrop()) {
			crop(path, imageConfig.getAspectX(), imageConfig.getAspectY(), imageConfig.getOutputX(), imageConfig.getOutputY());
		} else {
			Intent data = new Intent();
			pathList.add(path);
			data.putStringArrayListExtra(EXTRA_RESULT, pathList);
			setResult(RESULT_OK, data);
			finish();
		}
	}

	@Override
	public void onImageSelected(String path) {
		if (!pathList.contains(path)) {
			pathList.add(path);
		}
		if (pathList.size() > 0) {
			tv_right.setText("完成" + "(" + pathList.size() + "/" + imageConfig.getMaxSize() + ")");
		}
	}

	@Override
	public void onImageUnselected(String path) {
		if (pathList.contains(path)) {
			pathList.remove(path);
			tv_right.setText("完成" + "(" + pathList.size() + "/" + imageConfig.getMaxSize() + ")");
		} else {
			tv_right.setText("完成" + "(" + pathList.size() + "/" + imageConfig.getMaxSize() + ")");
		}
		if (pathList.size() == 0) {
			tv_right.setText("完成");
		}
	}

	@Override
	public void onCameraShot(File imageFile) {
//		if (imageFile != null) {
//			Intent data = new Intent();
//			pathList.add(imageFile.getAbsolutePath());
//			data.putStringArrayListExtra(EXTRA_RESULT, pathList);
//			setResult(RESULT_OK, data);
//			finish();
//		}
		if (imageFile != null) {
			if (imageConfig.isCrop()) {
				crop(imageFile.getAbsolutePath(), imageConfig.getAspectX(), imageConfig.getAspectY(), imageConfig.getOutputX(), imageConfig.getOutputY());
			} else {
				String path=imageFile.getAbsolutePath();
				Intent data = new Intent();
				pathList.add(path);
				data.putStringArrayListExtra(EXTRA_RESULT, pathList);
				setResult(RESULT_OK, data);
				finish();
			}
		}
	}

	private void crop(String imagePath, int aspectX, int aspectY, int outputX, int outputY) {
		String path = Constants.path + Constants._image;
		long currentTimeMillis = System.currentTimeMillis();
		String fileName = currentTimeMillis + ".jpeg";
		file = new File(path, fileName);
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
		} catch (Exception e) {
		}

		Intent intent = new Intent("com.android.camera.action.CROP");
		// intent.setDataAndType(Uri.fromFile(new File(imagePath)), "image/*");
		intent.setDataAndType(Uri.fromFile(new File(imagePath)), "image/jpeg");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", aspectX);
		intent.putExtra("aspectY", aspectY);
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("scale", true);
		intent.putExtra("return-data", false);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true);
		startActivityForResult(intent, ImageSelector.IMAGE_CROP_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ImageSelector.IMAGE_CROP_CODE && data != null) {
			// 拿到剪切数据
			if (!imageConfig.isMutiSelect()) {
				pathList = new ArrayList<String>();
			}
			String path = file.getPath();
			pathList.add(path);
			Intent intent = new Intent();
			intent.putStringArrayListExtra(EXTRA_RESULT, pathList);
			setResult(RESULT_OK, intent);
			finish();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void back() {
		setResult(RESULT_CANCELED);
		finish();
	}
}