package withdrawal_list.withdrawal_list_2.code;

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

public class WithdrawalListJson {
	/**
	 * 获取提现记录列表
	 * 
	 * @param result
	 * @return RequestReturnBean
	 */
	public static RequestReturnBean getWithdrawalList(String result) {
		LogUtils.i("WithdrawalListJson", result);
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
					if (object.has("id"))
						map.put("id", object.getString("id"));
					if (object.has("bank_id"))
						map.put("bank_id", object.getString("bank_id"));
					if (object.has("bank_name"))
						map.put("bank_name", object.getString("bank_name"));
					if (object.has("type"))
						map.put("type", object.getString("type"));
					if (object.has("amount"))
						map.put("amount", object.getString("amount"));
					if (object.has("true_amount"))
						map.put("true_amount", object.getString("true_amount"));
					if (object.has("status"))
						map.put("status", object.getString("status"));
					if (object.has("time"))
						map.put("time", object.getString("time"));
					listMap.add(map);
				}
				returnBean.setListObject(listMap);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// returnBean.setStatus("1");
		// returnBean.setInfo("成功");
		// returnBean.setObject("300");
		// List<Map<String, String>> listMap = new ArrayList<Map<String,
		// String>>();
		// Map<String, String> map;
		// for (int i = 0; i < 15; i++) {
		// map = new HashMap<String, String>();
		// map.put("money", "20");
		// map.put("time","12421311");
		// map.put("state", "处理中");
		// listMap.add(map);
		// }
		// returnBean.setListObject(listMap);

		return returnBean;
	}
}
