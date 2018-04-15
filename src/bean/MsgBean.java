package bean;

/**
 * 推送消息体
 * 
 * @author peidongxu
 * 
 */
public class MsgBean extends BaseDBBean {
	// array(
	// type
	// 1 有用户绑定您的孩子，等待审核 （给家长）
	// 2 绑定孩子完成 （给家长）
	// 3 绑定孩子请求被拒绝 （给家长）
	// 4 作业更新 （给家长）
	// 5 成绩更新 （给家长）
	// 6 有家长请假（给老师）
	// 7 老师同意请假 （给家长）
	// 8 老师不同意请假 （给家长）
	// 9 进出校门 （给家长）
	// 10 系统通知 运营后台 （给所有用户）
	// 11 学校通知 OA （给所有用户）
	// alert "家庭作业有更新,快去看看吧"
	// rid 关联ID 作业ID/考试ID/请假ID/记录ID/通知ID
	// rule_id 角色ID
	// )

	private String type;// 类型
	private String alert;// 提示内容
	private String rid;// 关联ID
	private String rule_id;// 角色ID
	private String time;// 推送时间
	private String state;// 推送消息状态 0：默认 未读 1：已读

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAlert() {
		return alert;
	}

	public void setAlert(String alert) {
		this.alert = alert;
	}

	public String getRid() {
		return rid;
	}

	public void setRid(String rid) {
		this.rid = rid;
	}

	public String getRule_id() {
		return rule_id;
	}

	public void setRule_id(String rule_id) {
		this.rule_id = rule_id;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

}
