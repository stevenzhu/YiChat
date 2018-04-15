package chat.chat_1.code;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.hyphenate.easeui.ui.EaseBaseActivity;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.shorigo.yichat.R;

/**
 * 聊天页
 * 
 * @author peidongxu
 */
public class ChatUI extends EaseBaseActivity implements OnClickListener {
	public static ChatUI activityInstance;
	private EaseChatFragment chatFragment;
	String toChatUsername;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.ease_activity_chat);
		activityInstance = this;
		// 用户ID 或者 群组ID
		toChatUsername = getIntent().getExtras().getString("id");
		chatFragment = new ChatFragment();
		chatFragment.setArguments(getIntent().getExtras());
		getSupportFragmentManager().beginTransaction().add(R.id.container, chatFragment).commit();
	}

	@Override
	public void onClick(View v) {
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		activityInstance = null;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		String username = intent.getStringExtra("id");
		if (toChatUsername.equals(username))
			super.onNewIntent(intent);
		else {
			finish();
			startActivity(intent);
		}
	}

	@Override
	public void onBackPressed() {
		chatFragment.onBackPressed();
	}

	public String getToChatUsername() {
		return toChatUsername;
	}

}
