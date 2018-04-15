package group_add_member.group_add_member_1.code;

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

public class GroupAddMemberJson {
	/**
	 * 解析我的联系人
	 * 
	 * @param result
	 * @return RequestReturnBean
	 */
	public static RequestReturnBean getContactsList(String result) {
		LogUtils.i("GroupAddMemberJson", result);
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
					if (object.has("user_id"))
						map.put("user_id", object.getString("user_id"));
					if (object.has("nick"))
						map.put("user_nick", object.getString("nick"));
					if (object.has("avatar"))
						map.put("avatar", object.getString("avatar"));
					if (object.has("friend"))
						map.put("friend", object.getString("friend"));
					if (object.has("sign"))
						map.put("sign_desc", object.getString("sign"));
					if (object.has("user_type"))
						map.put("user_type", object.getString("user_type"));
					if (object.has("user_star"))
						map.put("user_star", object.getString("user_star"));
					if (object.has("user_cert"))
						map.put("user_cert", object.getString("user_cert"));
					listMap.add(map);
				}
				returnBean.setListObject(listMap);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// returnBean.setCode("1000");
		// returnBean.setMessage("");
		// List<Map<String,String>> listMap = new
		// ArrayList<Map<String,String>>();
		// String[] arrName = new String[]{"张三","李四","王五","莎莎","晓琳","张三丰"};
		// for (int i = 0; i < arrName.length; i++) {
		// Map<String,String> map = new HashMap<String, String>();
		// map.put("user_id", String.valueOf(i+1));
		// map.put("user_nick", arrName[i]);
		// map.put("avatar", "");
		// map.put("sex", "0");
		// map.put("sign_desc", "");
		// map.put("is_select", "0");
		// listMap.add(map);
		// }
		// returnBean.setListObject(listMap);
		return returnBean;
	}
}
