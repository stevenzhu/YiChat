package me_card.me_card_1.code;

import java.util.Map;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.WriterException;
import com.shorigo.BaseUI;
import com.shorigo.utils.BitmapHelp;
import com.shorigo.utils.Constants;
import com.shorigo.utils.MyConfig;
import com.shorigo.utils.QrcodeBitmapUtil;
import com.shorigo.yichat.R;

/**
 * 二维码名片
 * 
 * @author peidongxu
 * 
 */
public class MeCardUI extends BaseUI {
	private ImageView iv_avatar, iv_sex;
	private TextView tv_user_nick, tv_address;
	private Map<String, String> userMap;

	private ImageView iv_qrcode;
	private Bitmap createQRCode;

	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.me_card_1);
	}

	@Override
	protected void findView_AddListener() {
		iv_avatar = (ImageView) findViewById(R.id.iv_avatar);
		iv_sex = (ImageView) findViewById(R.id.iv_sex);
		tv_user_nick = (TextView) findViewById(R.id.tv_user_nick);
		tv_address = (TextView) findViewById(R.id.tv_address);
		iv_qrcode = (ImageView) findViewById(R.id.iv_qrcode);
		iv_qrcode.setOnClickListener(this);
	}

	@Override
	protected void prepareData() {
		setTitle("二维码名片");

		setValue();
	}

	@Override
	protected void onMyClick(View v) {

	}

	/**
	 * 页面赋值
	 */
	private void setValue() {
		userMap = MyConfig.getUserInfo(this);
		if (userMap == null) {
			return;
		}

		BitmapHelp.loadImg(this, iv_avatar, userMap.get("avatar"), R.drawable.default_avatar);
		tv_user_nick.setText(userMap.get("user_nick"));
		// 性别1：男 2：女
		String sex = userMap.get("sex");
		if ("1".equals(sex)) {
			iv_sex.setImageResource(R.drawable.icon_boy);
		} else if ("2".equals(sex)) {
			iv_sex.setImageResource(R.drawable.icon_girl);
		}
		tv_address.setText(userMap.get("city_name") + userMap.get("address"));

		setQrcode();
	}

	/**
	 * 设置二维码
	 */
	private void setQrcode() {
		if (userMap == null) {
			return;
		}
		try {
			// createQRCode =
			// EncodingHandler.createQRCode(userMap.get("user_id"), 900);
			// 生成彩色二维码
			// bitmap = BitmapUtil.makeQRImage(trim, 400,400);
			// 中间头像转成bitmap类型
			createQRCode = QrcodeBitmapUtil.gainBitmap(getApplicationContext(), R.drawable.logo);
			// 加头像把头像放到二维码里 1.头像2.获取要转化成二维码的信息3.宽4.高
			createQRCode = QrcodeBitmapUtil.makeQRImage(createQRCode, Constants.PREFIX + userMap.get("user_id"), 900, 900);
			if (createQRCode != null) {
				iv_qrcode.setImageBitmap(createQRCode);
			}
		} catch (WriterException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void back() {
		finish();
	}

}
