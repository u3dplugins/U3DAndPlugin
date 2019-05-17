package com.sdkplugin.bridge;

/**
 * 类名 : U3D <-> java 接口 <br/>
 * 作者 : canyon / 龚阳辉 <br/>
 * 时间 : 2016-05-30 10：01 <br/>
 * 功能 : 
 */
public interface IU3DListener {
	public void init(String ugojName, String ugobjMethod);

	public void receiveFromUnity(String json) throws Exception;

	public void response2Unity(String json) throws Exception;
}
