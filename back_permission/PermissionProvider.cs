using UnityEngine;
using System.Collections;
using System;
using Platform.Android.Stubs;
 
namespace Platform.Android
{
    public class PermissionProvider : MonoBehaviour
    {
 
        public static event Action<bool> OnReceiveStoragePermission;
        #if UNITY_ANDROID
        private AndroidJavaClass _request;
        public  AndroidJavaClass Request {
            get {
                if (_request == null)
                    _request = new AndroidJavaClass ("com.example.androidpermissionmgr.PermissionProvider");
                return _request;
            }
        }
        #else
         
        public AndroidJavaClassStub Request = new AndroidJavaClassStub ();
         
        #endif
 
        public void VerifyStorage (Action<bool> action)
        {
            Debug.Log ("[PermissionProvider] VerifyStorage" );
            OnReceiveStoragePermission += action;
            RequestPermissions (1001, "android.permission.READ_EXTERNAL_STORAGE", "android.permission.ACCESS_NETWORK_STATE");//android.permission.ACCESS_NETWORK_STATE  "android.permission.WRITE_EXTERNAL_STORAGE"
 
        }
 
        public void RequestPermissions (int requestCode, params string[]permissions)
        {
            Debug.Log ("[PermissionProvider] Requesting permissions: "+ requestCode + " " + permissions );
            Request.CallStatic ("verifyPermissions", gameObject.name, requestCode, permissions);
        }
 
        public void OnGranted (string requestCode)
        {
            Debug.Log("[PermissionProvider] OnGranted " + requestCode);
            Example.result = "[PermissionProvider] OnGranted " + requestCode;
 
            if (requestCode.Equals("1001"))
            {
                if (OnReceiveStoragePermission != null)
                    OnReceiveStoragePermission(true);
            }
            if (Example.permissionDic.ContainsValue(int.Parse(requestCode)))
            {
                foreach (var item in Example.permissionDic)
                {
                    if (item.Value == int.Parse(requestCode))
                    {
                        Example.permissionDic.Remove(item.Key);
                        break;
                    }
                }
            }
            CheckPermissionAgain();
        }
 
        public void OnDenied (string requestCode)
        {
            Debug.Log("[PermissionProvider] OnDenied " + requestCode);
            Example.result += "[PermissionProvider] OnDenied " + requestCode;
            if (requestCode.Equals("1001"))
            {
                if (OnReceiveStoragePermission != null)
                    OnReceiveStoragePermission(false);
            }
            //只要有一个重要权限未授权直接退出，或者再次请求权限
            if (Example.permissionDic.Count>0)
            {
                Application.Quit();
            }
        }
 
        public void AskForPermission()
        {
            RequestPermissions(100, "android.permission.READ_CONTACTS");//android.permission.ACCESS_NETWORK_STATE  "android.permission.WRITE_EXTERNAL_STORAGE"
 
            Example.result += "AskForPermission";
        }
 
        public void GetNoGrantPermission()
        {
            Request.Call("GetNoGrantPermission");
        }
 
        public void AskForAllPermission()
        {
            foreach (var item in Example.permissionDic)
            {
                RequestPermissions(item.Value, item.Key);
            }
        }
 
        public void CheckPermissionAgain()
        {
            if (Example.permissionDic.Count > 0)
            {
                AskForAllPermission();
            }
            else
            {
                Example.result = "APK已全部授权成功!";
            }
        }
    }
}