package bean;

/**
 * 评论的回复bean
 * 
 * @author peidongxu
 * 
 */
public class CmsReplyBean {
	private String reply_id; // 回复ID
	private String parent_reply_id; // 回复上级回复ID
	private String content; // 回复内容
	private String send_user_id; // 发送人ID
	private String send_nick; // 发送人名称
	private String send_avatar; // 发送人头像
	private String reply_user_id; // 被回复人ID
	private String reply_nick; // 被回复人名称
	private String reply_avatar; // 被回复人头像
	private String is_like; // 是否点赞 1是0否
	private String like_num; // 点赞数
	private String add_time; // 回复时间

	public String getReply_id() {
		return reply_id;
	}

	public void setReply_id(String reply_id) {
		this.reply_id = reply_id;
	}

	public String getParent_reply_id() {
		return parent_reply_id;
	}

	public void setParent_reply_id(String parent_reply_id) {
		this.parent_reply_id = parent_reply_id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSend_user_id() {
		return send_user_id;
	}

	public void setSend_user_id(String send_user_id) {
		this.send_user_id = send_user_id;
	}

	public String getSend_nick() {
		return send_nick;
	}

	public void setSend_nick(String send_nick) {
		this.send_nick = send_nick;
	}

	public String getSend_avatar() {
		return send_avatar;
	}

	public void setSend_avatar(String send_avatar) {
		this.send_avatar = send_avatar;
	}

	public String getReply_user_id() {
		return reply_user_id;
	}

	public void setReply_user_id(String reply_user_id) {
		this.reply_user_id = reply_user_id;
	}

	public String getReply_nick() {
		return reply_nick;
	}

	public void setReply_nick(String reply_nick) {
		this.reply_nick = reply_nick;
	}

	public String getReply_avatar() {
		return reply_avatar;
	}

	public void setReply_avatar(String reply_avatar) {
		this.reply_avatar = reply_avatar;
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

}
