package com.sdkplugin.bridge;

import android.content.Context;
import android.content.res.Resources;

/**
 * 类名 : 屏幕适配 <br/>
 * 作者 : canyon / 龚阳辉 <br/>
 * 时间 : 2019-06-27 21:30 <br/>
 * 功能 : 继承 AndU3DBasic,并实现取得国产手机的齐刘海逻辑
 */
public class AndU3DScreenAdaptation extends AndU3DBasic {
	/*
	 * hasNotch 是否是刘海屏手机
	 */
	static final private boolean is_huawei(Context context) {
		boolean ret = false;
		Object obj = _invoke(context, "com.huawei.android.util.HwNotchSizeUtil", "hasNotchInScreen");
		if (obj != null)
			ret = "TRUE".equalsIgnoreCase(obj.toString()) ? true : false;
		return ret;
	}

	/*
	 * notchSize 获取刘海尺寸： int[0]= 刘海width,int[1]=刘海height
	 */
	static final private int[] size_huawei(Context context) {
		int[] ret = null;
		Object obj = _invoke(context, "com.huawei.android.util.HwNotchSizeUtil", "getNotchSize");
		if (obj != null)
			ret = (int[]) obj;
		return ret;
	}

	/*
	 * 小米
	 */
	static final private boolean is_xiaomi(Context context) {
		boolean ret = false;
		Object obj = _invoke(context, "android.os.SystemProperties", "getInt", new Class[] { String.class, int.class }, "ro.miui.notch");
		if (obj != null)
			ret = "1".equalsIgnoreCase(obj.toString()) ? true : false;
		return ret;
	}

	static final private int[] size_xiaomi(Context context) {
		int _w = 0, _h = 0, resourceId = 0;
		Resources _res = context.getResources();
		resourceId = _res.getIdentifier("notch_width", "dimen", "android");
		if (resourceId > 0) {
			_w = _res.getDimensionPixelSize(resourceId);
		}
		resourceId = _res.getIdentifier("notch_height", "dimen", "android");
		if (resourceId > 0) {
			_h = _res.getDimensionPixelSize(resourceId);
		}
		if (_h > 0 || _w > 0)
			return new int[] { _w, _h };
		return null;
	}

	/*
	 * oppo
	 */
	static final private boolean is_oppo(Context context) {
		return context.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
	}

	static final private int[] size_oppo(Context context) {
		return new int[] { 324, 80 };
	}

	/*
	 * vivo
	 */
	static final private boolean is_vivo(Context context) {
		boolean ret = false;
		Object obj = _invoke(context, "android.util.FtFeature", "isFeatureSupport", new Class[] { int.class }, 0x00000020);
		if (obj != null)
			ret = "TRUE".equalsIgnoreCase(obj.toString()) ? true : false;
		return ret;
	}

	static final private int[] size_vivo(Context context) {
		return new int[] { 100, 27 };
	}

	/** 刘海width,刘海heigh */
	static final public int[] notchSize() {
		int[] ret = null;
		int aType = getPhoneTypeInt();
		Context context = getCurContext();
		switch (aType) {
		case 0:
			break;
		case 1:
			if (is_huawei(context))
				ret = size_huawei(context);
			break;
		case 2:
			if (is_xiaomi(context))
				ret = size_xiaomi(context);
			break;
		case 3:
			if (is_oppo(context))
				ret = size_oppo(context);
			break;
		case 4:
			if (is_vivo(context))
				ret = size_vivo(context);
			break;
		}
		return ret;
	}
}
