package cms_detail.cms_detail_1.code;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import bean.CmsBean;
import bean.CmsCommentBean;
import bean.CmsReplyBean;
import bean.RequestReturnBean;

import com.shorigo.http.HttpUtil;
import com.shorigo.utils.LogUtils;
import com.shorigo.utils.Utils;

public class CmsDetailJson {

	/**
	 * 获取cms详情
	 * 
	 * @param result
	 * @return RequestReturnBean
	 */
	public static RequestReturnBean getCmsDetail(String result) {
		LogUtils.i("CmsDetailJson", result);
		RequestReturnBean returnBean = new RequestReturnBean();
		try {
			JSONObject jsonObject = new JSONObject(result);
			String code = jsonObject.getString("code");
			returnBean.setCode(code);
			returnBean.setMessage(jsonObject.getString("message"));
			if (HttpUtil.HTTP_STATUS_SUCCESS.equals(code)) {
				JSONObject object = jsonObject.getJSONObject("result");
				CmsBean cmsBean = new CmsBean();
				if (object.has("id")) {
					cmsBean.setId(object.getString("id"));
				}
				if (object.has("title")) {
					cmsBean.setTitle(object.getString("title"));
				}
				if (object.has("content")) {
					cmsBean.setContent(object.getString("content"));
				}
				if (object.has("comment_num")) {
					cmsBean.setComment_num(object.getString("comment_num"));
				}
				if (object.has("video")) {
					String video = object.getString("video");
					if (!Utils.isEmity(video)) {
						cmsBean.setType("4");
						cmsBean.setVideo(video);
					}
				}
				//debug
				if (object.has("video_img")) {
					cmsBean.setVideo_img(object.getString("video_img"));
				}
				if (object.has("imgs")) {
					JSONArray jsonArray2 = object.getJSONArray("imgs");
					List<String> listImg = new ArrayList<String>();
					int imgSize = jsonArray2.length();
					if (imgSize >= 3) {
						cmsBean.setType("2");
					} else if (imgSize >= 1) {
						cmsBean.setType("1");
					}
					for (int j = 0; j < imgSize; j++) {
						listImg.add(jsonArray2.getString(j));
					}
					cmsBean.setListImg(listImg);
				}
				if (object.has("user")) {
					Map<String, String> userMap = new HashMap<String, String>();
					JSONObject jsonObject2 = object.getJSONObject("user");
					if (jsonObject2.has("user_nick") && !Utils.isEmity(jsonObject2.getString("user_nick"))) {
						cmsBean.setSource(jsonObject2.getString("user_nick"));
						userMap.put("user_nick", jsonObject2.getString("user_nick"));
					} else {
						cmsBean.setSource("彝人圈子");
					}
					if (jsonObject2.has("user_id")) {
						userMap.put("user_id", jsonObject2.getString("user_id"));
					}
					cmsBean.setUserMap(userMap);
				} else {
					cmsBean.setSource("彝人圈子");
				}
				if (object.has("add_time")) {
					cmsBean.setAdd_time(object.getString("add_time"));
				}
				cmsBean.tag=object.optString("tag");
				returnBean.setObject(cmsBean);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return returnBean;
	}

	/**
	 * 获取动态评论
	 * 
	 * @param result
	 * @return RequestReturnBean
	 */
	public static RequestReturnBean getComment(String result) {
		LogUtils.i("CmsDetailJson", result);
		RequestReturnBean returnBean = new RequestReturnBean();
		try {
			JSONObject jsonObject = new JSONObject(result);
			String code = jsonObject.getString("code");
			returnBean.setCode(code);
			returnBean.setMessage(jsonObject.getString("message"));
			if (HttpUtil.HTTP_STATUS_SUCCESS.equals(code)) {
				JSONArray jsonArray2 = jsonObject.getJSONArray("result");
				JSONObject object2;
				JSONObject object3;
				JSONArray jsonArray3;
				List<CmsCommentBean> listCommentsBeans = new ArrayList<CmsCommentBean>();
				CmsCommentBean commentsBean;
				List<CmsReplyBean> listReplyBeans;
				CmsReplyBean replyBean;
				for (int j = 0; j < jsonArray2.length(); j++) {
					object2 = jsonArray2.getJSONObject(j);
					commentsBean = new CmsCommentBean();
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
					listCommentsBeans.add(commentsBean);
				}
				returnBean.setListObject(listCommentsBeans);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return returnBean;
	}

	/**
	 * 操作、无返回值
	 * 
	 * @param result
	 * @return RequestReturnBean
	 */
	public static RequestReturnBean action(String result) {
		LogUtils.i("CmsDetailJson", result);
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
