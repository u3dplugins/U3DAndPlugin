package com.sdkplugin.bridge;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * 类名 : Android 6.0 以后权限申请 <br/>
 * 作者 : canyon / 龚阳辉 <br/>
 * 时间 : 2019-07-13 13：30 <br/>
 * 功能 : 在继承UnityPlayerActivity里面调用
 */
public class AndPermission extends AndBasic {
	// Manifest.permission.READ_EXTERNAL_STORAGE,
	// Manifest.permission.WRITE_EXTERNAL_STORAGE
	// Manifest.permission.READ_PHONE_STATE
	static int reqCode = 2;
	static int Code_1 = PackageManager.PERMISSION_GRANTED;
	static public boolean isReRequest = true;

	// 是否授权
	static final public boolean isGrant(Activity activity, String permission) {
		return ContextCompat.checkSelfPermission(activity, permission) == Code_1;
	}

	// 判断是否勾选禁止后不再询问 (是否可以再显示)
	static final public boolean isCanShowPermission(Activity activity, String permission) {
		return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
	}

	// 取得权限状态[0=已申请,1=未申请(可以申请),2=未申请(不能再申请)]
	static final public int grantState(Activity activity, String permission) {
		int _ret = 0;
		boolean isGrant = ContextCompat.checkSelfPermission(activity, permission) == Code_1;
		if (!isGrant) {
			boolean isCanShow = isCanShowPermission(activity, permission);
			_ret = isCanShow ? 1 : 2;
		}
		return _ret;
	}

	// 声明一个集合，在后面的代码中用来存储用户拒绝授权的权
	static final public void initPermissions(Activity activity, String... permissions) {
		if (permissions == null || permissions.length <= 0)
			return;

		List<String> _list = new ArrayList<>();
		int _st = -1;
		for (int i = 0; i < permissions.length; i++) {
			_st = grantState(activity, permissions[i]);
			if (_st == 1) {
				_list.add(permissions[i]);
			}
		}
		if (!_list.isEmpty()) {
			String[] _arrs = {};
			_arrs = _list.toArray(_arrs);// 将List转为数组
			ActivityCompat.requestPermissions(activity, _arrs, reqCode);
		}
	}

	static final public void onRequestPermissionsResult2(int requestCode, String[] permissions, int[] grantResults, Activity activity) {
		if (reqCode == requestCode) {
			for (int i = 0; i < grantResults.length; i++) {
				if (grantResults[i] != Code_1) {
					// 判断是否勾选禁止后不再询问
					if (isCanShowPermission(activity, permissions[i])) {
						// showToast("权限未申请");
					}
				}
			}
		}
	}

	static final public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults, Activity activity) {
		if (reqCode == requestCode) {
			if (isReRequest) {
				initPermissions(activity, permissions);
			}
		}
	}

	static final public void reqPermission(Activity activity, String permission) {
		initPermissions(activity, permission);
	}

	static final public void gotoPermission(Context context) {
		AndPhoneType pt = getPhoneType(true);
		switch (pt) {
		case PT_HUAWEI:
			gotoHuaweiPermission(context);
			break;
		case PT_XIAOMI:
			gotoMiuiPermission(context);// 小米
			break;
		case PT_MEIZU:
			gotoMeizuPermission(context);
			break;
		default:
			context.startActivity(getAppDetailSettingIntent(context));
			break;
		}
	}

	/**
	 * 跳转到miui的权限管理页面
	 */
	private static void gotoMiuiPermission(Context context) {
		Intent _itt = new Intent("miui.intent.action.APP_PERM_EDITOR");
		try { // MIUI 8
			_itt.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
			_itt.putExtra("extra_pkgname", context.getPackageName());
			context.startActivity(_itt);
		} catch (Exception e) {
			try { // MIUI 5/6/7
				_itt.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
				_itt.putExtra("extra_pkgname", context.getPackageName());
				context.startActivity(_itt);
			} catch (Exception e1) { // 否则跳转到应用详情
				context.startActivity(getAppDetailSettingIntent(context));
			}
		}
	}

	/**
	 * 跳转到魅族的权限管理系统
	 */
	private static void gotoMeizuPermission(Context context) {
		try {
			Intent _itt = new Intent("com.meizu.safe.security.SHOW_APPSEC");
			_itt.addCategory(Intent.CATEGORY_DEFAULT);
			_itt.putExtra("packageName", context.getPackageName());
			context.startActivity(_itt);
		} catch (Exception e) {
			context.startActivity(getAppDetailSettingIntent(context));
		}
	}

	/**
	 * 华为的权限管理页面
	 */
	private static void gotoHuaweiPermission(Context context) {
		try {
			Intent _itt = new Intent();
			_itt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");// 华为权限管理
			_itt.setComponent(comp);
			context.startActivity(_itt);
		} catch (Exception e) {
			context.startActivity(getAppDetailSettingIntent(context));
		}

	}

	/**
	 * 获取应用详情页面intent（如果找不到要跳转的界面，也可以先把用户引导到系统设置页面）
	 */
	private static Intent getAppDetailSettingIntent(Context context) {
		Intent _itt = new Intent();
		_itt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// 等价于 android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
		_itt.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
		_itt.setData(Uri.fromParts("package", context.getPackageName(), null));
		return _itt;
	}
}
