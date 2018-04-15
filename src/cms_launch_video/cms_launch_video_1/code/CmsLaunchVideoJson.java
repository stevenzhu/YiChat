package cms_launch_video.cms_launch_video_1.code;

import org.json.JSONException;
import org.json.JSONObject;

import bean.RequestReturnBean;

import com.shorigo.utils.LogUtils;

public class CmsLaunchVideoJson {
	/**
	 * 无返回值解析，只判断状态
	 * 
	 * @param result
	 * @return RequestReturnBean
	 */
	public static RequestReturnBean analysis(String result) {
		LogUtils.i("CmsLaunchVideoJson", result);
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
