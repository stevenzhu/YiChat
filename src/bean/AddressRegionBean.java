package bean;

/**
 * 25.区域列表
 * 
 * @author SSS
 * 
 */
public class AddressRegionBean extends BaseDBBean {
	private String area_id; // 区域id
	private String area_name; // 区域名称

	public String getArea_id() {
		return area_id;
	}

	public void setArea_id(String area_id) {
		this.area_id = area_id;
	}

	public String getArea_name() {
		return area_name;
	}

	public void setArea_name(String area_name) {
		this.area_name = area_name;
	}

}
