package update_nick.update_nick_1.code;

import org.json.JSONException;
import org.json.JSONObject;

import bean.RequestReturnBean;

import com.shorigo.utils.LogUtils;

public class UpdateNickJson {
	/**
	 * 
	 * @param result
	 * @return RequestReturnBean
	 */
	public static RequestReturnBean analysis(String result) {
		LogUtils.i("UpdateNickJson", result);
		RequestReturnBean returnBean = new RequestReturnBean();
		try {
			JSONObject jsonObject = new JSONObject(result);
			String code = jsonObject.getString("code");
			returnBean.setCode(code);
			returnBean.setMessage(jsonObject.getString("message"));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// returnBean.setCode("1");
		// returnBean.setMessage("成功");
		return returnBean;
	}
}
