package me.me_3.code;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONObject;

import user_info.user_info_1.code.UserInfoUI;
import wallet.wallet_1.code.WalletUI;
import about.about_2.code.AboutUI;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import bean.RequestReturnBean;

import com.hyphenate.chat.EMClient;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.shorigo.BaseUI;
import com.shorigo.MyApplication;
import com.shorigo.http.HttpUtil;
import com.shorigo.utils.BitmapHelp;
import com.shorigo.utils.Constants;
import com.shorigo.utils.FileUtils;
import com.shorigo.utils.MyConfig;
import com.shorigo.utils.Utils;
import com.shorigo.view.pulltozoomview.PullToZoomScrollViewEx;
import com.shorigo.yichat.R;

import feedback.feedback_1.code.FeedBackUI;

/**
 * 我的
 * 
 * @author peidongxu
 * 
 */
public class MeUI extends BaseUI {
	// 用户头像
	private ImageView iv_avatar;
	// 用户昵称
	private TextView tv_user_nick;
	// 用户简介
	private TextView tv_user_desc;
	/* 版本更新start */
	private Dialog mDialog;
	private String tempPath;
	private ProgressBar pbProgress;
	private TextView tvProgress;
	private TextView tv_version_value;
	private String appUrl;
	/* 版本更新end */

	private PullToZoomScrollViewEx sv_me;

