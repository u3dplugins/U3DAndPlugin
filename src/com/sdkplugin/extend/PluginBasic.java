package com.sdkplugin.extend;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.sdkplugin.bridge.AbsU3DListener;
import com.sdkplugin.tools.Tools;

import android.content.Intent;
import android.net.Uri;

/**
 * 类名 : U3D -> java 消息处理 <br/>
 * 作者 : canyon / 龚阳辉 <br/>
 * 时间 : 2016-05-30 10：30 <br/>
 * 功能 : 相当于一个默认消息
 */
public class PluginBasic extends AbsU3DListener {

	protected Map<String, Object> mapData = new HashMap<String, Object>();
	protected String jsonData = "";

	private void reInit() {
		jsonData = "";
		mapData.clear();
	}

	@Override
	public void receiveFromUnity(String json) throws Exception {
		if (json == null || json.length() <= 0) {
			logInfo("json is null or empty");
			return;
		}
		logInfo(json);

		JSONObject obj = null;
		boolean isThrow = false;
		try {
			reInit();
			obj = new JSONObject(json);
			final String cmd = obj.getString("cmd");
			if (obj.has("isThrow")) {
				isThrow = obj.getBoolean("isThrow");
			}
			handlerJson(cmd, obj);
		} catch (Exception ex) {
			if (isThrow) {
				throw ex;
			} else {
				ex.printStackTrace();
				Tools.msg2U3D(CODE_ERROR, ex.getMessage(), obj, this);
			}
		} finally {
			reInit();
		}
	}

	private void handlerJson(final String cmd, JSONObject data) throws Exception {
		switch (cmd) {
		case "getPackageInfo":
			Tools.msg2U3D(Tools.getTextInAssets(getCurContext(), data.getString("filename")), this);
			break;
		case "kill":
			killSelf();
			break;
		case "logLev":
			if (data.has("logLev")) {
				this.logLevel = data.getInt("logLev");
			}
			break;
		case "phoneInfo":
			Tools.msg2U3D(CODE_SUCCESS, "", cmd, getPhoneState(mapData), this);
			break;
		case "phone_mem_cpu":
			Tools.msg2U3D(CODE_SUCCESS, "", cmd, getMemCpuInfo(mapData), this);
			break;
		case "notchSize": {
			int[] _wh = notchSize();
			int _w = 0, _h = 0;
			boolean isNotch = (_wh != null);
			mapData.put("isNotch", isNotch);
			if (isNotch) {
				_w = _wh[0];
				_h = _wh[1];
			}
			mapData.put("w", _w);
			mapData.put("h", _h);
			Tools.msg2U3D(CODE_SUCCESS, "", cmd, mapData, this);
			break;
		}
		default:
			handlerMsg(cmd, data);
			break;
		}
	}

	protected void handlerMsg(final String cmd, JSONObject data) throws Exception {
		String strVal1 = "", strVal2 = "";
		boolean isVal1 = false;
		switch (cmd) {
		case "mapInfo":
			mapData.put("code", CODE_SUCCESS);
			mapData.put("val", "这是一个demo");
			Tools.msg2U3D(mapData);
			break;
		case "jsonInfo":
			jsonData = Tools.ToData(cmd, "val", "这是一个demo");
			Tools.msg2U3D(CODE_SUCCESS, "", jsonData);
			break;
		case "isInApk":
			// 判断是否安装了某个apk
			if (data.has("pkgName")) {
				strVal1 = data.getString("pkgName");
			}
			if (!"".equals(strVal1)) {
				isVal1 = isInstalledApk(strVal1); // "com.facebook.katana"
			}
			data.put("isInApk", isVal1);
			Tools.msg2U3D(CODE_SUCCESS, "", cmd, data, this);
			break;
		case "shareImg":
			// 图片分享
			if (data.has("filepath")) {
				strVal1 = data.getString("filepath");
			}
			if (data.has("nType")) {
				strVal2 = data.getString("nType");
			}
			if (!"".equals(strVal1) && !"".equals(strVal2)) {
				if ("instagram".equalsIgnoreCase(strVal2)) {
					isVal1 = true;
					shareInstagramIntent("image/*", strVal1);
				}
			}
			mapData.put("isState", isVal1);
			Tools.msg2U3D(CODE_SUCCESS, "", cmd, mapData, this);
			break;
		default:
			Tools.msg2U3D(CODE_SUCCESS, "", String.format("{\"cmd\":\"%s\",\"val\":\"这是一个demo\"}", cmd), this);
			break;
		}
	}

	/** type[image/*,video/*] **/
	private void shareInstagramIntent(String type, String mediaPath) {
		Intent share = new Intent(Intent.ACTION_SEND);
		share.setType(type);
		File media = new File(mediaPath);
		Uri uri = Uri.fromFile(media);
		share.putExtra(Intent.EXTRA_STREAM, uri);
		getCurActivity().startActivity(Intent.createChooser(share, "Share to"));
	}
}
