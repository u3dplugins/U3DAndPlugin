package com.sdkplugin.bridge;

/**
 * 类名 : U3D -> java 中间件 <br/>
 * 作者 : canyon / 龚阳辉 <br/>
 * 时间 : 2016-05-30 10：30 <br/>
 * 功能 :
 */
public abstract class AbsU3DListener extends AndU3DTools implements IU3DListener {

	static public final String CODE_SUCCESS = "success";
	static public final String CODE_WAIT = "wait";
	static public final String CODE_FAILS = "fails";
	static public final String CODE_ERROR = "error";
	static final String fmt_req = "=== %s,request=,msg = [%s]";
	static final String fmt_res = "=== %s,response=,msg = [%s]";

	protected String ugobjName = "";
	protected String ugobjMethod = "";

	static final protected int LEV_LOG_ALL = -1;
	static final protected int LEV_LOG_NORMAL = 0;
	static final protected int LEV_LOG_MUST = 5;
	static final protected int LEV_LOG_NONE = 10;

	protected int logLevel = LEV_LOG_ALL;
	protected String logHead = "plugin";

	protected void debugLog(String msg, int level, boolean isRequest) {
		if (logLevel >= LEV_LOG_NONE)
			return;

		if (level < logLevel || msg == null || msg.length() <= 0)
			return;

		String _v = String.format(isRequest ? fmt_req : fmt_res, logHead, msg);
		System.out.println(_v);
	}

	protected void logInfo(String msg, boolean isRequest) {
		debugLog(msg, LEV_LOG_NORMAL, isRequest);
	}

	protected void logInfo(String msg) {
		logInfo(msg, true);
	}

	protected void logMust(String msg, boolean isRequest) {
		debugLog(msg, LEV_LOG_MUST, isRequest);
	}

	protected void logMust(String msg) {
		logMust(msg, true);
	}

	@Override
	public void init(String ugojName, String ugobjMethod) {
		this.ugobjName = ugojName;
		this.ugobjMethod = ugobjMethod;
	}

	@Override
	public void response2Unity(String json) throws Exception {
		logInfo(json, false);
		sendMsg(this.ugobjName, this.ugobjMethod, json);
	}

}
