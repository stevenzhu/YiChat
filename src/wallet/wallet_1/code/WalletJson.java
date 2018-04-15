package wallet.wallet_1.code;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import bean.CardBean;
import bean.RequestReturnBean;

import com.shorigo.http.HttpUtil;
import com.shorigo.utils.LogUtils;

public class WalletJson {
	/**
	 * 获取我的账户
	 * 
	 * @param result
	 * @return RequestReturnBean
	 */
	public static RequestReturnBean getAccount(String result) {
		LogUtils.i("WalletJson", result);
		RequestReturnBean returnBean = new RequestReturnBean();
		try {
			JSONObject jsonObject = new JSONObject(result);
			String code = jsonObject.getString("code");
			returnBean.setCode(code);
			returnBean.setMessage(jsonObject.getString("message"));
			if (HttpUtil.HTTP_STATUS_SUCCESS.equals(code)) {
				JSONObject object = jsonObject.getJSONObject("result");
				Map<String, String> map = new HashMap<String, String>();
				if (object.has("money"))
					map.put("money", object.getString("money"));
				returnBean.setObject(map);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return returnBean;
	}

	/**
	 * 获取我的银行卡列表
	 * 
	 * @param result
	 * @return RequestReturnBean
	 */
	public static RequestReturnBean getUserBankList(String result) {
		LogUtils.i("WalletJson", result);
		RequestReturnBean returnBean = new RequestReturnBean();
		try {
			JSONObject jsonObject = new JSONObject(result);
			String code = jsonObject.getString("code");
			returnBean.setCode(code);
			returnBean.setMessage(jsonObject.getString("message"));
			if (HttpUtil.HTTP_STATUS_SUCCESS.equals(code)) {
				JSONArray jsonArray = jsonObject.getJSONArray("result");
				List<CardBean> listCardBean = new ArrayList<CardBean>();
				CardBean cardBean;
				JSONObject object;
				for (int i = 0; i < jsonArray.length(); i++) {
					object = jsonArray.getJSONObject(i);
					cardBean = new CardBean();
					if (object.has("id")) {
						cardBean.setId(object.getString("id"));
					}
					if (object.has("bank_id")) {
						cardBean.setBank_id(object.getString("bank_id"));
					}
					if (object.has("user_name")) {
						cardBean.setUser_name(object.getString("user_name"));
					}
					if (object.has("identity_card")) {
						cardBean.setIdentity_card(object.getString("identity_card"));
					}
					if (object.has("bank")) {
						JSONObject jsonObject2 = object.getJSONObject("bank");
						Map<String, String> bankMap = new HashMap<String, String>();
						if (jsonObject2.has("id")) {
							bankMap.put("id", jsonObject2.getString("id"));
						}
						if (jsonObject2.has("name")) {
							bankMap.put("name", jsonObject2.getString("name"));
						}
						cardBean.setBankMap(bankMap);
					}
					cardBean.setCheck(false);
					listCardBean.add(cardBean);
				}
				returnBean.setListObject(listCardBean);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return returnBean;
	}

}
