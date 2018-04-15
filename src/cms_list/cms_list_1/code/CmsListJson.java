package cms_list.cms_list_1.code;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import bean.CmsBean;
import bean.RequestReturnBean;

import com.shorigo.http.HttpUtil;
import com.shorigo.utils.LogUtils;
import com.shorigo.utils.Utils;

public class CmsListJson {

	/**
	 * 获取cms列表
	 * 
	 * @param result
	 * @return RequestReturnBean
	 */
	public static RequestReturnBean getCmsList(String result) {
		LogUtils.i("CmsListJson", result);
		RequestReturnBean returnBean = new RequestReturnBean();
		try {
			JSONObject jsonObject = new JSONObject(result);
			String code = jsonObject.getString("code");
			returnBean.setCode(code);
			returnBean.setMessage(jsonObject.getString("message"));
			if (HttpUtil.HTTP_STATUS_SUCCESS.equals(code)) {
				JSONArray jsonArray = jsonObject.getJSONArray("result");
				List<CmsBean> listCmsBean = new ArrayList<CmsBean>();
				CmsBean cmsBean;
				JSONObject object;
				for (int i = 0; i < jsonArray.length(); i++) {
					object = jsonArray.getJSONObject(i);
					cmsBean = new CmsBean();
					if (object.has("id")) {
						cmsBean.setId(object.getString("id"));
					}
					if (object.has("title")) {
						cmsBean.setTitle(object.getString("title"));
					}
					if (object.has("view_num")) {
						cmsBean.setView_num(object.getString("view_num"));
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
					listCmsBean.add(cmsBean);
				}
				returnBean.setListObject(listCmsBean);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return returnBean;
	}
	
	
	/**
	 * 删除cms
	 * 
	 * @param result
	 * @return RequestReturnBean
	 */
	public static RequestReturnBean delCms(String result) {
		LogUtils.i("CmsListJson", result);
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
