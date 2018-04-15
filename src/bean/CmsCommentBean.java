package bean;

import java.io.Serializable;
import java.util.List;

/**
 * 评论bean
 * 
 * @author peidongxu
 * 
 */
public class CmsCommentBean implements Serializable {
	private String review_id; // 评论id
	private String user_id; // 评论人id
	private String nick; // 评论人名称
	private String avatar; // 评论人头像
	private String content; // 评论内容
	private String is_like; // 是否点赞 1是0否
	private String like_num; // 点赞数
	private String add_time; // 评论时间
	private List<CmsReplyBean> listReply; // 回复列表

	public String getReview_id() {
		return review_id;
	}

	public void setReview_id(String review_id) {
		this.review_id = review_id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getIs_like() {
		return is_like;
	}

	public void setIs_like(String is_like) {
		this.is_like = is_like;
	}

	public String getLike_num() {
		return like_num;
	}

	public void setLike_num(String like_num) {
		this.like_num = like_num;
	}

	public String getAdd_time() {
		return add_time;
	}

	public void setAdd_time(String add_time) {
		this.add_time = add_time;
	}

	public List<CmsReplyBean> getListReply() {
		return listReply;
	}

	public void setListReply(List<CmsReplyBean> listReply) {
		this.listReply = listReply;
	}
}
