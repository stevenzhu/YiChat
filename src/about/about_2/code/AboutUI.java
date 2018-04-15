package about.about_2.code;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shorigo.BaseUI;
import com.shorigo.utils.Constants;
import com.shorigo.utils.Utils;
import com.shorigo.yichat.R;

/**
 * 关于我们
 * 
 * @author peidongxu
 * 
 */
public class AboutUI extends BaseUI {

	// 对话框宽度
	private TextView tv_phone;
	private LinearLayout z_phone;
	private String phone = "18101092112";
	private Dialog mDialog;

	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.about_2);
	}

	@Override
	protected void findView_AddListener() {
		z_phone = (LinearLayout) findViewById(R.id.z_phone);
		z_phone.setOnClickListener(this);
		tv_phone = (TextView) findViewById(R.id.tv_phone);
		tv_phone.setOnClickListener(this);
	}

	@Override
	protected void prepareData() {
		setTitle("关于我们");

		tv_phone.setText(phone);
	}

	@Override
	protected void onMyClick(View v) {
		switch (v.getId()) {
		case R.id.z_phone:
		case R.id.tv_phone:
			showPhoneCallDialog();
			break;
		case R.id.other_view:
		case R.id.btn_cancle:
			mDialog.dismiss();
			break;
		case R.id.btn_call:
			// 立即拨打
			mDialog.dismiss();
			callPhone();
			break;
		default:
			break;
		}
	}

	/**
	 * 拨打电话弹出框
	 */
	public void showPhoneCallDialog() {
		mDialog = new Dialog(this, R.style.custom_dialog_style);
		View view = View.inflate(this, R.layout.about_2_phone_call_dialog, null);
		view.findViewById(R.id.other_view).setOnClickListener(this);
		view.findViewById(R.id.btn_cancle).setOnClickListener(this);
		// 拨打电话
		view.findViewById(R.id.btn_call).setOnClickListener(this);
		mDialog.setContentView(view);
		mDialog.setCanceledOnTouchOutside(true);
		mDialog.show();
		LayoutParams attributes = mDialog.getWindow().getAttributes();
		attributes.width = Constants.width;
		mDialog.getWindow().setAttributes(attributes);
	}

	/**
	 * 打电话
	 * 
	 * @param phoneno
	 */
	private void callPhone() {
		phone = tv_phone.getText().toString();
		if (!Utils.isEmity(phone)) {
			Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
			startActivity(intent);
		}
	}

	@Override
	protected void back() {
		finish();
	}
}
