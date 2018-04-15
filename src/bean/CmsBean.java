package bean;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import yichat.util.ZUIUtil;

public class CmsBean implements Serializable {
	private String id;// 新闻id
	private String title;// 新闻短标题
	private String content;// 新闻摘要
	private String type;// 新闻类型：1: 单图文章2：多图文章3：组图4：视频
	private String video;// 视频文件地址
	private String video_img;// 视频封面图
	private List<String> listImg;// 列表缩略图地址
	private List<Map<String, String>> listImgMap;// 组图
	private String source;// 来源
	private String view_num;// 点击数
	private String comment_num;// 评论数
	private String add_time;// 发布时间
	private Map<String, String> userMap;

	public String tag;

	public String[] getTags(){
		if(TextUtils.isEmpty(tag)){
			return new String[0];
		}
		String[] ret=tag.split(",");
		return ret;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getVideo() {
		return video;
	}

	public void setVideo(String video) {
		this.video = video;
	}

	public String getVideo_img() {
		return video_img;
	}

	public void setVideo_img(String video_img) {
		this.video_img = video_img;
	}

	public List<String> getListImg() {
		return listImg;
	}

	public void setListImg(List<String> listImg) {
		this.listImg = listImg;
	}

	public List<Map<String, String>> getListImgMap() {
		return listImgMap;
	}

	public void setListImgMap(List<Map<String, String>> listImgMap) {
		this.listImgMap = listImgMap;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getView_num() {
		return view_num;
	}

	public void setView_num(String view_num) {
		this.view_num = view_num;
	}

	public String getComment_num() {
		return comment_num;
	}

	public void setComment_num(String comment_num) {
		this.comment_num = comment_num;
	}

	public String getAdd_time() {
		return add_time;
	}

	public void setAdd_time(String add_time) {
		this.add_time = add_time;
	}

	public Map<String, String> getUserMap() {
		return userMap;
	}

	public void setUserMap(Map<String, String> userMap) {
		this.userMap = userMap;
	}

}
