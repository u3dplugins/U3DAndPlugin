package com.sdkplugin.bridge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 类名 : Android 6.0 以后权限申请 <br/>
 * 作者 : canyon / 龚阳辉 <br/>
 * 时间 : 2018-09-07 10：30 <br/>
 * 功能 : 权限改革后的权限申请处理
 * 参考 https://blog.csdn.net/xietansheng/article/details/54315674
 */
@SuppressLint("All")
public class AndU3DPermissionProvider extends Fragment {
	static final String PARS_PERMISSIONS = "Permissions";
	static final String PARS_REQUEST_CODE = "RequestCode";

	int requestCode = 10000;
	List<String> allPermissions = new ArrayList<String>();
	List<String> needPermissions = new ArrayList<String>();

	public AndU3DPermissionProvider() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final Bundle bundle = getArguments();
		if (bundle != null) {
			allPermissions.clear();
			final String[] permissions = bundle.getStringArray(PARS_PERMISSIONS);
			if (permissions != null) {
				Collections.addAll(allPermissions, permissions);
			}
			checkThemePermissions();
		}
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	void checkThemePermissions() {
		// Check and request for permissions for Android M and older versions
		needPermissions.clear();
		// 检查权限
		int lens = allPermissions.size();
		String perm = "";
		for (int i = 0; i < lens; i++) {
			perm = allPermissions.get(i);
			if (getActivity().checkSelfPermission(perm) != PackageManager.PERMISSION_GRANTED) {
				needPermissions.add(perm);
			}
		}
		lens = needPermissions.size();
		if (lens > 0) {
			// _shouldShowRequestPermissionRationale();
			// 请求权限
			this.requestPermissions(needPermissions.toArray(new String[lens]), requestCode);
		} else {
			// 完成
			doCompletePermission();
		}
	}
	
	// 解释权限(可选)
	void _shouldShowRequestPermissionRationale() {
		if (needPermissions.size()<=0) {
			return;
		}
		boolean pr = false;
		for (final String p : needPermissions) {
			pr = shouldShowRequestPermissionRationale(p);
			if (pr) {
				break;
			}
		}
		if(pr) {
			//如果应用第一次请求过此权限，但是被用户拒绝了，则之后调用该方法将返回 true，此时就有必要向用户详细说明需要此权限的原因
		}
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (this.requestCode == requestCode) {
            boolean isAllGranted = true;
            // 判断是否所有的权限都已经授予了
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }

            if (isAllGranted) {
                // 如果所有的权限都授予了, 则执行备份代码
                doCompletePermission();
            } else {
                // 弹出对话框告诉用户需要权限的原因, 并引导用户去应用权限管理中手动打开权限按钮
                openAppDetails();
            }
        }
    }

	/** * 打开 APP 的详情设置    */
    private void openAppDetails() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("请到 “应用信息 -> 权限” 中授予！");
        builder.setPositiveButton("去手动授权", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + getContext().getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }
    
    // 权限获取完毕
    private void doCompletePermission() {
    	allPermissions.clear();
    	needPermissions.clear();
    }

	/*
	 * 判断是否获取到相机权限
	 */
	public void CheckPermission() {
		allPermissions.clear();
		allPermissions.add(Manifest.permission.CAMERA);
		checkThemePermissions();
	}

	static public void verifyPermissions(final int requestCode, final String[] permissions) {
		AndU3DBasic.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				final Bundle bundle = new Bundle();
				bundle.putInt(PARS_REQUEST_CODE, requestCode);
				bundle.putStringArray(PARS_PERMISSIONS, permissions);
				final AndU3DPermissionProvider fragment = new AndU3DPermissionProvider();
				fragment.setArguments(bundle);
				AndU3DBasic.getCurActivity().getFragmentManager().beginTransaction().add(fragment, AndU3DPermissionProvider.class.getCanonicalName()).commit();
			}
		});
	}
	
	static public void verifyPermissions(final String[] permissions) {
		verifyPermissions(10000, permissions);
	}
}
