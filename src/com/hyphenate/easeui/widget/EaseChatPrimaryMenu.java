package com.hyphenate.easeui.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.easeui.R;
import com.hyphenate.util.EMLog;

/**
 * 聊天底部操作按钮布局
 * 
 * @author peidongxu
 */
public class EaseChatPrimaryMenu extends EaseChatPrimaryMenuBase implements OnClickListener {
	private RelativeLayout z_message;
	private EditText et_message;
	private View btn_keyboard;
	private View btn_keyboard_right;
	private View btn_voice;
	private View btn_send;
	private View z_speak;
	private ImageView iv_biaoqing;
	private ImageView iv_add;
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
		LayoutInflater.from(context).inflate(R.layout.ease_widget_chat_primary_menu, this);
		et_message = (EditText) findViewById(R.id.et_message);
		btn_keyboard = findViewById(R.id.btn_keyboard);
		z_message = (RelativeLayout) findViewById(R.id.z_message);
		btn_voice = findViewById(R.id.btn_voice);
		btn_send = findViewById(R.id.btn_send);
		z_speak = findViewById(R.id.z_speak);
		btn_keyboard_right = findViewById(R.id.btn_keyboard_right);
		iv_biaoqing = (ImageView) findViewById(R.id.iv_biaoqing);
		iv_add = (ImageView) findViewById(R.id.iv_add);
		z_message.setBackgroundResource(R.drawable.ease_input_bar_bg_normal);

		btn_send.setOnClickListener(this);
		btn_keyboard.setOnClickListener(this);
		btn_voice.setOnClickListener(this);
		iv_add.setOnClickListener(this);
		iv_biaoqing.setOnClickListener(this);
		btn_keyboard_right.setOnClickListener(this);
		et_message.setOnClickListener(this);
		et_message.requestFocus();

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
		// listen the text change
		et_message.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (!TextUtils.isEmpty(s)) {
					iv_add.setVisibility(View.GONE);
					btn_send.setVisibility(View.VISIBLE);
				} else {
					iv_add.setVisibility(View.VISIBLE);
					btn_send.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		et_message.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				EMLog.d("key", "keyCode:" + keyCode + " action:" + event.getAction());
				// test on Mac virtual machine: ctrl map to KEYCODE_UNKNOWN
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
				EMLog.d("key", "keyCode:" + event.getKeyCode() + " action" + event.getAction() + " ctrl:" + ctrlPress);
				if (actionId == EditorInfo.IME_ACTION_SEND || (event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN && ctrlPress == true)) {
					String s = et_message.getText().toString();
					et_message.setText("");
					listener.onSendBtnClicked(s);
					return true;
				} else {
					return false;
				}
			}
		});

		z_speak.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (listener != null) {
					return listener.onPressToSpeakBtnTouch(v, event);
				}
				return false;
			}
		});
	}

	/**
	 * set recorder view when speak icon is touched
	 * 
	 * @param voiceRecorderView
	 */
	public void setPressToSpeakRecorderView(EaseVoiceRecorderView voiceRecorderView) {
		EaseVoiceRecorderView voiceRecorderView1 = voiceRecorderView;
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
			btn_voice.setVisibility(View.VISIBLE);
			btn_keyboard.setVisibility(View.GONE);
			btn_keyboard_right.setVisibility(View.GONE);
			iv_biaoqing.setVisibility(View.VISIBLE);
			z_message.setVisibility(View.VISIBLE);
			z_speak.setVisibility(View.GONE);
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
			btn_voice.setVisibility(View.VISIBLE);
			btn_keyboard.setVisibility(View.GONE);
			z_message.setVisibility(View.VISIBLE);
			z_speak.setVisibility(View.GONE);
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
		btn_voice.setVisibility(View.GONE);
		btn_keyboard.setVisibility(View.VISIBLE);
		btn_send.setVisibility(View.GONE);
		iv_add.setVisibility(View.VISIBLE);
		z_speak.setVisibility(View.VISIBLE);
		iv_biaoqing.setVisibility(View.VISIBLE);
		btn_keyboard_right.setVisibility(View.GONE);
	}

	/**
	 * show keyboard
	 */
	protected void setModeKeyboard() {
		z_message.setVisibility(View.VISIBLE);
		btn_keyboard.setVisibility(View.GONE);
		btn_keyboard_right.setVisibility(View.GONE);
		iv_biaoqing.setVisibility(View.VISIBLE);
		btn_voice.setVisibility(View.VISIBLE);
		z_speak.setVisibility(View.GONE);
		if (TextUtils.isEmpty(et_message.getText())) {
			iv_add.setVisibility(View.VISIBLE);
			btn_send.setVisibility(View.GONE);
		} else {
			iv_add.setVisibility(View.GONE);
			btn_send.setVisibility(View.VISIBLE);
		}
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
	public EditText getEditText() {
		return et_message;
	}

}
