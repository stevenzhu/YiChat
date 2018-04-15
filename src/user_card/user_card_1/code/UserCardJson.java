package user_card.user_card_1.code;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import bean.RequestReturnBean;

import com.shorigo.http.HttpUtil;
import com.shorigo.utils.LogUtils;

public class UserCardJson {
	/**
	 * 获取用户信息
	 * 
	 * @param result
	 * @return RequestReturnBean
	 */
	public static RequestReturnBean getUserInfo(String result) {
		LogUtils.i("UserCardJson", result);
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
				if (object.has("sex"))
					map.put("sex", object.getString("sex"));
				if (object.has("age"))
					map.put("age", object.getString("age"));
				if (object.has("birthday"))
					map.put("birthday", object.getString("birthday"));
				if (object.has("phone"))
					map.put("phone", object.getString("phone"));
				if (object.has("sign_desc"))
					map.put("sign_desc", object.getString("sign_desc"));
				if (object.has("family_id"))
					map.put("family_id", object.getString("family_id"));
				if (object.has("family_name"))
					map.put("family_name", object.getString("family_name"));
				if (object.has("city_id"))
					map.put("city_id", object.getString("city_id"));
				if (object.has("city_name"))
					map.put("city_name", object.getString("city_name"));
				if (object.has("address"))
					map.put("address", object.getString("address"));
				if (object.has("friend"))
					map.put("friend", object.getString("friend"));
				returnBean.setObject(map);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return returnBean;
	}

	/**
	 * 删除好友
	 * 
	 * @param result
	 * @return RequestReturnBean
	 */
	public static RequestReturnBean delFriend(String result) {
		LogUtils.i("UserCardJson", result);
		RequestReturnBean returnBean = new RequestReturnBean();
		try {
			JSONObject jsonObject = new JSONObject(result);
			String code = jsonObject.getString("code");
			returnBean.setCode(code);
			returnBean.setMessage(jsonObject.getString("message"));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return returnBean;
	}
}
