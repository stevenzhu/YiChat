package bean;

import java.io.Serializable;

/**
 * 广告
 * 
 * @author peidongxu
 * 
 */
public class AdBean implements Serializable {
	private String id;// 广告title
	private String name;// 广告title
	private String title;// 广告title
	private String img;// 广告图片
	private String url;// 广告页面链接
	private String content;// 广告页面内容
	private String type;// 类型0：本地 1：网络
	private int resId;// 资源图片ID

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getResId() {
		return resId;
	}

	public void setResId(int resId) {
		this.resId = resId;
	}

}
