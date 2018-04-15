package cms_comment_detail.cms_comment_detail_1.code;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import bean.CmsCommentBean;
import bean.CmsReplyBean;
import bean.RequestReturnBean;

import com.shorigo.http.HttpUtil;
import com.shorigo.utils.LogUtils;

public class CmsCommentDetailJson {
	/**
	 * 获取动态评论回复
	 * 
	 * @param result
	 * @return RequestReturnBean
	 */
	public static RequestReturnBean getCommentDetail(String result) {
		LogUtils.i("CmsCommentJson", result);
		RequestReturnBean returnBean = new RequestReturnBean();
		try {
			JSONObject jsonObject = new JSONObject(result);
			String code = jsonObject.getString("code");
			returnBean.setCode(code);
			returnBean.setMessage(jsonObject.getString("message"));
			if (HttpUtil.HTTP_STATUS_SUCCESS.equals(code)) {
				JSONObject object2 = jsonObject.getJSONObject("result");
				CmsCommentBean commentsBean = new CmsCommentBean();
				if (object2.has("review_id")) {
					commentsBean.setReview_id(object2.getString("review_id"));
				}
				if (object2.has("user_id")) {
					commentsBean.setUser_id(object2.getString("user_id"));
				}
				if (object2.has("nick")) {
					commentsBean.setNick(object2.getString("nick"));
				}
				if (object2.has("avatar")) {
					commentsBean.setAvatar(object2.getString("avatar"));
				}
				if (object2.has("content")) {
					commentsBean.setContent(object2.getString("content"));
				}
				if (object2.has("is_like")) {
					commentsBean.setIs_like(object2.getString("is_like"));
				}
				if (object2.has("like_num")) {
					commentsBean.setLike_num(object2.getString("like_num"));
				}
				if (object2.has("add_time")) {
					commentsBean.setAdd_time(object2.getString("add_time"));
				}
				if (object2.has("reply")) {
					JSONObject object3;
					JSONArray jsonArray3;
					List<CmsReplyBean> listReplyBeans;
					CmsReplyBean replyBean;
					listReplyBeans = new ArrayList<CmsReplyBean>();
					jsonArray3 = object2.getJSONArray("reply");
					for (int k = 0; k < jsonArray3.length(); k++) {
						replyBean = new CmsReplyBean();
						object3 = jsonArray3.getJSONObject(k);
						if (object3.has("reply_id")) {
							replyBean.setReply_id(object3.getString("reply_id"));
						}
						if (object3.has("parent_reply_id")) {
							replyBean.setParent_reply_id(object3.getString("parent_reply_id"));
						}
						if (object3.has("content")) {
							replyBean.setContent(object3.getString("content"));
						}
						if (object3.has("send_user_id")) {
							replyBean.setSend_user_id(object3.getString("send_user_id"));
						}
						if (object3.has("send_nick")) {
							replyBean.setSend_nick(object3.getString("send_nick"));
						}
						if (object3.has("send_avatar")) {
							replyBean.setSend_avatar(object3.getString("send_avatar"));
						}
						if (object3.has("reply_user_id")) {
							replyBean.setReply_user_id(object3.getString("reply_user_id"));
						}
						if (object3.has("reply_nick")) {
							replyBean.setReply_nick(object3.getString("reply_nick"));
						}
						if (object3.has("reply_avatar")) {
							replyBean.setReply_avatar(object3.getString("reply_avatar"));
						}
						if (object3.has("is_like")) {
							replyBean.setIs_like(object3.getString("is_like"));
						}
						if (object3.has("like_num")) {
							replyBean.setLike_num(object3.getString("like_num"));
						}
						if (object3.has("add_time")) {
							replyBean.setAdd_time(object3.getString("add_time"));
						}
						listReplyBeans.add(replyBean);
					}
					commentsBean.setListReply(listReplyBeans);
				}
				returnBean.setObject(commentsBean);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return returnBean;
	}
	
	
	/**
	 * 操作
	 * 
	 * @param result
	 * @return RequestReturnBean
	 */
	public static RequestReturnBean action(String result) {
		LogUtils.i("CmsCommentDetailJson", result);
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
