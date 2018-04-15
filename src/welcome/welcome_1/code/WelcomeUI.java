package welcome.welcome_1.code;

import main.main_1.code.MainUI;
import android.content.Intent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

import com.shorigo.BaseUI;
import com.shorigo.utils.Constants;
import com.shorigo.utils.FileUtils;
import com.shorigo.yichat.R;

/**
 * 启动页
 * 
 * @author peidongxu
 * 
 */
public class WelcomeUI extends BaseUI {
	private ImageView iv_welcome;

	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.welcome_1);
	}

	@Override
	protected void findView_AddListener() {
		iv_welcome = (ImageView) findViewById(R.id.iv_welcome);
	}

	@Override
	protected void prepareData() {

		// 创建缓存文件
		FileUtils.createCacheFolder();
		// 屏幕宽度，高度
		int width = getWindowManager().getDefaultDisplay().getWidth();
		int height = getWindowManager().getDefaultDisplay().getHeight();
		Constants.width = width;
		Constants.height = height;

		// 加载启动动画
		AlphaAnimation animation = new AlphaAnimation(0.5f, 1.0f);
		animation.setDuration(500);
		iv_welcome.startAnimation(animation);
		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				startActivity(new Intent(WelcomeUI.this, MainUI.class));
				back();
			}
		});
	}

	@Override
	protected void onMyClick(View v) {
	}

	@Override
	protected void back() {
		finish();
	}

}