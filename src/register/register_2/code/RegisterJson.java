package register.register_2.code;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import bean.RequestReturnBean;

import com.shorigo.http.HttpUtil;
import com.shorigo.utils.LogUtils;

public class RegisterJson {

	/**
	 * 无返回值解析，只判断状态
	 * 
	 * @param result
	 * @return RequestReturnBean
	 */
	public static RequestReturnBean getCode(String result) {
		LogUtils.i("RegisterJson", result);
		RequestReturnBean returnBean = new RequestReturnBean();
		try {
			JSONObject jsonObject = new JSONObject(result);
			String code = jsonObject.getString("code");
			returnBean.setCode(code);
			returnBean.setMessage(jsonObject.getString("message"));
			// returnBean.setObject(jsonObject.getString("result"));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// returnBean.setCode("1");
		// returnBean.setMessage("成功");
		return returnBean;
	}

	/**
	 * 登录
	 * 
	 * @param result
	 * @return RequestReturnBean
	 */
	public static RequestReturnBean register(String result) {
		LogUtils.i("RegisterJson", result);
		RequestReturnBean returnBean = new RequestReturnBean();
		try {
			JSONObject jsonObject = new JSONObject(result);
			String status = jsonObject.getString("code");
			returnBean.setCode(status);
			returnBean.setMessage(jsonObject.getString("message"));
			if (HttpUtil.HTTP_STATUS_SUCCESS.equals(status)) {
				Map<String, String> map = new HashMap<String, String>();
				JSONObject object = jsonObject.getJSONObject("result");
				if (object.has("access_token")) {
					map.put("access_token", object.getString("access_token"));
				} else {
					map.put("access_token", "");
				}
				if (object.has("user_id")) {
					map.put("user_id", object.getString("user_id"));
				} else {
					map.put("user_id", "");
				}
				returnBean.setObject(map);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// returnBean.setCode("1");
		// returnBean.setMessage("成功");
		// Map<String,String> map = new HashMap<String, String>();
		// map.put("access_token", "sadasdasdasdasdfqwzsad");
		// map.put("user_id", "1");
		// returnBean.setObject(map);
		return returnBean;
	}
	
	/**
	 * 获取用户信息
	 * 
	 * @param result
	 * @return RequestReturnBean
	 */
	public static RequestReturnBean getUserInfo(String result) {
		LogUtils.i("RegisterJson", result);
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
				returnBean.setObject(map);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return returnBean;
	}
}
