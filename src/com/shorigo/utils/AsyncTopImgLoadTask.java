package com.shorigo.utils;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.TextView;
import bean.RequestReturnBean;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.shorigo.http.HttpUtil;
import com.shorigo.yichat.R;

/**
 * 头像异步检测缓存并加载
 * 
 * @author peidongxu
 * 
 */
public class AsyncTopImgLoadTask extends AsyncTask<Void, Void, Void> {
	private Context context;// 当前对象
	private String user_id;// 用户ID
	private TextView tv_user_nick;// 用户头像控件
	private ImageView iv_avatar;// 用户头像控件
	private Gson gson;
	private Type userType;
	private Map<String, String> userMap;// 用户缓存数据

	public AsyncTopImgLoadTask(Context context, String user_id, TextView tv_user_nick, ImageView iv_avatar) {
		this.context = context;
		gson = new Gson();
		userType = new TypeToken<Map<String, String>>() {
		}.getType();
		if (!Utils.isEmity(user_id) && user_id.contains(Constants.PREFIX)) {
			this.user_id = user_id.replace(Constants.PREFIX, "");
		} else {
			this.user_id = user_id;
		}
		this.tv_user_nick = tv_user_nick;
		this.iv_avatar = iv_avatar;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected Void doInBackground(Void... arg0) {
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		if (!Utils.isEmity(user_id)) {
			userMap = gson.fromJson(ACache.get(context).getAsString("user_" + user_id), userType);
			if (userMap != null) {
				if (Utils.isEmity(userMap.get("avatar"))) {
					// 没有头像--获取服务器上数据
					getUserInfo();
				} else {
					if (tv_user_nick != null) {
						tv_user_nick.setText(userMap.get("user_nick"));
					}
					BitmapHelp.loadImg(context, iv_avatar, userMap.get("avatar"), R.drawable.default_avatar_angle);
				}
			} else {
				// 本地没有该用户--获取服务器上数据
				getUserInfo();
			}
		} else {
			BitmapHelp.loadImg(context, iv_avatar, "", R.drawable.default_avatar_angle);
		}
		super.onPostExecute(result);
	}

	/**
	 * 获取用户信息
	 */
	private void getUserInfo() {
		String url = HttpUtil.getUrl("/user/userInfo");
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("access_token", MyConfig.getToken(context));
		map.put("user_id", user_id);
		HttpUtil.post(context, url, map, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				RequestReturnBean returnBean = getUserInfo(response.toString());
				if (HttpUtil.isSuccess(context, returnBean.getCode())) {
					// 成功
					Map<String, String> map = (Map<String, String>) returnBean.getObject();
					if (map != null) {
						if (userMap == null) {
							userMap = new HashMap<String, String>();
							userMap.put("user_id", user_id);
							userMap.put("user_nick", map.get("user_nick"));
							userMap.put("avatar", map.get("avatar"));
							String json = gson.toJson(userMap);
							ACache.get(context).put("user_" + user_id, json);
						} else {
							userMap.put("user_nick", map.get("user_nick"));
							userMap.put("avatar", map.get("avatar"));
							String json = gson.toJson(userMap);
							ACache.get(context).put("user_" + user_id, json);
						}
						if (tv_user_nick != null) {
							tv_user_nick.setText(userMap.get("user_nick"));
						}
						BitmapHelp.loadImg(context, iv_avatar, userMap.get("avatar"), R.drawable.default_avatar_angle);
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
	 * 解析用户信息
	 * 
	 * @param result
	 * @return RequestReturnBean
	 */
	public static RequestReturnBean getUserInfo(String result) {
		LogUtils.i("getUserInfo", result);
		RequestReturnBean returnBean = new RequestReturnBean();
		try {
			JSONObject jsonObject = new JSONObject(result);
			String code = jsonObject.getString("code");
			returnBean.setCode(code);
			returnBean.setMessage(jsonObject.getString("message"));
			if (HttpUtil.HTTP_STATUS_SUCCESS.equals(code)) {
				JSONObject object = jsonObject.getJSONObject("result");
				Map<String, String> map = new HashMap<String, String>();
				if (object.has("user_id"))
					map.put("user_id", object.getString("user_id"));
				if (object.has("user_nick"))
					map.put("user_nick", object.getString("user_nick"));
				if (object.has("avatar"))
					map.put("avatar", object.getString("avatar"));
				returnBean.setObject(map);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return returnBean;
	}

}
