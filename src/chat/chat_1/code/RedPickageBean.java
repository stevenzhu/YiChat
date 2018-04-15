package chat.chat_1.code;

import java.util.List;
import java.util.Map;

/**
 * 红包Bean
 * 
 * @author peidongxu
 * 
 */
public class RedPickageBean {

	private String id;// 红包ID
	private String type;// 红包类型 1 普通红包 2 普通群红包 3 随机群红包
	private String add_user_id;// 发送人用户ID
	private String add_user_nick;// 发送人用户昵称
	private String add_useravatar;// 发送人用户头像
	private String num;// 红包总个数
	private String money;// 红包总金额
	private String message;// 红包标题
	private String add_time;// 红包发放时间
	private String receive_num;// 红包已领个数
	private String status;// 红包状态 0可领取 1已领取 2已抢光 3已过期
	private String receive_status;// 本人领取状态 0未领取 1已领取
	private String receive_money;// 本人领取金额
	private List<Map<String, String>> listReceive;//
	// sn;//领取编号
	// receive_user_id ;//领取人用户ID
	// receive_user_nick ;// 领取人用户昵称
	// receive_user_avatar;//领取人用户头像
	// moneyprivate String ;// 领取金额
	// timeprivate String ;// 领取时间
	private String max_sn;// 手气最佳领取编号

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAdd_user_id() {
		return add_user_id;
	}

	public void setAdd_user_id(String add_user_id) {
		this.add_user_id = add_user_id;
	}

	public String getAdd_user_nick() {
		return add_user_nick;
	}

	public void setAdd_user_nick(String add_user_nick) {
		this.add_user_nick = add_user_nick;
	}

	public String getAdd_useravatar() {
		return add_useravatar;
	}

	public void setAdd_useravatar(String add_useravatar) {
		this.add_useravatar = add_useravatar;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getAdd_time() {
		return add_time;
	}

	public void setAdd_time(String add_time) {
		this.add_time = add_time;
	}

	public String getReceive_num() {
		return receive_num;
	}

	public void setReceive_num(String receive_num) {
		this.receive_num = receive_num;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getReceive_status() {
		return receive_status;
	}

	public void setReceive_status(String receive_status) {
		this.receive_status = receive_status;
	}

	public String getReceive_money() {
		return receive_money;
	}

	public void setReceive_money(String receive_money) {
		this.receive_money = receive_money;
	}

	public List<Map<String, String>> getListReceive() {
		return listReceive;
	}

	public void setListReceive(List<Map<String, String>> listReceive) {
		this.listReceive = listReceive;
	}

	public String getMax_sn() {
		return max_sn;
	}

	public void setMax_sn(String max_sn) {
		this.max_sn = max_sn;
	}

}
