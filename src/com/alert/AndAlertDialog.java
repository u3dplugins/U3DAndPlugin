package com.alert;

import com.interfaces.IAndAlert;

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
	private Context mContext;
	private int nBtnType = 0;

	private AndAlertDialog() {
	}

	public AndAlertDialog init(@Nullable Context context, IAndAlert impl, int btnType) {
		this.mImpl = impl;
		this.mContext = context;
		this.nBtnType = btnType > 2 ? 0 : btnType;
		return this;
	}

	public AndAlertDialog init(@Nullable Context context, IAndAlert impl) {
		this.mImpl = impl;
		this.mContext = context;
		this.nBtnType = 0;
		return this;
	}

	public void show(@Nullable Context context, IAndAlert impl, @Nullable String title, @Nullable String msg, String btnSure, String btnCancel) {
		if (btnSure == null || btnSure.isEmpty())
			btnSure = "确定";
		if (btnCancel == null || btnCancel.isEmpty())
			btnCancel = "取消";

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(context).setTitle(title).setMessage(msg);
		boolean isBl = this.nBtnType == 0 || this.nBtnType == 1;
		if (isBl) {
			alertDialog.setPositiveButton(btnSure, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					excuteCall(true);
				}
			});
		}

		isBl = this.nBtnType == 0 || this.nBtnType == 2;
		if (isBl) {
			alertDialog.setNegativeButton(btnCancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					excuteCall(false);
				}
			});
		}
		alertDialog.show();
	}

	public void show(@Nullable String title, @Nullable String msg, String btnSure, String btnCancel) {
		show(this.mContext, this.mImpl, title, msg, btnSure, btnCancel);
	}

	public void show(@Nullable String title, @Nullable String msg, String btnSure) {
		show(this.mContext, this.mImpl, title, msg, btnSure, null);
	}

	public void show(@Nullable String title, @Nullable String msg) {
		show(this.mContext, this.mImpl, title, msg, null, null);
	}

	public void showCancel(@Nullable String title, @Nullable String msg, String btnCancel) {
		show(this.mContext, this.mImpl, title, msg, null, btnCancel);
	}

	final public void excuteCall(boolean isSure) {
		if (this.mImpl != null) {
			this.mImpl.onCall4ClickBtn(isSure);
		}
	}

	static final public AndAlertDialog build(@Nullable Context context, IAndAlert impl, int btnType) {
		AndAlertDialog _ret = new AndAlertDialog();
		return _ret.init(context, impl, btnType);
	}

	static final public AndAlertDialog build(@Nullable Context context, IAndAlert impl) {
		return build(context, impl, 0);
	}

	static final public AndAlertDialog buildOne(@Nullable Context context, IAndAlert impl, boolean isUseSure) {
		return build(context, impl, isUseSure ? 1 : 2);
	}

	static final public AndAlertDialog buildOne(@Nullable Context context, IAndAlert impl) {
		return buildOne(context, impl, true);
	}
}
