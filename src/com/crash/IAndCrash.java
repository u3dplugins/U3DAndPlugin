package com.crash;

/**
 * 类名 : 接口 - 发送崩溃信息到服务器 <br/>
 * 作者 : canyon / 龚阳辉 <br/>
 * 时间 : 2020-02-04 10：30 <br/>
 * 功能 :
 */
public interface IAndCrash {
	/**
	 * 发送异常信息到服务器
	 */
	void sendCrash2Sv(String info);
}
