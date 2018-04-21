package update_avatar.update_avatar_1.code;

import imageselector.imageselector_1.code.ImageConfig;
import imageselector.imageselector_1.code.ImageSelector;
import imageselector.imageselector_1.code.ImageSelectorActivity;
import imageselector.imageselector_1.code.XutilsLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONObject;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import bean.RequestReturnBean;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shorigo.BaseUI;
import com.shorigo.MyApplication;
import com.shorigo.http.HttpUtil;
import com.shorigo.utils.BitmapHelp;
import com.shorigo.utils.Constants;
import com.shorigo.utils.MyConfig;
import com.shorigo.yichat.R;

/**
 * 修改头像
 * 
 * @author peidongxu
 * 
 */
public class UpdateAvatarUI extends BaseUI {

	private ImageView iv_avatar;
	public static final int REQUEST_CODE = 1;
	private ArrayList<String> pathList = new ArrayList<String>();
	private Map<String, String> userMap;

	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.update_avatar_1);
	}

	@Override
	protected void findView_AddListener() {
		iv_avatar = (ImageView) findViewById(R.id.iv_avatar);
		iv_avatar.setOnClickListener(this);

	}

	@Override
	protected void prepareData() {
		setTitle("修改头像");
		setRightButton("确定");

		userMap = MyConfig.getUserInfo(this);
		if (userMap != null) {
			BitmapHelp.loadImg(this, iv_avatar, userMap.get("avatar"), R.drawable.default_avatar_angle);
		}
	}

	/**
	 * 设置用户信息
	 */
	private void setUserInfo() {
		String url = HttpUtil.getUrl("/user/updateUserInfo");
		Map<String, String> map = new HashMap<String, String>();
		map.put("access_token", MyConfig.getToken(this));
		RequestParams params = new RequestParams();
		if (pathList != null && pathList.size() > 0) {
			try {
				params.put("avatar", new File(pathList.get(0)));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		HttpUtil.post(this, url, map, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				String str=response.toString();
				RequestReturnBean returnBean = UpdateAvatarJson.setUserInfo(response.toString());
				if (HttpUtil.isSuccess(UpdateAvatarUI.this, returnBean.getCode())) {
					if (userMap != null) {
						userMap.put("avatar", pathList.get(0));
						MyConfig.saveUserInfo(UpdateAvatarUI.this, userMap);
						back();
					}
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}

	@Override
	protected void onMyClick(View v) {
		switch (v.getId()) {
		case R.id.iv_avatar:
			ImageConfig imageConfig = new ImageConfig.Builder(new XutilsLoader()).singleSelect().crop().pathList(pathList).filePath(Constants.path + Constants._image).showCamera().requestCode(REQUEST_CODE).build();
			ImageSelector.open(UpdateAvatarUI.this, imageConfig);
			break;
		case R.id.z_right:
			if (pathList != null && pathList.size() > 0) {
				setUserInfo();
			} else {
				MyApplication.getInstance().showToast("请上传图片");
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
			pathList = data.getStringArrayListExtra(ImageSelectorActivity.EXTRA_RESULT);
			if (pathList != null && pathList.size() > 0) {
				BitmapHelp.loadImg(this, iv_avatar, pathList.get(0), R.drawable.default_avatar_angle);
			}
		}
	}

	@Override
	protected void back() {
		finish();
	}

}
