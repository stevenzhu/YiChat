package bean;

/**
 * 版本更新 Bean
 * 
 * @author peidongxu
 */
public class AppVersion {
	private String url;// apk文件下载地址 没有新版本则为空
	private String isforceupdata;// 是否强制更新 1是 0否

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getIsforceupdata() {
		return isforceupdata;
	}

	public void setIsforceupdata(String isforceupdata) {
		this.isforceupdata = isforceupdata;
	}

}