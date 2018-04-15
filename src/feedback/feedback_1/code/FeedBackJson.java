package feedback.feedback_1.code;

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

public class FeedBackJson {
	/**
	 * 获取意见反馈分类
	 * 
	 * @param result
	 * @return RequestReturnBean
	 */
	public static RequestReturnBean getFeedBackCate(String result) {
		LogUtils.i("FeedBackJson", result);
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
					map.put("id", object.getString("id"));
					map.put("name", object.getString("name"));
					map.put("state", "0");
					listMap.add(map);
				}
				returnBean.setListObject(listMap);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// returnBean.setStatus("1");
		// returnBean.setInfo("成功");
		// List<Map<String, String>> listMap = new ArrayList<Map<String,
		// String>>();
		// Map<String, String> map;
		// for (int i = 0; i < 8; i++) {
		// map = new HashMap<String, String>();
		// map.put("id", String.valueOf(i + 1));
		// map.put("name", "分类" + String.valueOf(i + 1));
		// map.put("state", "0");
		// listMap.add(map);
		// }
		// returnBean.setListObject(listMap);
		return returnBean;
	}

	/**
	 * 提交意见反馈
	 * 
	 * @param result
	 * @return RequestReturnBean
	 */
	public static RequestReturnBean submitFeedBack(String result) {
		LogUtils.i("FeedBackJson", result);
		RequestReturnBean returnBean = new RequestReturnBean();
		try {
			JSONObject jsonObject = new JSONObject(result);
			String code = jsonObject.getString("code");
			returnBean.setCode(code);
			returnBean.setMessage(jsonObject.getString("message"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// returnBean.setStatus("1");
		// returnBean.setInfo("成功");
		return returnBean;
	}
}
