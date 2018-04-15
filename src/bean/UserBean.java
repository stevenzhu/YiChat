package bean;

/**
 * 用户信息
 * 
 * @author SSS
 * 
 */
public class UserBean {
	private String user_id; // ID
	private String user_name; // 名字
	private String user_nick; // 昵称
	private String user_pic; // 头像
	private String birthday; // 生日
	private String sex; // 性别 (0:男 1：女 2：保密)
	private String email; // 邮箱
	private String phone; // 手机号
	private String address; // 所在地
	private String ProvinceName; // 省名称
	private String CityName; // 市名称
	private String DistrictName; // 县（区）名称
	private int ProvinceId; // 省Id
	private int CityId; // 市Id
	private int DistrictId; // 县（区）Id
	private String userBg; // 用户背景
	private String isCertificate; // 是否认证 是否认证 0未认证、1认证中、2已认证、3认证失败
	private String integral; // 用户积分

	private String is_vip; // 会员
	private String point; // 积分

	private String nick_name; // 昵称
	private String shortnum; // 短号
	private String sign_desc; // 个人签名
	private String qq; // QQ
	private String region; // 地区 北京市北京市朝阳区
	private String remark; // 备注 （任课信息或班主任

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

	public String getUser_nick() {
		return user_nick;
	}

	public void setUser_nick(String user_nick) {
		this.user_nick = user_nick;
	}

	public String getUser_pic() {
		return user_pic;
	}

	public void setUser_pic(String user_pic) {
		this.user_pic = user_pic;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getProvinceName() {
		return ProvinceName;
	}

	public void setProvinceName(String provinceName) {
		ProvinceName = provinceName;
	}

	public String getCityName() {
		return CityName;
	}

	public void setCityName(String cityName) {
		CityName = cityName;
	}

	public String getDistrictName() {
		return DistrictName;
	}

	public void setDistrictName(String districtName) {
		DistrictName = districtName;
	}

	public int getProvinceId() {
		return ProvinceId;
	}

	public void setProvinceId(int provinceId) {
		ProvinceId = provinceId;
	}

	public int getCityId() {
		return CityId;
	}

	public void setCityId(int cityId) {
		CityId = cityId;
	}

	public int getDistrictId() {
		return DistrictId;
	}

	public void setDistrictId(int districtId) {
		DistrictId = districtId;
	}

	public String getUserBg() {
		return userBg;
	}

	public void setUserBg(String userBg) {
		this.userBg = userBg;
	}

	public String getIsCertificate() {
		return isCertificate;
	}

	public void setIsCertificate(String isCertificate) {
		this.isCertificate = isCertificate;
	}

	public String getIntegral() {
		return integral;
	}

	public void setIntegral(String integral) {
		this.integral = integral;
	}

	public String getIs_vip() {
		return is_vip;
	}

	public void setIs_vip(String is_vip) {
		this.is_vip = is_vip;
	}

	public String getPoint() {
		return point;
	}

	public void setPoint(String point) {
		this.point = point;
	}

	public String getNick_name() {
		return nick_name;
	}

	public void setNick_name(String nick_name) {
		this.nick_name = nick_name;
	}

	public String getShortnum() {
		return shortnum;
	}

	public void setShortnum(String shortnum) {
		this.shortnum = shortnum;
	}

	public String getSign_desc() {
		return sign_desc;
	}

	public void setSign_desc(String sign_desc) {
		this.sign_desc = sign_desc;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
