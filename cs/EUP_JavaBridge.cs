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
	AndroidJavaObject GetListener(string className){
		this._clsListener = className;
		if(dicJo.ContainsKey(className)){
			return (AndroidJavaObject)dicJo[className];
		}
		AndroidJavaObject jo = null;
		try{
			AndroidJavaClass _jc = new AndroidJavaClass( className );
			jo = _jc.CallStatic<AndroidJavaObject>("getInstance");
		}catch{
			jo = null;
		}
		try{
			if(jo == null){
				jo = new AndroidJavaObject(className);
			}
			dicJo.Add(className,jo);
		}catch{
		}
		return jo;
	}
	
	T call4Listener<T>(string className,string methodName, params object[] args){
		try{
			AndroidJavaObject jo = GetListener(className);
			if(jo != null){
				return jo.Call<T>(methodName,args);
			}
		}catch{
		}
		return default(T);
	}
	
	void call4Listener(string className,string methodName, params object[] args){
		try{
			AndroidJavaObject jo = GetListener(className);
			if(jo != null){
				jo.Call(methodName,args);
			}
		}catch{
		}
	}
	
	T callStatic4Class<T>(string className,string methodName, params object[] args){
		try{
			AndroidJavaClass _jc = new AndroidJavaClass( className );
			if(_jc != null){
				return _jc.CallStatic<T>(methodName,args);
			}
		}catch{
		}
		return default(T);
	}
	
	void callStatic4Class(string className,string methodName, params object[] args){
		try{
			AndroidJavaClass _jc = new AndroidJavaClass( className );
			if(_jc != null){
				_jc.CallStatic(methodName,args);
			}
		}catch{
		}
	}
#endif
	
	public void Init( string className,System.Action<string> onResult ) {
		this._callBack = onResult;
#if UNITY_ANDROID
		InitBridge();
		if(string.IsNullOrEmpty(className)){
			jcBridge.CallStatic(NM_JAVA_METHOD_INITPARS,NM_Gobj,NM_ON_RESULT_FUNC);
			return;
		}
		
		if(className.Equals(this._clsListener))
			return;
		
		AndroidJavaObject joListener = GetListener(className);

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
	
	public T call<T>(string className,string methodName, params object[] args){
#if UNITY_ANDROID
		return call4Listener<T>(className,methodName,args);
#else
		return default(T);
#endif
	}
	
	public void call(string className,string methodName, params object[] args){
#if UNITY_ANDROID
		call4Listener(className,methodName,args);
#endif
	}
	
	public T callStatic<T>(string className,string methodName, params object[] args){
#if UNITY_ANDROID
		return callStatic4Class<T>(className,methodName,args);
#else
		return default(T);
#endif
	}
	
	public void callStatic(string className,string methodName, params object[] args){
#if UNITY_ANDROID
		callStatic4Class(className,methodName,args);
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