	private Map<String, String> userMap;

	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.me_3);
	}

	@Override
	protected void findView_AddListener() {

		sv_me = (PullToZoomScrollViewEx) findViewById(R.id.sv_me);

		View me_header = View.inflate(this, R.layout.me_3_header, null);
		ImageView iv_back = (ImageView) me_header.findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);
		// 昵称
		tv_user_nick = (TextView) me_header.findViewById(R.id.tv_nick);
		// 头像
		iv_avatar = (ImageView) me_header.findViewById(R.id.iv_avatar);
		iv_avatar.setOnClickListener(this);
		// 简介
		tv_user_desc = (TextView) me_header.findViewById(R.id.tv_desc);

		View me_content = LayoutInflater.from(this).inflate(R.layout.me_3_content, null);

		// 我的钱包
		RelativeLayout z_wallet = (RelativeLayout) me_content.findViewById(R.id.z_wallet);
		z_wallet.setOnClickListener(this);
		// 意见反馈
		RelativeLayout z_feedback = (RelativeLayout) me_content.findViewById(R.id.z_feedback);
		z_feedback.setOnClickListener(this);
		// 版本
		RelativeLayout z_version = (RelativeLayout) me_content.findViewById(R.id.z_version);
		z_version.setOnClickListener(this);
		tv_version_value = (TextView) me_content.findViewById(R.id.tv_version);
		// 关于我们
		RelativeLayout z_about = (RelativeLayout) me_content.findViewById(R.id.z_about);
		z_about.setOnClickListener(this);
		// 注销
		RelativeLayout z_logout = (RelativeLayout) me_content.findViewById(R.id.z_logout);
		z_logout.setOnClickListener(this);

		sv_me.setZoomView(me_header);
		sv_me.setScrollContentView(me_content);
		DisplayMetrics localDisplayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
		int mScreenHeight = localDisplayMetrics.heightPixels;
		int mScreenWidth = localDisplayMetrics.widthPixels;
		LinearLayout.LayoutParams localObject = new LinearLayout.LayoutParams(mScreenWidth, (int) (mScreenHeight / 10 * 4.5));
		sv_me.setHeaderLayoutParams(localObject);

	}

	@Override
	protected void prepareData() {
		// 设置版本值
		String versionName = "";
		try {
			versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
		}
		tv_version_value.setText(versionName);

	}

	@Override
	protected void onResume() {
		super.onResume();
		// 设置用户信息
		userMap = MyConfig.getUserInfo(this);
		setUser();
		getUserInfo();
	}

	@Override
	protected void onMyClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			// 返回
			back();
			break;
		case R.id.iv_avatar:
			// 个人信息
			startActivity(new Intent(this, UserInfoUI.class));
			break;
		case R.id.z_wallet:
			// 钱包
			startActivity(new Intent(this, WalletUI.class));
			break;
		case R.id.z_feedback:
			// 意见反馈
			startActivity(new Intent(this, FeedBackUI.class));
			break;
		case R.id.z_version:
			// 版本更新
			checkVersion();
			break;
		case R.id.z_about:
			// 关于我们
			startActivity(new Intent(this, AboutUI.class));
			break;
		case R.id.z_logout:
			// 注销
			try {
				// 清空token
				MyConfig.saveToken(this, "");
				// 退出环信
				EMClient.getInstance().logout(true);
				back();
			} catch (Exception e) {
			}
			break;
		case R.id.tv_update_version_dialog_ok:
			// 立即升级
			mDialog.dismiss();
			// 弹出下载框
			showDownLoadingDialog();
			new Thread(new Runnable() {

				@Override
				public void run() {
					downLoadAPK();
				}
			}).start();
			break;
		case R.id.tv_update_version_dialog_cancel:
			// 取消升级
			if (mDialog != null) {
				mDialog.dismiss();
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 获取用户信息
	 */
	private void getUserInfo() {
		String url = HttpUtil.getUrl("/user/userInfo");
		Map<String, String> map = new HashMap<String, String>();
		map.put("access_token", MyConfig.getToken(this));
		map.put("user_id", "");

		HttpUtil.post(this, url, map, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = MeJson.getUserInfo(response.toString());
				if (HttpUtil.isSuccess(MeUI.this, returnBean.getCode())) {
					// 登录成功、保存用户信息
					userMap = (Map<String, String>) returnBean.getObject();
					MyConfig.saveUserInfo(MeUI.this, userMap);
					setUser();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}

	/**
	 * 设置用户的信息
	 */
	private void setUser() {
		if (userMap == null) {
			return;
		}
		BitmapHelp.loadImg(this, iv_avatar, userMap.get("avatar"), R.drawable.default_avatar_angle);
		tv_user_nick.setText(userMap.get("user_nick"));
		tv_user_desc.setText(userMap.get("sign_desc"));
	}

	/**
	 * 检查版本更新
	 */
	private void checkVersion() {
		int versionCode = Utils.getVersionCode(this);
		String url = HttpUtil.getUrl("/public/version");
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("version_code", String.valueOf(versionCode));
		HttpUtil.post(this, url, map, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = MeJson.analysisVersion(response.toString());
				if (HttpUtil.isSuccess(MeUI.this, returnBean.getCode())) {
					if (returnBean.getObject() != null && !Utils.isEmity(returnBean.getObject().toString())) {
						appUrl = returnBean.getObject().toString();
						// 弹出版本升级提示框
						showUpdateVersionDialog();
					} else {
						MyApplication.getInstance().showToast("当前为最新版本");
					}
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}

	/**
	 * 版本升级对话框
	 */
	private void showUpdateVersionDialog() {
		mDialog = new Dialog(this, R.style.custom_dialog_style);
		View dialogView = View.inflate(this, R.layout.yudian_update_version_dialog, null);
		dialogView.findViewById(R.id.tv_update_version_dialog_ok).setOnClickListener(this);
		dialogView.findViewById(R.id.tv_update_version_dialog_cancel).setOnClickListener(this);
		mDialog.setContentView(dialogView);
		mDialog.setCancelable(false);
		mDialog.show();

		LayoutParams attributes = mDialog.getWindow().getAttributes();
		attributes.width = Constants.width - Constants.width / 10 * 2;
		mDialog.getWindow().setAttributes(attributes);

		attributes = null;
	}

	/**
	 * 版本下载框
	 */
	private void showDownLoadingDialog() {
		mDialog = new Dialog(this, R.style.custom_dialog_style);
		View view = View.inflate(this, R.layout.yudian_down_load_apk_dialog, null);
		pbProgress = (ProgressBar) view.findViewById(R.id.pb_down_load_apk_dialog);
		tvProgress = (TextView) view.findViewById(R.id.tv_down_load_apk_dialog);
		mDialog.setContentView(view);
		mDialog.setCancelable(false);
		mDialog.show();

		LayoutParams attributes = mDialog.getWindow().getAttributes();
		attributes.width = Constants.width - Constants.width / 10 * 2;
		mDialog.getWindow().setAttributes(attributes);

		attributes = null;
	}

	/**
	 * 下载新版本
	 */
	private void downLoadAPK() {
		tempPath = Constants.path + Constants._video + File.separator + appUrl.replaceAll("[^\\w.]", "");
		HttpUtils http = new HttpUtils();
		http.download(appUrl, tempPath, true, true, new RequestCallBack<File>() {

			@Override
			public void onLoading(long total, long current, boolean isUploading) {
				super.onLoading(total, current, isUploading);
				pbProgress.setMax((int) total);
				pbProgress.setProgress((int) current);
				tvProgress.setText(transformProgress((int) current) + "/" + transformProgress((int) total));
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				mDialog.dismiss();
				FileUtils.deleteFile(appUrl);
				MyApplication.getInstance().showToast("下载失败，请稍后尝试");
			}

			@Override
			public void onSuccess(ResponseInfo<File> responseInfo) {
				// 下载成功
				mDialog.dismiss();
				try {
					String command[] = { "chmod", "777", responseInfo.result.getPath() };
					ProcessBuilder builder = new ProcessBuilder(command);
					builder.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
				Intent installIntent = new Intent(Intent.ACTION_VIEW);
				installIntent.setDataAndType(Uri.fromFile(responseInfo.result), "main/vnd.android.package-archive");
				startActivity(installIntent);
				finish();
			}
		});
	}

	/**
	 * 转换进度
	 * 
	 * @param progress
	 * @return
	 */
	public String transformProgress(int progress) {
		String result = null;
		if (progress / 1048576 > 0) { // 单位为MB
			result = divide(progress, 1048576) + "MB";
		} else if (progress / 1024 > 0) { // 单位为KB
			result = divide(progress, 1024) + "KB";
		} else { // 单位为B
			result = progress + "B";
		}
		return result;
	}

	/**
	 * 除法运算,保留两位小数点,并四舍五入
	 * 
	 * @param m
	 *            被除数
	 * @param n
	 *            除数
	 * @return
	 */
	public double divide(int m, int n) {
		BigDecimal bd1 = new BigDecimal(m);
		BigDecimal bd2 = new BigDecimal(n);
		return bd1.divide(bd2, 2, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	@Override
	protected void back() {
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
