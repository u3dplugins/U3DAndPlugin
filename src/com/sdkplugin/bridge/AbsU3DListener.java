package com.sdkplugin.bridge;

/**
 * 类名 : U3D -> java 中间件 <br/>
 * 作者 : canyon / 龚阳辉 <br/>
 * 时间 : 2016-05-30 10：30 <br/>
 * 功能 :
 */
public abstract class AbsU3DListener extends AndU3DTools implements
		IU3DListener {

	static public final String CODE_SUCCESS = "success";
	static public final String CODE_WAIT = "wait";
	static public final String CODE_FAILS = "fails";
	static public final String CODE_ERROR = "error";

	protected String ugobjName = "";
	protected String ugobjMethod = "";

	static final protected int LEV_LOG_ALL = -1;
	static final protected int LEV_LOG_NORMAL = 0;
	static final protected int LEV_LOG_MUST = 5;
	static final protected int LEV_LOG_NONE = 10;
	
	protected int logLevel = LEV_LOG_ALL;
	protected String logHead = "plugin";

	protected void logInfo(String msg) {
		debugLog(msg, LEV_LOG_NORMAL);
	}
	
	protected void logMust(String msg) {
		debugLog(msg, LEV_LOG_MUST);
	}

	protected void debugLog(String msg, int level) {
		if(logLevel >= LEV_LOG_NONE)
			return;
		
		if (level < logLevel || msg == null || msg.length() <= 0)
			return;

		System.out.println(String.format("== %s ==,msg = [%s]", logHead, msg));
	}

	@Override
	public void init(String ugojName, String ugobjMethod) {
		this.ugobjName = ugojName;
		this.ugobjMethod = ugobjMethod;
	}

	@Override
	public void response2Unity(String json) throws Exception {
		sendMsg(this.ugobjName, this.ugobjMethod, json);
	}

}
