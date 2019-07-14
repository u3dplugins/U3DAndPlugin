package com.sdkplugin.bridge;

import java.util.ArrayList;
import java.util.List;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * 类名 : Android 6.0 以后权限申请 <br/>
 * 作者 : canyon / 龚阳辉 <br/>
 * 时间 : 2019-07-13 13：30 <br/>
 * 功能 : 在继承UnityPlayerActivity里面调用
 */
public class AndPermission {
	static int reqCode = 2; // 10000

	// 声明一个集合，在后面的代码中用来存储用户拒绝授权的权
	static public void initPermissions(Activity activity, String... permissions) {
		if (permissions == null || permissions.length <= 0)
			return;

		List<String> _list = new ArrayList<>();
		for (int i = 0; i < permissions.length; i++) {
			if (ContextCompat.checkSelfPermission(activity, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
				_list.add(permissions[i]);
			}
		}
		if (!_list.isEmpty()) {
			String[] _arrs = {};
			_arrs = _list.toArray(_arrs);// 将List转为数组
			ActivityCompat.requestPermissions(activity, _arrs, reqCode);
		}
	}

	static public void initPermissions(Activity activity) {
		String[] perm = { Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE };
		initPermissions(activity, perm);
	}

	static public void initPermissions() {
		initPermissions(AndU3DBasic.getCurActivity());
	}

	static public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults, Activity activity) {
		if (reqCode == requestCode) {
			for (int i = 0; i < grantResults.length; i++) {
				if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
					// 判断是否勾选禁止后不再询问
					boolean showRequestPermission = ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[i]);
					if (showRequestPermission) {
						// showToast("权限未申请");
					}
				}
			}
		}
	}

	static public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		onRequestPermissionsResult(requestCode, permissions, grantResults, AndU3DBasic.getCurActivity());
	}
}
