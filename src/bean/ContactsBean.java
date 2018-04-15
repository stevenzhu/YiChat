package bean;

import java.util.List;

/**
 * 联系人分类
 * 
 * @author peidongxu
 * 
 */
public class ContactsBean {

	private String name;
	private List<UserBean> listUserBean;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<UserBean> getListUserBean() {
		return listUserBean;
	}

	public void setListUserBean(List<UserBean> listUserBean) {
		this.listUserBean = listUserBean;
	}

}
