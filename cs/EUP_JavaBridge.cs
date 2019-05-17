using UnityEngine;
using System.Collections.Generic;

/// <summary>
/// 类名 : U3D 与 Android 通讯桥
/// 作者 : Canyon / 龚阳辉
/// 日期 : 2016-05-22 10:15
/// 功能 : 本类配合 SDKPlugin 工作，实现java 与  u3d 之间的通讯
/// 描述 : 消息监听者继承com.sdkplugin.extend.PluginBasic,可以通过 Init 函数初始化消息监听者
/// </summary>
public class EUP_JavaBridge : MonoSingleton<EUP_JavaBridge> {
	// java 类名
	const string NM_JAVA_BRIDGE_CLASS = "com.sdkplugin.bridge.U3DBridge";

	// 回调方法名
	const string NM_ON_RESULT_FUNC = "OnResult4Java";
	
	// java设置方法名
	const string NM_JAVA_METHOD_INITALL = "initAll";
	const string NM_JAVA_METHOD_INITPARS = "initPars";

	// java消息接受方法
	const string NM_JAVA_METHOD_NOTIFY = "request";

	protected Dictionary<string,object> dicJo = new Dictionary<string, object>();

	string _clsListener = "-1";
	
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
#endif
	}

#if UNITY_ANDROID
	AndroidJavaObject GetListener(string classListener){
		this._clsListener = classListener;
		if(dicJo.ContainsKey(classListener)){
			return (AndroidJavaObject)dicJo[classListener];
		}
		AndroidJavaObject jo = null;
		try{
			AndroidJavaClass _jc = new AndroidJavaClass( classListener );
			jo = _jc.CallStatic<AndroidJavaObject>("getInstance");
		}catch{
			jo = null;
		}
		try{
			if(jo == null){
				jo = new AndroidJavaObject(classListener);
			}
			dicJo.Add(classListener,jo);
		}catch{
		}
		return jo;
	}
#endif
	
	public void Init( string classListener,System.Action<string> onResult ) {
		this._callBack = onResult;
#if UNITY_ANDROID
		InitBridge();
		if(string.IsNullOrEmpty(classListener)){
			jcBridge.CallStatic(NM_JAVA_METHOD_INITPARS,NM_Gobj,NM_ON_RESULT_FUNC);
			return;
		}
		
		if(classListener.Equals(this._clsListener))
			return;
		
		AndroidJavaObject joListener = GetListener(classListener);

		if(joListener == null){
			jcBridge.CallStatic(NM_JAVA_METHOD_INITPARS,NM_Gobj,NM_ON_RESULT_FUNC);
			return;
		}

		jcBridge.CallStatic(NM_JAVA_METHOD_INITALL,joListener,NM_Gobj,NM_ON_RESULT_FUNC);
#endif
	}
	
	public void Init(System.Action<string> onResult) {
		Init("",onResult);
	}
	
	public void SendToJava( string param ){
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
			instance.Init (this._clsListener,this._callBack);
		}
	}

	void Clear(){
#if UNITY_ANDROID
		if(jcBridge != null){
			jcBridge.Dispose();
			jcBridge = null;
		}
#endif
		dicJo.Clear();
	}
}