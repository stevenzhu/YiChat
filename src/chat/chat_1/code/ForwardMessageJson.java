package chat.chat_1.code;

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

public class ForwardMessageJson {
	/**
	 * 解析我的联系人
	 * 
	 * @param result
	 * @return RequestReturnBean
	 */
	public static RequestReturnBean getContactsList(String result) {
		LogUtils.i("ForwardMessageJson", result);
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
					if (object.has("sex"))
						map.put("sex", object.getString("sex"));
					if (object.has("desc"))
						map.put("sign_desc", object.getString("desc"));
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
