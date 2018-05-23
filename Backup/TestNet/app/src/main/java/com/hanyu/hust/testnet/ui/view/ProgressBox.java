package com.hanyu.hust.testnet.ui.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.Window;

import com.hanyu.hust.testnet.R;

/**
 * 进度条
 */
public class ProgressBox {
	public static ProgressDialog show(Context context, String content) {
		ProgressDialog dialog = new ProgressDialog(context, R.style.dialog);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setMessage(content);
		dialog.setCancelable(true);
		dialog.show();
		return dialog;
	}

	public static ProgressDialog showCancleable(Context context, String content) {
		ProgressDialog dialog = new ProgressDialog(context, R.style.dialog);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setMessage(content);
		dialog.setCancelable(false);
		dialog.show();
		return dialog;

	}
}