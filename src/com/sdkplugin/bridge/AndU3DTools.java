package com.sdkplugin.bridge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 类名 : U3D -> JAVA工具类 <br/>
 * 作者 : canyon / 龚阳辉 <br/>
 * 时间 : 2019-06-27 21:30 <br/>
 * 功能 :
 */
public class AndU3DTools extends AndU3DScreenAdaptation {
	static final public boolean isEmptyTrim(String v) {
		if (null == v || v.length() <= 0)
			return true;
		return v.trim().length() <= 0;
	}

	/*** GET参数转换为map对象 */
	static final public Map<String, String> buildMapByQuery(String query) {
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

	static final public void msg2U3D(String msgData, IU3DListener ler) {
		U3DBridge.response(ler, msgData);
	}

	static final public void msg2U3D(String msgData) {
		msg2U3D(msgData, null);
	}

	static final public void msg2U3D(JSONObject msgJson, IU3DListener ler) {
		msg2U3D(msgJson.toString(), ler);
	}

	static final public void msg2U3D(JSONObject msgJson) {
		msg2U3D(msgJson, null);
	}

	static final public void msg2U3D(Map<?, ?> msgMap, IU3DListener ler) {
		msg2U3D(new JSONObject(msgMap), ler);
	}

	static final public void msg2U3D(Map<?, ?> msgMap) {
		msg2U3D(msgMap, null);
	}

	static final public void msg2U3D(String code, String msg, String data, IU3DListener ler) {
		Map<String, String> mapJson = new HashMap<String, String>();
		mapJson.put("code", code);
		mapJson.put("msg", isEmptyTrim(msg) ? "" : msg);
		mapJson.put("data", isEmptyTrim(data) ? "" : data);
		msg2U3D(mapJson, ler);
	}

	static final public void msg2U3D(String code, String msg, String data) {
		msg2U3D(code, msg, data, (IU3DListener) null);
	}

	static final public void msg2U3D(String code, String msg, JSONObject data, IU3DListener ler) {
		msg2U3D(code, msg, data == null ? null : data.toString(), ler);
	}

	static final public void msg2U3D(String code, String msg, JSONObject data) {
		msg2U3D(code, msg, data, null);
	}

	static final public void msg2U3D(String code, String msg, String cmd, JSONObject data, IU3DListener ler) {
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

	static final public void msg2U3D(String code, String msg, String cmd, JSONObject data) {
		msg2U3D(code, msg, cmd, data, null);
	}

	static final public void msg2U3D(String code, String msg, String cmd, Map<String, Object> data, IU3DListener ler) {
		String strData = "";
		if (cmd != null && !"".equals(cmd)) {
			try {
				strData = toData(cmd, data);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		msg2U3D(code, msg, strData, ler);
	}

	static final public void msg2U3D(String code, String msg, String cmd, Map<String, Object> data) {
		msg2U3D(code, msg, cmd, data, null);
	}

	static final public String toData(String cmd, String... kvals) throws Exception {
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

	static final public String toData(String cmd, Map<String, Object> map) throws Exception {
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
