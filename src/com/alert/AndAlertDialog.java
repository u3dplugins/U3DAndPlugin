package com.alert;

import com.sdkplugin.extend.PluginBasic;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;

/**
 * 类名 : Android 对话框 <br/>
 * 作者 : canyon / 龚阳辉 <br/>
 * 时间 : 2020-03-21 13:30 <br/>
 * 功能 :
 */
public class AndAlertDialog {
	private IAndAlert mImpl;

	private AndAlertDialog() {
	}

	public void show(Context context, IAndAlert impl, @Nullable String title, @Nullable String msg, String btnSure, String btnCancel) {
		this.mImpl = impl;
		if (btnSure == null || btnSure.isEmpty())
			btnSure = "确定";
		if (btnCancel == null || btnCancel.isEmpty())
			btnCancel = "取消";

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(context).setTitle(title).setMessage(msg).setPositiveButton(btnSure, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				excuteCall(true);
			}
		}).setNegativeButton(btnCancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				excuteCall(false);
			}
		});
		alertDialog.show();
	}

	public void show(IAndAlert impl, @Nullable String title, @Nullable String msg, String btnSure, String btnCancel) {
		show(PluginBasic.getCurContext(), impl, title, msg, btnSure, btnCancel);
	}

	final public void excuteCall(boolean isSure) {
		if (this.mImpl != null) {
			this.mImpl.onCall4ClickBtn(isSure);
		}
	}

	private static AndAlertDialog _instance = null;

	static final public AndAlertDialog getInstance() {
		if (_instance == null) {
			_instance = new AndAlertDialog();
		}
		return _instance;
	}
}
