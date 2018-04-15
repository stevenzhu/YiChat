package card_add.card_add_1.code;

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

public class CardAddJson {

	/**
	 * 无返回值解析，只判断状态
	 * 
	 * @param result
	 * @return RequestReturnBean
	 */
	public static RequestReturnBean analysis(String result) {
		LogUtils.i("CardAddJson", result);
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

	/**
	 * 获取银行列表
	 * 
	 * @param result
	 * @return RequestReturnBean
	 */
	public static RequestReturnBean getBankList(String result) {
		LogUtils.i("CardAddJson", result);
		RequestReturnBean returnBean = new RequestReturnBean();
		try {
			JSONObject jsonObject = new JSONObject(result);
			String code = jsonObject.getString("code");
			returnBean.setCode(code);
			returnBean.setMessage(jsonObject.getString("message"));
			if (HttpUtil.HTTP_STATUS_SUCCESS.equals(code)) {
				JSONArray jsonArray = jsonObject.getJSONArray("result");
				List<Map<String, String>> listMap = new ArrayList<Map<String, String>>();
				Map<String, String> map;
				JSONObject object;
				for (int i = 0; i < jsonArray.length(); i++) {
					object = jsonArray.getJSONObject(i);
					map = new HashMap<String, String>();
					if (object.has("id")) {
						map.put("id", object.getString("id"));
					}
					if (object.has("name")) {
						map.put("name", object.getString("name"));
					}
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
