package com.shorigo.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import com.shorigo.yichat.R;

/**
 * 弹出框工具类
 * 
 * @author peidongxu
 * 
 */
public class DialogUtil {
	private Dialog mDialog;
	private DialogClickCallBack dialogClick;
	private int width;

	/**
	 * 查看图片弹出框
	 */
	public void showLookImgDialog(Context context) {
		mDialog = new Dialog(context, R.style.custom_dialog_style);
		View view = View.inflate(context, R.layout.dialog_look_img, null);
		view.findViewById(R.id.other_view).setOnClickListener(cancleClickListener);
		view.findViewById(R.id.tv_dialog_look_img_send).setOnClickListener(onClickListener);
		view.findViewById(R.id.tv_dialog_look_img_save).setOnClickListener(onClickListener);

		mDialog.setContentView(view);
		mDialog.setCanceledOnTouchOutside(true);
		mDialog.show();
		setDialogAttributes(mDialog.getWindow(), width);
	}


	/**
	 * 支付弹出框
	 */
	public void showPayDialog(Context context) {
		mDialog = new Dialog(context, R.style.custom_dialog_style);
		View view = View.inflate(context, R.layout.dialog_pay, null);
		view.findViewById(R.id.other_view).setOnClickListener(cancleClickListener);
		view.findViewById(R.id.z_pay_alipay).setOnClickListener(onClickListener);
		view.findViewById(R.id.z_pay_wxpay).setOnClickListener(onClickListener);

		mDialog.setContentView(view);
		mDialog.setCanceledOnTouchOutside(true);
		mDialog.show();
		setDialogAttributes(mDialog.getWindow(), width);
	}

	/**
	 * 提示框弹出框
	 */
	public void showTipDialog(Context context, String content, boolean isHasCancle) {
		mDialog = new Dialog(context, R.style.custom_dialog_style);
		View view = View.inflate(context, R.layout.dialog_tip, null);

		TextView tv_content = (TextView) view.findViewById(R.id.tv_dialog_tip_content);
		if (!Utils.isEmity(content)) {
			tv_content.setText(content);
		}
		// 确定按钮
		view.findViewById(R.id.tv_dialog_tip_ok).setOnClickListener(onClickListener);
		// 取消按钮
		View tv_dialog_tip_cancel = view.findViewById(R.id.tv_dialog_tip_cancel);
		if (isHasCancle) {
			tv_dialog_tip_cancel.setOnClickListener(cancleClickListener);
			tv_dialog_tip_cancel.setVisibility(View.VISIBLE);
		} else {
			tv_dialog_tip_cancel.setVisibility(View.GONE);
		}
		mDialog.setContentView(view);
		mDialog.setCanceledOnTouchOutside(true);
		mDialog.show();
		setDialogAttributes(mDialog.getWindow(), width);
	}

	/**
	 * 提示框弹出框
	 */
	public void showTipDialog(Context context, String content) {
		mDialog = new Dialog(context, R.style.custom_dialog_style);
		View view = View.inflate(context, R.layout.dialog_tip, null);

		TextView tv_content = (TextView) view.findViewById(R.id.tv_dialog_tip_content);
		if (!Utils.isEmity(content)) {
			tv_content.setText(content);
		}
		// 确定按钮
		view.findViewById(R.id.tv_dialog_tip_ok).setOnClickListener(onClickListener);
		// 取消按钮
		view.findViewById(R.id.tv_dialog_tip_cancel).setOnClickListener(cancleClickListener);
		mDialog.setContentView(view);
		mDialog.setCanceledOnTouchOutside(true);
		mDialog.show();
		setDialogAttributes(mDialog.getWindow(), width);
	}

	/**
	 * 设置弹出框宽度
	 * 
	 * @param width
	 *            宽度
	 * @param isFull
	 *            宽度是否满屏
	 */
	public void setDialogWidth(int width, boolean isFull) {
		if (isFull) {
			this.width = width;
		} else {
			this.width = width - width / 10 * 2;
		}
	}

	public void setDialogWidth(int width) {
		this.width = width - width / 10 * 2;
	}

	/**
	 * 设置回调函数
	 */
	public void setCallBack(DialogClickCallBack dialogClick) {
		this.dialogClick = dialogClick;
	}

	/**
	 * 点击事件
	 */
	OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (dialogClick != null) {
				dialogClick.callBack(v);
			}
		}
	};

	/**
	 * 返回点击事件
	 */
	OnClickListener cancleClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			dismissDialog();
		}
	};

	/**
	 * 设置对话框的左右边距
	 * 
	 * @param window
	 *            窗口对象
	 * @param width
	 *            宽度
	 */
	public void setDialogAttributes(Window window, int width) {
		LayoutParams attributes = window.getAttributes();
		attributes.width = width;
		window.setAttributes(attributes);

		attributes = null;
	}

	/**
	 * 设置对话框的左右、上下边距
	 * 
	 * @param window
	 *            窗口对象
	 * @param width
	 *            宽度
	 */
	public void setDialogAttributes(Window window, int width, int height) {
		LayoutParams attributes = window.getAttributes();
		attributes.width = width;
		attributes.height = height;
		window.setAttributes(attributes);

		attributes = null;
	}

	/**
	 * 关闭弹出框
	 */
	public void dismissDialog() {
		if (mDialog != null) {
			mDialog.dismiss();
		}
	}

	/**
	 * 对话框回调接口
	 * 
	 * @author peidongxu
	 */
	public interface DialogClickCallBack {
		public void callBack(View v);
	}

}
