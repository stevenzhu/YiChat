package forget_pass.forget_pass_1.code;

import org.json.JSONException;
import org.json.JSONObject;

import bean.RequestReturnBean;

import com.shorigo.utils.LogUtils;

public class ForgetPassJson {

	/**
	 * 获取验证码
	 * 
	 * @param result
	 * @return RequestReturnBean
	 */
	public static RequestReturnBean getCode(String result) {
		LogUtils.i("ForgetPassJson", result);
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
		return returnBean;
	}

	/**
	 * 找回密码
	 * 
	 * @param result
	 * @return RequestReturnBean
	 */
	public static RequestReturnBean forgetPass(String result) {
		LogUtils.i("ForgetPassJson", result);
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
}
