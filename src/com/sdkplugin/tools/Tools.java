package com.sdkplugin.tools;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.sdkplugin.bridge.IU3DListener;
import com.sdkplugin.bridge.U3DBridge;

/**
 * 类名 : 工具类 <br/>
 * 作者 : canyon / 龚阳辉 <br/>
 * 时间 : 2016-05-30 10：30 <br/>
 * 功能 :
 */
@SuppressWarnings("rawtypes")
public class Tools {
	static public final boolean isEmptyTrim(String v) {
		if (null == v || v.length() <= 0)
			return true;
		return v.trim().length() <= 0;
	}

	/*** GET参数转换为map对象 */
	static public final Map<String, String> buildMapByQuery(String query) {
		Map<String, String> ret = new HashMap<String, String>();
		if (!isEmptyTrim(query)) {
			boolean isFirst = query.indexOf("?") == 0;
			if (isFirst)
				query = query.substring(1);
			String[] params = query.split("&");
			for (String item : params) {
				if (isEmptyTrim(item))
					continue;
				int index = item.indexOf("=");
				if (index < 0)
					continue;
				String k = item.substring(0, index);
				String v = item.substring(index + 1);
				if (ret.containsKey(k)) {
					v = ret.get(k) + "," + v;
				}
				ret.put(k, v);
			}
		}
		return ret;
	}

	static public void msg2U3D(String msgData, IU3DListener ler) {
		U3DBridge.response(ler, msgData);
	}

	static public void msg2U3D(String msgData) {
		msg2U3D(msgData, null);
	}

	static public void msg2U3D(JSONObject msgJson, IU3DListener ler) {
		msg2U3D(msgJson.toString(), ler);
	}

	static public void msg2U3D(JSONObject msgJson) {
		msg2U3D(msgJson, null);
	}

	static public void msg2U3D(Map msgMap, IU3DListener ler) {
		msg2U3D(new JSONObject(msgMap), ler);
	}

	static public void msg2U3D(Map msgMap) {
		msg2U3D(msgMap, null);
	}

	static public void msg2U3D(String code, String msg, String data, IU3DListener ler) {
		Map<String, String> mapJson = new HashMap<String, String>();
		mapJson.put("code", code);
		mapJson.put("msg", isEmptyTrim(msg) ? "" : msg);
		mapJson.put("data", isEmptyTrim(data) ? "" : data);
		msg2U3D(mapJson, ler);
	}

	static public void msg2U3D(String code, String msg, String data) {
		msg2U3D(code, msg, data, (IU3DListener) null);
	}

	static public void msg2U3D(String code, String msg, JSONObject data, IU3DListener ler) {
		msg2U3D(code, msg, data == null ? null : data.toString(), ler);
	}

	static public void msg2U3D(String code, String msg, JSONObject data) {
		msg2U3D(code, msg, data, null);
	}

	static public void msg2U3D(String code, String msg, String cmd, JSONObject data, IU3DListener ler) {
		if (cmd != null && !"".equals(cmd)) {
			if (data == null) {
				data = new JSONObject();
			}

			try {
				data.put("cmd", cmd);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		msg2U3D(code, msg, data, ler);
	}

	static public void msg2U3D(String code, String msg, String cmd, JSONObject data) {
		msg2U3D(code, msg, cmd, data, null);
	}

	static public void msg2U3D(String code, String msg, String cmd, Map<String, Object> data, IU3DListener ler) {
		String strData = "";
		if (cmd != null && !"".equals(cmd)) {
			try {
				strData = ToData(cmd, data);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		msg2U3D(code, msg, strData, ler);
	}

	static public void msg2U3D(String code, String msg, String cmd, Map<String, Object> data) {
		msg2U3D(code, msg, cmd, data, null);
	}

	static public String getTextInAssets(android.content.Context context, String fn) {
		try {
			InputStream inStream = context.getAssets().open(fn);
			int size = inStream.available();
			byte[] buf = new byte[size];
			inStream.read(buf);
			inStream.close();
			return new String(buf, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	static public String ToData(String cmd, String... kvals) throws Exception {
		JSONObject data = new JSONObject();
		data.put("cmd", cmd);

		if (kvals != null && kvals.length > 0) {
			int lens = kvals.length;
			if (lens == 1) {
				data.put("val", kvals[0]);
			} else {
				for (int i = 0; (i + 1) < lens; i += 2) {
					data.put(kvals[i], kvals[i + 1]);
				}
			}
		}
		return data.toString();
	}

	static public String ToData2(String cmd, String[] kvals) throws Exception {
		JSONObject data = new JSONObject();
		data.put("cmd", cmd);

		if (kvals != null && kvals.length > 0) {
			int lens = kvals.length;
			if (lens == 1) {
				data.put("val", kvals[0]);
			} else {
				for (int i = 0; (i + 1) < lens; i += 2) {
					data.put(kvals[i], kvals[i + 1]);
				}
			}
		}
		return data.toString();
	}

	static public String ToData(String cmd, Map<String, Object> map) throws Exception {
		JSONObject data = new JSONObject();
		data.put("cmd", cmd);

		if (map != null && map.size() > 0) {
			List<String> list = new ArrayList<String>();
			list.addAll(map.keySet());
			String key = "";
			for (int i = 0; i < list.size(); i++) {
				key = list.get(i);
				data.put(key, map.get(key));
			}
		}
		return data.toString();
	}
}
