package bean;

/**
 * 联系人数组Bean
 * 
 * @author peidongxu
 * 
 */
public class ContactArrBean extends BaseDBBean {

	private String user_id; // 联系人ID
	private String user_name;// 联系人名称
	private String user_pic;// 联系人头像

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getUser_pic() {
		return user_pic;
	}

	public void setUser_pic(String user_pic) {
		this.user_pic = user_pic;
	}

}
