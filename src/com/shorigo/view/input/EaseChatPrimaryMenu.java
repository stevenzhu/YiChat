package com.shorigo.view.input;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shorigo.yichat.R;

/**
 * 底部输入框操作按钮布局
 * 
 * @author peidongxu
 */
public class EaseChatPrimaryMenu extends EaseChatPrimaryMenuBase implements OnClickListener {
	private RelativeLayout z_message;
	private EditText et_message;
	private View btn_keyboard_right;
	private View z_send;
	private View btn_send;
	private ImageView iv_biaoqing;
	private boolean ctrlPress = false;

	public EaseChatPrimaryMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	public EaseChatPrimaryMenu(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public EaseChatPrimaryMenu(Context context) {
		super(context);
		init(context, null);
	}

	private void init(final Context context, AttributeSet attrs) {
		LayoutInflater.from(context).inflate(R.layout.input_widget_primary_menu, this);
		et_message = (EditText) findViewById(R.id.et_message);
		z_message = (RelativeLayout) findViewById(R.id.z_message);
		z_send = findViewById(R.id.z_send);
		btn_send = findViewById(R.id.btn_send);
		btn_keyboard_right = findViewById(R.id.btn_keyboard_right);
		iv_biaoqing = (ImageView) findViewById(R.id.iv_biaoqing);
		z_message.setBackgroundResource(R.drawable.ease_input_bar_bg_normal);

		btn_send.setOnClickListener(this);
		iv_biaoqing.setOnClickListener(this);
		btn_keyboard_right.setOnClickListener(this);
		et_message.setOnClickListener(this);

		et_message.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					z_message.setBackgroundResource(R.drawable.ease_input_bar_bg_active);
				} else {
					z_message.setBackgroundResource(R.drawable.ease_input_bar_bg_normal);
				}

				iv_biaoqing.setVisibility(View.VISIBLE);
				btn_keyboard_right.setVisibility(View.GONE);
				if (listener != null)
					listener.onEditTextClicked();
			}
		});

		et_message.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_UNKNOWN) {
					if (event.getAction() == KeyEvent.ACTION_DOWN) {
						ctrlPress = true;
					} else if (event.getAction() == KeyEvent.ACTION_UP) {
						ctrlPress = false;
					}
				}
				return false;
			}
		});

		et_message.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEND || (event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN && ctrlPress == true)) {
					String s = et_message.getText().toString();
					et_message.setText("");
					hintKbOne();
					listener.onSendBtnClicked(s);
					return true;
				} else {
					return false;
				}
			}
		});
	}

	// 此方法，如果显示则隐藏，如果隐藏则显示
	private void hintKbOne() {
		InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		// 得到InputMethodManager的实例
		if (imm.isActive()) {
			// 如果开启
			imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	/**
	 * append emoji icon to editText
	 * 
	 * @param emojiContent
	 */
	public void onEmojiconInputEvent(CharSequence emojiContent) {
		et_message.append(emojiContent);
	}

	/**
	 * delete emojicon
	 */
	public void onEmojiconDeleteEvent() {
		if (!TextUtils.isEmpty(et_message.getText())) {
			KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
			et_message.dispatchKeyEvent(event);
		}
	}

	/**
	 * on clicked event
	 * 
	 * @param view
	 */
	@Override
	public void onClick(View view) {
		int id = view.getId();
		if (id == R.id.btn_send) {
			if (listener != null) {
				String s = et_message.getText().toString();
				et_message.setText("");
				hideKeyboard();
				listener.onSendBtnClicked(s);
			}
		} else if (id == R.id.btn_voice) {
			setModeVoice();
			if (listener != null)
				listener.onToggleVoiceBtnClicked();
		} else if (id == R.id.btn_keyboard) {
			setModeKeyboard();
			if (listener != null)
				listener.onToggleVoiceBtnClicked();
		} else if (id == R.id.iv_add) {
			btn_keyboard_right.setVisibility(View.GONE);
			iv_biaoqing.setVisibility(View.VISIBLE);
			z_message.setVisibility(View.VISIBLE);
			if (listener != null)
				listener.onToggleExtendClicked();
		} else if (id == R.id.et_message) {
			z_message.setBackgroundResource(R.drawable.ease_input_bar_bg_active);
			iv_biaoqing.setVisibility(View.VISIBLE);
			btn_keyboard_right.setVisibility(View.GONE);
			if (listener != null)
				listener.onEditTextClicked();
		} else if (id == R.id.btn_keyboard_right) {
			setModeKeyboard();
			if (listener != null)
				listener.onToggleVoiceBtnClicked();
		} else if (id == R.id.iv_biaoqing) {
			z_message.setVisibility(View.VISIBLE);
			btn_keyboard_right.setVisibility(View.VISIBLE);
			iv_biaoqing.setVisibility(View.GONE);
			if (listener != null) {
				listener.onToggleEmojiconClicked();
			}
		}
	}

	/**
	 * show voice icon when speak bar is touched
	 * 
	 */
	protected void setModeVoice() {
		hideKeyboard();
		z_message.setVisibility(View.GONE);
		z_send.setVisibility(View.GONE);
		btn_send.setVisibility(View.GONE);
		iv_biaoqing.setVisibility(View.VISIBLE);
		btn_keyboard_right.setVisibility(View.GONE);
	}

	/**
	 * show keyboard
	 */
	protected void setModeKeyboard() {
		z_message.setVisibility(View.VISIBLE);
		btn_keyboard_right.setVisibility(View.GONE);
		iv_biaoqing.setVisibility(View.VISIBLE);
		z_send.setVisibility(View.VISIBLE);
		btn_send.setVisibility(View.VISIBLE);
		et_message.requestFocus();
	}

	@Override
	public void onExtendMenuContainerHide() {
	}

	@Override
	public void onTextInsert(CharSequence text) {
		int start = et_message.getSelectionStart();
		Editable editable = et_message.getEditableText();
		editable.insert(start, text);
		setModeKeyboard();
	}

	@Override
	public void onTextSetHint(CharSequence text) {
		et_message.setHint(text);
		setModeKeyboard();
	}

	@Override
	public EditText getEditText() {
		return et_message;
	}

}
