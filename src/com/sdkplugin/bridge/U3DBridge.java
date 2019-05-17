package com.sdkplugin.bridge;

/**
 * 类名 : U3D <-> java 桥 <br/>
 * 作者 : canyon / 龚阳辉 <br/>
 * 时间 : 2016-05-30 10：30 <br/>
 * 功能 : 
 */
public class U3DBridge {

	private static IU3DListener uListener = null;
	private static String ugobjName = "";
	private static String ugobjMethod = "";

	static IU3DListener _defListener = null;

	static IU3DListener getDefListener() {
		if (_defListener == null) {
			_defListener = new com.sdkplugin.extend.PluginBasic();
		}
		return _defListener;
	}

	static public void setListener(IU3DListener listener) {
		uListener = listener;
	}

	static public IU3DListener getListener() {
		if (uListener == null)
			return getDefListener();
		return uListener;
	}

	static public void setListenerPars() {
		getListener().init(ugobjName, ugobjMethod);
	}

	static public void setPars(String nmGobj, String method) {
		ugobjName = nmGobj;
		ugobjMethod = method;
	}

	static public void init(String nmGobj, String method) {
		setPars(nmGobj, method);
		setListenerPars();
	}

	static public void initAll(IU3DListener listener, String ugojName,
			String ugobjMethod) {
		setListener(listener);
		init(ugojName, ugobjMethod);
	}

	static public void initPars(String nmGobj, String method) {
		init(nmGobj, method);
	}

	static public void request(String json) throws Exception {
		getListener().receiveFromUnity(json);
	}

	static public void response(String json) {
		try {
			getListener().response2Unity(json);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
