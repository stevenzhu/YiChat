package main.main_1.code;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import bean.RequestReturnBean;

import com.shorigo.http.HttpUtil;
import com.shorigo.utils.LogUtils;

public class MainJson {
	/**
	 * 获取附近的人
	 * 
	 * @param result
	 * @return RequestReturnBean
	 */
	public static RequestReturnBean getNearList(String result) {
		LogUtils.i("MainJson", result);
		RequestReturnBean returnBean = new RequestReturnBean();
		try {
			JSONObject jsonObject = new JSONObject(result);
			String code = jsonObject.getString("code");
			returnBean.setCode(code);
			returnBean.setMessage(jsonObject.getString("message"));
			if (HttpUtil.HTTP_STATUS_SUCCESS.equals(code)) {
				JSONArray jsonArray = jsonObject.getJSONArray("result");
				JSONObject object;
				List<Map<String, String>> listMap = new ArrayList<Map<String, String>>();
				Map<String, String> map;
				for (int i = 0; i < jsonArray.length(); i++) {
					object = jsonArray.getJSONObject(i);
					map = new HashMap<String, String>();
					if (object.has("user_id")) {
						map.put("user_id", object.getString("user_id"));
					}
					if (object.has("user_nick")) {
						map.put("user_nick", object.getString("user_nick"));
					}
					if (object.has("avatar")) {
						map.put("avatar", object.getString("avatar"));
					}
					if (object.has("sex")) {
						map.put("sex", object.getString("sex"));
					}
					if (object.has("family_id")) {
						map.put("family_id", object.getString("family_id"));
					}
					if (object.has("family_name")) {
						map.put("family_name", object.getString("family_name"));
					}
					if (object.has("distance")) {
						map.put("distance", object.getString("distance"));
					}
					if (object.has("lon")) {
						map.put("lon", object.getString("lon"));
					}
					if (object.has("lat")) {
						map.put("lat", object.getString("lat"));
					}
					if (object.has("age"))
						map.put("age", object.getString("age"));
					if (object.has("birthday"))
						map.put("birthday", object.getString("birthday"));
					listMap.add(map);
				}
				returnBean.setListObject(listMap);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return returnBean;
	}

}
