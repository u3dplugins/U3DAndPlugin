using UnityEngine;
using System.Collections.Generic;

/// <summary>
/// 类名 : U3D 与 Android 通讯桥
/// 作者 : Canyon / 龚阳辉
/// 日期 : 2016-05-22 10:15
/// 功能 : 本类配合 SDKPlugin 工作，实现java 与  u3d 之间的通讯
/// 描述 : 消息监听者继承com.sdkplugin.extend.PluginBasic,并且在MainActivity里初始化
/// </summary>
public class EUO_JavaBridge : MonoSingleton<EUO_JavaBridge> {
	// java 类名
	const string NM_JAVA_BRIDGE_CLASS = "com.sdkplugin.bridge.U3DBridge";
	
	// 回调方法名
	const string NM_ON_RESULT_FUNC = "OnResult4Java";
	
	// java设置方法名
	const string NM_JAVA_METHOD_INIT = "initPars";

	// java消息接受方法
	const string NM_JAVA_METHOD_NOTIFY = "request";

#if UNITY_ANDROID	
	// java连接对象
	AndroidJavaClass jcBridge;
#endif

	System.Action<string> _callBack;

	void InitBridge(){
#if UNITY_ANDROID
		if( jcBridge != null ){
			return;
		}
		jcBridge = new AndroidJavaClass( NM_JAVA_BRIDGE_CLASS );
		jcBridge.CallStatic( NM_JAVA_METHOD_INIT,NM_Gobj ,NM_ON_RESULT_FUNC);
#endif
	}
	
	public void Init(System.Action<string> onResult) {
		this._callBack = onResult;
#if UNITY_ANDROID
		InitBridge();
#endif
	}
	
	public void SendToJava(string param){
#if UNITY_ANDROID
		if(jcBridge != null){
			jcBridge.CallStatic(NM_JAVA_METHOD_NOTIFY, param);
		} else {
			Debug.LogWarning("SendToJava: jcBridge is null.");
		}
#endif
	}

	void OnResult4Java(string data){
		Log(data);
		if(_callBack != null){
			_callBack(data);
		} else {
			Debug.LogWarning("OnResult4Java: _callBack is null");
		}
	}

	protected override void OnCall4Destroy ()
	{
		Clear ();
		if (_isMustNewWhenDestroy) {
			instance.Init (this._callBack);
		}
	}

	void Clear(){
#if UNITY_ANDROID
		if(jcBridge != null){
			jcBridge.Dispose();
			jcBridge = null;
		}
#endif
	}
}