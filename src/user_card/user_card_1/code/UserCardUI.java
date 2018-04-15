package user_card.user_card_1.code;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import look_pic.look_pic_1.code.LookPicUI;

import org.apache.http.Header;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import bean.RequestReturnBean;
import chat.chat_1.code.ChatUI;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.shorigo.BaseUI;
import com.shorigo.MyApplication;
import com.shorigo.http.HttpUtil;
import com.shorigo.utils.ACache;
import com.shorigo.utils.BitmapHelp;
import com.shorigo.utils.Constants;
import com.shorigo.utils.MyConfig;
import com.shorigo.utils.Utils;
import com.shorigo.yichat.R;

/**
 * 个人名片页
 * 
 * @author peidongxu
 * 
 */
public class UserCardUI extends BaseUI {
	private ImageView iv_avatar;
	private TextView tv_user_nick, tv_family, tv_address, tv_sign,tvSexAge;
//	private ImageView iv_sex;
	private String id;
	private TextView tv_chat;
	private TextView tv_add_friend;

	private Gson gson;
	private Map<String, String> userMap;
	private String friend;

	private ProgressDialog progressDialog;// 等待框

	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.user_card_1);
	}

	@Override
	protected void findView_AddListener() {
		iv_avatar = (ImageView) findViewById(R.id.iv_avatar);
		iv_avatar.setOnClickListener(this);
		tv_user_nick = (TextView) findViewById(R.id.tv_user_nick);
//		iv_sex = (ImageView) findViewById(R.id.iv_sex);
		tv_family = (TextView) findViewById(R.id.tv_family);
		tv_address = (TextView) findViewById(R.id.tv_address);
		tv_sign = (TextView) findViewById(R.id.tv_sign);
		tvSexAge = (TextView) findViewById(R.id.tvSexAge);

		tv_chat = (TextView) findViewById(R.id.tv_chat);
		tv_chat.setOnClickListener(this);
		tv_add_friend = (TextView) findViewById(R.id.tv_add_friend);
		tv_add_friend.setOnClickListener(this);
	}

	@Override
	protected void prepareData() {
		setTitle("详细资料");

		gson = new Gson();
		Intent intent = getIntent();
		id = intent.getStringExtra("user_id");

		// 记录 已查看过此人
		Type listStringTemp = new TypeToken<List<String>>() {
		}.getType();
		List<String> listUserId = gson.fromJson(ACache.get(this).getAsString("list_user_id"), listStringTemp);
		if (listUserId == null) {
			listUserId = new ArrayList<String>();
		}
		if (!listUserId.contains(id)) {
			listUserId.add(id);
			String json = gson.toJson(listUserId);
			ACache.get(this).put("list_user_id", json);
		}

		Type user = new TypeToken<Map<String, String>>() {
		}.getType();
		userMap = gson.fromJson(ACache.get(this).getAsString("user_" + id), user);
		setValue();

		getUserInfo();
	}

	@Override
	protected void onMyClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.iv_avatar:
			if (userMap != null && !Utils.isEmity(userMap.get("avatar"))) {
				intent = new Intent(this, LookPicUI.class);
				intent.putExtra("img", userMap.get("avatar"));
				startActivity(intent);
			}
			break;
		case R.id.tv_chat:
			// 发消息
			intent = new Intent(this, ChatUI.class);
			intent.putExtra("id", Constants.PREFIX + id);
			startActivity(intent);
			break;
		case R.id.tv_add_friend:
			if (userMap == null) {
				return;
			}
			// 是否好友 0：不是好友 1：是好友
			friend = userMap.get("friend");
			if ("1".equals(friend)) {
				delContact(Constants.PREFIX + id);
			} else {
				addContact(Constants.PREFIX + id);
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
		map.put("user_id", id);
		HttpUtil.post(this, url, map, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = UserCardJson.getUserInfo(response.toString());
				if (HttpUtil.isSuccess(UserCardUI.this, returnBean.getCode())) {
					// 成功
					userMap = (Map<String, String>) returnBean.getObject();
					setValue();
					// 个人数据缓存
					String json = gson.toJson(userMap);
					ACache.get(UserCardUI.this).put("user_" + id, json);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}

	/**
	 * 添加联系人
	 */
	public void addContact(final String user_id) {
		if (EMClient.getInstance().getCurrentUser().equals(user_id)) {
			new EaseAlertDialog(this, R.string.not_add_myself).show();
			return;
		}

		progressDialog = new ProgressDialog(this);
		String stri = getResources().getString(R.string.Is_sending_a_request);
		progressDialog.setMessage(stri);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();

		new Thread(new Runnable() {
			public void run() {
				try {
					String s = getResources().getString(R.string.Add_a_friend);
					EMClient.getInstance().contactManager().addContact(user_id, s);
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							String s1 = getResources().getString(R.string.send_successful);
							MyApplication.getInstance().showToast(s1);
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
						}
					});
				}
			}
		}).start();
	}

	/**
	 * 删除联系人
	 */
	public void delContact(final String user_id) {
		progressDialog = new ProgressDialog(this);
		String stri = getResources().getString(R.string.Is_sending_a_request);
		progressDialog.setMessage(stri);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();

		new Thread(new Runnable() {
			public void run() {
				try {
					EMClient.getInstance().contactManager().deleteContact(user_id);
					runOnUiThread(new Runnable() {
						public void run() {
							delFriend();
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
						}
					});
				}
			}
		}).start();
	}

	/**
	 * 移除好友
	 */
	private void delFriend() {
		String url = HttpUtil.getUrl("/user/delFriend");
		Map<String, String> map = new HashMap<String, String>();
		map.put("access_token", MyConfig.getToken(this));
		map.put("to_user_id", id);
		HttpUtil.post(this, url, map, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = UserCardJson.delFriend(response.toString());
				if (HttpUtil.isSuccess(UserCardUI.this, returnBean.getCode())) {
					// 成功
					if (progressDialog != null) {
						progressDialog.dismiss();
					}
					// 是否好友 0：不是好友 1：是好友
					userMap.put("friend", "0");
					tv_chat.setVisibility(View.GONE);
					tv_add_friend.setVisibility(View.VISIBLE);
					tv_add_friend.setText("添加好友");
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
				if (progressDialog != null) {
					progressDialog.dismiss();
				}
			}
		});
	}

	private void setValue() {
		if (userMap == null) {
			return;
		}

		BitmapHelp.loadImg(this, iv_avatar, userMap.get("avatar"), R.drawable.default_avatar_angle);
		tv_user_nick.setText(userMap.get("user_nick"));
		tv_family.setText(userMap.get("family_name"));
		// 性别1：男 2：女
//		String sex = userMap.get("sex");
//		if ("1".equals(sex)) {
//			iv_sex.setImageResource(R.drawable.icon_boy);
//		} else if ("2".equals(sex)) {
//			iv_sex.setImageResource(R.drawable.icon_girl);
//		}
		// 年龄
//		tv_age.setText(userMap.get("age"));

		// 性别1：男 2：女
		boolean isBoy = "1".equals(userMap.get("sex"));
		tvSexAge.setSelected(!isBoy);
		tvSexAge.setText(userMap.get("age"));

		tv_address.setText(userMap.get("city_name") + userMap.get("address"));
		tv_sign.setText(userMap.get("sign_desc"));

		Map<String, String> userInfo = MyConfig.getUserInfo(this);
		if (!id.equals(userInfo.get("user_id"))) {
			tv_chat.setVisibility(View.VISIBLE);
			// 是否好友 0：不是好友 1：是好友
			String friend = userMap.get("friend");
			if ("1".equals(friend)) {
				tv_add_friend.setVisibility(View.VISIBLE);
				tv_add_friend.setText("删除好友");
			} else {
				tv_add_friend.setVisibility(View.VISIBLE);
				tv_add_friend.setText("添加好友");
			}
		} else {
			tv_chat.setVisibility(View.GONE);
		}
	}

	@Override
	protected void back() {
		finish();
	}

}
