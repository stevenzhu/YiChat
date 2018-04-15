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

public class RedPickageJson {
	/**
	 * 获取红包订单号
	 * 
	 * @param result
	 * @return RequestReturnBean
	 */
	public static RequestReturnBean getOrderSn(String result) {
		LogUtils.i("RedPickageJson", result);
		RequestReturnBean returnBean = new RequestReturnBean();
		try {
			JSONObject jsonObject = new JSONObject(result);
			String code = jsonObject.getString("code");
			returnBean.setCode(code);
			returnBean.setMessage(jsonObject.getString("message"));
			if (HttpUtil.HTTP_STATUS_SUCCESS.equals(code)) {
				if (jsonObject.has("result")) {
					returnBean.setObject(jsonObject.getString("result"));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return returnBean;
	}

	/**
	 * 获取红包ID
	 * 
	 * @param result
	 * @return RequestReturnBean
	 */
	public static RequestReturnBean getOrderId(String result) {
		LogUtils.i("RedPickageJson", result);
		RequestReturnBean returnBean = new RequestReturnBean();
		try {
			JSONObject jsonObject = new JSONObject(result);
			String code = jsonObject.getString("code");
			returnBean.setCode(code);
			returnBean.setMessage(jsonObject.getString("message"));
			if (HttpUtil.HTTP_STATUS_SUCCESS.equals(code)) {
				if (jsonObject.has("result")) {
					returnBean.setObject(jsonObject.getString("result"));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return returnBean;
	}

	/**
	 * 领红包检测
	 * 
	 * @param result
	 * @return RequestReturnBean
	 */
	public static RequestReturnBean receiveCheck(String result) {
		LogUtils.i("RedPickageJson", result);
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
	 * 领红包
	 * 
	 * @param result
	 * @return RequestReturnBean
	 */
	public static RequestReturnBean receive(String result) {
		LogUtils.i("RedPickageJson", result);
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
	 * 获取红包详情
	 * 
	 * @param result
	 * @return RequestReturnBean
	 */
	public static RequestReturnBean getDetail(String result) {
		LogUtils.i("RedPickageJson", result);
		RequestReturnBean returnBean = new RequestReturnBean();
		try {
			JSONObject jsonObject = new JSONObject(result);
			String code = jsonObject.getString("code");
			returnBean.setCode(code);
			returnBean.setMessage(jsonObject.getString("message"));
			if (HttpUtil.HTTP_STATUS_SUCCESS.equals(code)) {
				JSONObject object = jsonObject.getJSONObject("result");
				RedPickageBean bean = new RedPickageBean();
				if (object.has("id")) {
					bean.setId(object.getString("id"));
				}
				if (object.has("type")) {
					bean.setType(object.getString("type"));
				}
				if (object.has("add_user_id")) {
					bean.setAdd_user_id(object.getString("add_user_id"));
				}
				if (object.has("add_user_nick")) {
					bean.setAdd_user_nick(object.getString("add_user_nick"));
				}
				if (object.has("add_useravatar")) {
					bean.setAdd_useravatar(object.getString("add_useravatar"));
				}
				if (object.has("num")) {
					bean.setNum(object.getString("num"));
				}
				if (object.has("money")) {
					bean.setMoney(object.getString("money"));
				}
				if (object.has("message")) {
					bean.setMessage(object.getString("message"));
				}
				if (object.has("add_time")) {
					bean.setAdd_time(object.getString("add_time"));
				}
				if (object.has("receive_num")) {
					bean.setReceive_num(object.getString("receive_num"));
				}
				if (object.has("status")) {
					bean.setStatus(object.getString("status"));
				}
				if (object.has("receive_status")) {
					bean.setReceive_status(object.getString("receive_status"));
				}
				if (object.has("receive_money")) {
					bean.setReceive_money(object.getString("receive_money"));
				}
				if (object.has("receive_list")) {
					JSONArray jsonArray = object.getJSONArray("receive_list");
					List<Map<String, String>> listReceive = new ArrayList<Map<String, String>>();
					JSONObject jsonObject2;
					Map<String, String> map;
					for (int i = 0; i < jsonArray.length(); i++) {
						jsonObject2 = jsonArray.getJSONObject(i);
						map = new HashMap<String, String>();
						if (jsonObject2.has("sn")) {
							map.put("sn", jsonObject2.getString("sn"));
						}
						if (jsonObject2.has("receive_user_id")) {
							map.put("receive_user_id", jsonObject2.getString("receive_user_id"));
						}
						if (jsonObject2.has("receive_user_nick")) {
							map.put("receive_user_nick", jsonObject2.getString("receive_user_nick"));
						}
						if (jsonObject2.has("receive_user_avatar")) {
							map.put("receive_user_avatar", jsonObject2.getString("receive_user_avatar"));
						}
						if (jsonObject2.has("money")) {
							map.put("money", jsonObject2.getString("money"));
						}
						if (jsonObject2.has("time")) {
							map.put("time", jsonObject2.getString("time"));
						}
						listReceive.add(map);
					}
					bean.setListReceive(listReceive);
				}
				returnBean.setObject(bean);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return returnBean;
	}
}
