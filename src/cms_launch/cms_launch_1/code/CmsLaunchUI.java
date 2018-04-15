package cms_launch.cms_launch_1.code;

import imageselector.imageselector_1.code.ImageConfig;
import imageselector.imageselector_1.code.ImageSelector;
import imageselector.imageselector_1.code.ImageSelectorActivity;
import imageselector.imageselector_1.code.XutilsLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import look_pic.look_pic_1.code.LookPicUI;
import look_pic_more.look_pic_more_1.code.LookMorePicUI;

import org.apache.http.Header;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import bean.RequestReturnBean;
import yichat.util.ZUtil;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shorigo.BaseUI;
import com.shorigo.MyApplication;
import com.shorigo.custom_style.StyleUtils;
import com.shorigo.http.HttpUtil;
import com.shorigo.utils.Constants;
import com.shorigo.utils.MyConfig;
import com.shorigo.utils.Utils;
import com.shorigo.view.MyGridView;
import com.shorigo.yichat.R;

/**
 * 发布图文
 * 
 * @author peidongxu
 * 
 */
public class CmsLaunchUI extends BaseUI implements OnItemClickListener, OnTouchListener {
	// 主题内容
	private EditText et_title, et_content;
	private String title, content;
	// 图片和表情切换
	private ImageView iv_biaoqing;
	// 表情数据
	private List<String> listRes;
	private ViewPager vp_expression;
	// 选择图片
	public static final int REQUEST_CODE = 1000;
	private ArrayList<String> listImg;
	private GridView gv_img;
	private CmsLaunchImgAdapter adapter;
	// 键盘管理
	private InputMethodManager inputMethodManager;
	private ProgressDialog progressDialog;

	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.cms_launch_1);
	}

	@Override
	protected void findView_AddListener() {
		inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		et_title = (EditText) findViewById(R.id.et_title);
		et_title.setOnTouchListener(this);
		et_content = (EditText) findViewById(R.id.et_content);
		et_content.setOnTouchListener(this);

		iv_biaoqing = (ImageView) findViewById(R.id.iv_biaoqing);
		iv_biaoqing.setOnClickListener(this);
		StyleUtils.setTabBg(this, iv_biaoqing, new int[] { R.drawable.circle_launch_1_icon_keyboard, R.drawable.circle_launch_1_icon_biaoqing });

		gv_img = (GridView) findViewById(R.id.gv_circle_launch_img);
		gv_img.setOnItemClickListener(this);

		vp_expression = (ViewPager) findViewById(R.id.vp_circle_launch);

	}

	@Override
	protected void prepareData() {
		setTitle("发布文章");
		setRightButton("发布");

		iv_biaoqing.setSelected(false);

		// 初始化图片
		listImg = new ArrayList<String>();
		listImg.add("default");
		adapter = new CmsLaunchImgAdapter(this, listImg, handler);
		gv_img.setAdapter(adapter);

		// 初始化表情
		listRes = getExpressionRes(35);
		List<View> views = new ArrayList<View>();
		View gv1 = getGridChildView(1);
		View gv2 = getGridChildView(2);
		views.add(gv1);
		views.add(gv2);
		vp_expression.setAdapter(new ExpressionPagerAdapter(views));
	}

	@Override
	protected void onMyClick(View v) {
		switch (v.getId()) {
		case R.id.z_right:
			// 发布
			title = et_title.getText().toString();
			content = et_content.getText().toString();
			if (Utils.isEmity(title)) {
				MyApplication.getInstance().showToast("请输入标题");
				return;
			}
			if (listImg == null || listImg.size() <= 0) {
				MyApplication.getInstance().showToast("请上传图片");
				return;
			}
			if (progressDialog == null) {
				progressDialog = new ProgressDialog(this);
			}
			progressDialog.setMessage("正在提交中...");
			progressDialog.show();
			sendDynamic();
			break;
		case R.id.iv_biaoqing:
			// 表情
			if (iv_biaoqing.isSelected()) {
				// 切换键盘模式
				iv_biaoqing.setSelected(false);
				vp_expression.setVisibility(View.GONE);
				showKeyboard();
			} else {
				// 切换表情模式
				iv_biaoqing.setSelected(true);
				// 隐藏键盘
				hideKeyboard();
				vp_expression.setVisibility(View.VISIBLE);
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUEST_CODE:
			if (data == null) {
				return;
			}
			ArrayList<String> listPath = data.getStringArrayListExtra(ImageSelectorActivity.EXTRA_RESULT);
			listImg.clear();
			listImg.addAll(listPath);
			listImg.add("default");
			adapter.setData(listImg);
			break;
		default:
			break;
		}

	}

	/**
	 * 发布主题
	 */
	private void sendDynamic() {
		String url = HttpUtil.getUrl("/dynamic/send");
		Map<String, String> map = new HashMap<String, String>();
		map.put("access_token", MyConfig.getToken(this));
		map.put("title", title);
		map.put("content", content);
		RequestParams params = new RequestParams();

		if (listImg != null && listImg.size() > 1) {
			// 有图片上传的时候
			String originalPath;
			try {
				String key;
				for (int i = 0; i < listImg.size(); i++) {
					originalPath = listImg.get(i);
					if (!"default".equals(originalPath)) {
						//压缩
						String pathScaled=ZUtil.scaleImg(originalPath);
						key = "imgs[" + i + "]";
						params.put(key, new File(pathScaled));
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		HttpUtil.post(this, url, map, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = CmsLaunchJson.analysis(response.toString());
				progressDialog.dismiss();
				if (HttpUtil.isSuccess(CmsLaunchUI.this, returnBean.getCode())) {
					back();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
				progressDialog.dismiss();
			}
		});
	}

	/**
	 * 获取表情的gridview的子view
	 * 
	 * @param i
	 * @return
	 */
	private View getGridChildView(int i) {
		View view = View.inflate(this, R.layout.cms_launch_1_expression_gridview, null);
		MyGridView gv = (MyGridView) view.findViewById(R.id.gridview);
		List<String> list = new ArrayList<String>();
		if (i == 1) {
			List<String> list1 = listRes.subList(0, 20);
			list.addAll(list1);
		} else if (i == 2) {
			list.addAll(listRes.subList(20, listRes.size()));
		}
		list.add("delete_expression");
		final ExpressionAdapter expressionAdapter = new ExpressionAdapter(this, 1, list);
		gv.setAdapter(expressionAdapter);
		gv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String filename = expressionAdapter.getItem(position);
				try {
					// 文字输入框可见时，才可输入表情
					// 按住说话可见，不让输入表情
					if (filename != "delete_expression") { // 不是删除键，显示表情
						// 这里用的反射，所以混淆的时候不要混淆SmileUtils这个类
						Class clz = SmileUtils.class;
						Field field = clz.getField(filename);
						et_content.append(SmileUtils.getSmiledText(CmsLaunchUI.this, (String) field.get(null)));
					} else { // 删除文字或者表情
						if (!TextUtils.isEmpty(et_content.getText())) {
							int selectionStart = et_content.getSelectionStart();// 获取光标的位置
							if (selectionStart > 0) {
								String body = et_content.getText().toString();
								String tempStr = body.substring(0, selectionStart);
								int i = tempStr.lastIndexOf("[");// 获取最后一个表情的位置
								if (i != -1) {
									CharSequence cs = tempStr.substring(i, selectionStart);
									if (SmileUtils.containsKey(cs.toString()))
										et_content.getEditableText().delete(i, selectionStart);
									else
										et_content.getEditableText().delete(selectionStart - 1, selectionStart);
								} else {
									et_content.getEditableText().delete(selectionStart - 1, selectionStart);
								}
							}
						}
					}
				} catch (Exception e) {
				}
			}
		});
		return view;
	}

	public List<String> getExpressionRes(int getSum) {
		List<String> reslist = new ArrayList<String>();
		for (int x = 1; x <= getSum; x++) {
			String filename = "ee_" + x;
			reslist.add(filename);
		}
		return reslist;
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				// 从选的照片中删除其中一张照片
				if (listImg != null && listImg.size() > msg.arg1) {
					listImg.remove(msg.arg1);
				}
				adapter.setData(listImg);
				break;
			default:
				break;
			}
		};
	};

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		if (position == listImg.size() - 1) {
			// 如果是最后一个
			if (listImg != null && listImg.size() >= 9) {
				MyApplication.getInstance().showToast("只能选择9张图片");
			} else {
				ArrayList<String> listTemp = new ArrayList<String>();
				listTemp.addAll(listImg);
				listTemp.remove(listTemp.size() - 1);
				ImageConfig imageConfig = new ImageConfig.Builder(new XutilsLoader()).mutiSelect().mutiSelectMaxSize(9).showCamera().pathList(listTemp).filePath(Constants.path + Constants._image).requestCode(REQUEST_CODE).build();
				ImageSelector.open(this, imageConfig);
			}
		} else {
			List<String> lists = new ArrayList<String>();
			lists.addAll(listImg);
			lists.remove(lists.size() - 1);

			ArrayList<String> listImg = new ArrayList<String>();

			for (int i = 0; i < lists.size(); i++) {
				listImg.add(lists.get(i));
			}

			if (listImg.size() == 1) {// 单张图片
				Intent intent = new Intent(this, LookPicUI.class);
				intent.putExtra("img", listImg.get(0));
				startActivity(intent);
			} else if (listImg.size() > 1) {// 多张图片
				Intent intent = new Intent(this, LookMorePicUI.class);
				intent.putExtra("position", position);
				intent.putStringArrayListExtra("img", listImg);
				startActivity(intent);
			}
		}
	}

	/**
	 * 隐藏软键盘
	 */
	private void hideKeyboard() {
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	/**
	 * 显示软键盘
	 */
	private void showKeyboard() {
		et_content.setFocusable(true);
		et_content.setFocusableInTouchMode(true);
		et_content.requestFocus();
		inputMethodManager = (InputMethodManager) et_content.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.showSoftInput(et_content, 0);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		iv_biaoqing.setSelected(false);
		vp_expression.setVisibility(View.GONE);
		return false;
	}

	@Override
	protected void back() {
		setResult(RESULT_OK);
		finish();
	}
}
