package bean;

/**
 * 提现Bean
 * 
 * @author peidongxu
 * 
 */
public class WithdrawalBean {

//	private String card_num;// 银行卡号
//	private String card_name;// 银行卡名称
	private String user_name;// 提现人
	private String money;// 提现金额
	private String time;// 提现时间
//	private String state;// 提现状态

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
}
