package bean;

import java.util.Map;

/**
 * 银行卡Bean
 * 
 * @author peidongxu
 * 
 */
public class CardBean {

	private String id;// 银行卡ID
	private String bank_id;// 银行ID
	private String user_name;// 持卡人姓名
	private String card_code;// 卡号
	private String identity_card;// 用户身份证号
	private Map<String, String> bankMap;// 银行信息
	private boolean check;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBank_id() {
		return bank_id;
	}

	public void setBank_id(String bank_id) {
		this.bank_id = bank_id;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getCard_code() {
		return card_code;
	}

	public void setCard_code(String card_code) {
		this.card_code = card_code;
	}

	public String getIdentity_card() {
		return identity_card;
	}

	public void setIdentity_card(String identity_card) {
		this.identity_card = identity_card;
	}

	public Map<String, String> getBankMap() {
		return bankMap;
	}

	public void setBankMap(Map<String, String> bankMap) {
		this.bankMap = bankMap;
	}

	public boolean isCheck() {
		return check;
	}

	public void setCheck(boolean check) {
		this.check = check;
	}

}
