using UnityEngine;
using System.Collections.Generic;

/// <summary>
/// 类名 : U3D 与 平台 通讯
/// 作者 : Canyon / 龚阳辉
/// 日期 : 2016-05-22 10:15
/// 功能 : 
/// </summary>
public static class EU_Bridge {
	
	public delegate void CallBackBridge (string data);
	static System.Action<string> _sys_act_call = null;
	
	static public void Init(CallBackBridge onResult) {
		_sys_act_call = null;
		if(onResult != null){
			_sys_act_call = data => onResult(data);
		}
#if UNITY_EDITOR
#elif UNITY_ANDROID
		EUO_JavaBridge.instance.Init(_sys_act_call);
#elif UNITY_IOS
		EUP_IOSBridge.instance.Init(_sys_act_call);
#endif
	}
	
	static public void Send(string param){
#if UNITY_EDITOR
#elif UNITY_ANDROID
		EUO_JavaBridge.instance.SendToJava(param);
#elif UNITY_IOS
		EUP_IOSBridge.instance.SendToIOS(param);
#endif
	}
	
	static public void SendAndCall(string param,CallBackBridge onResult){
		Init (onResult);
		Send (param);
	}
}