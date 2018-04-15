package ad_content.ad_content_1.code;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Build;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.shorigo.BaseUI;
import com.shorigo.utils.UrlConstants;
import com.shorigo.utils.Utils;
import com.shorigo.yichat.R;

/**
 * 广告详情页
 * 
 * @author peidongxu
 * 
 */
public class AdContentUI extends BaseUI {
	private WebView webView;
	// 广告链接
	private String content;

	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.ad_content_1);
	}

	@Override
	protected void findView_AddListener() {
		webView = (WebView) findViewById(R.id.webview);
	}

	@Override
	protected void prepareData() {
		setTitle("文章详情");

		// 接收参数
		Intent intent = getIntent();
		content = intent.getStringExtra("content");

		if (!Utils.isEmity(content)) {
			setWebView();
		}

	}

	/**
	 * 设置WebView参数
	 */
	@TargetApi(Build.VERSION_CODES.KITKAT)
	private void setWebView() {
		webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
		webView.setVerticalScrollBarEnabled(false);
		webView.setVerticalScrollbarOverlay(false);
		webView.setHorizontalScrollBarEnabled(false);
		webView.setHorizontalScrollbarOverlay(false);
		// 设置WebView属性，能够执行Javascript脚本
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
				super.onReceivedSslError(view, handler, error);
				handler.proceed(); // 接受所有网站的证书
			}

			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});

		webView.getSettings().setDefaultTextEncodingName("utf-8");
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
		} else {
			webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
		}
		// 加载服务器上的页面
		webView.loadDataWithBaseURL(UrlConstants.SERVICE_HOST_URL, Utils.getHtmlData(content), "text/html", "utf-8", null);
	}

	@Override
	protected void onMyClick(View v) {
	}

	@Override
	protected void back() {
		finish();
	}

}
