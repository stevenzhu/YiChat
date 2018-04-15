package protocol.protocol_1.code;

import android.view.View;
import android.webkit.WebView;

import com.shorigo.BaseUI;
import com.shorigo.yichat.R;

/**
 * 协议
 * 
 * @author peidongxu
 * 
 */
public class ProtocolUI extends BaseUI {

	private WebView webview;

	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.protocol_1);
	}

	@Override
	protected void findView_AddListener() {
		webview = (WebView) findViewById(R.id.webview);

	}

	@Override
	protected void prepareData() {
		setTitle("用户协议");
		// 加载协议内容
		webview.loadUrl("file:///android_asset/xieyi.html");
	}

	@Override
	protected void onMyClick(View v) {

	}

	@Override
	protected void back() {
		finish();
	}

}
