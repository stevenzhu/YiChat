package bean;

import java.util.List;

/**
 * 数据返回基类
 * 
 * @author peidongxu
 * 
 */
public class RequestReturnBean<T> {
	// 请求状态
	private String status;
	// 请求状态
	private String code;
	// 请求信息
	private String message;
	// 请求信息
	private String info;
	// 返回数据(单一值数据)
	private T object;
	// 返回数据(多对象数据)
	private List<T> listObject;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public T getObject() {
		return object;
	}

	public void setObject(T object) {
		this.object = object;
	}

	public List<T> getListObject() {
		return listObject;
	}

	public void setListObject(List<T> listObject) {
		this.listObject = listObject;
	}

}
