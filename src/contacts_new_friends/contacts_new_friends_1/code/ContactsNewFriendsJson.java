package contacts_new_friends.contacts_new_friends_1.code;

import org.json.JSONException;
import org.json.JSONObject;

import bean.RequestReturnBean;

import com.shorigo.utils.LogUtils;

public class ContactsNewFriendsJson {
	/**
	 * 加好友
	 * 
	 * @param result
	 * @return RequestReturnBean
	 */
	public static RequestReturnBean addFriend(String result) {
		LogUtils.i("ContactsNewFriendsJson", result);
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
