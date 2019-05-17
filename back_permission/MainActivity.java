package com.example.androidpermissionmgr;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.os.Bundle;
import android.widget.Toast;

import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

public class MainActivity extends UnityPlayerActivity {

	public static MainActivity _instanceActivity;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_instanceActivity = this;
	}
	
	
	//获取apk的所有权限
	@SuppressWarnings("static-access")
	public void GetAllPermission() {
        StringBuffer sb = new StringBuffer();
        PackageManager pm = null;
        PackageInfo pkgInfo = null;
        if (pm == null) {
            pm = getPackageManager();
        }
        
        try {
        	String pkgName = getBaseContext().getPackageName();
            pkgInfo = pm.getPackageInfo(pkgName, PackageManager.GET_PERMISSIONS);
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        String[] perms = pkgInfo.requestedPermissions;
        
        //所以权限列表
        for (String permName: perms) {
            //sb.append(permName).append('\n');
            try {
                PermissionInfo permInfo = pm.getPermissionInfo(permName, 0);
                PermissionGroupInfo pgi = pm.getPermissionGroupInfo(permInfo.group, 0);
                //permInfo.PROTECTION_DANGEROUS
                final boolean isGranted = ((PackageInfo.REQUESTED_PERMISSION_GRANTED) != 0);
                //http://sanjay-f.github.io/2016/05/18/%E6%BA%90%E7%A0%81%E6%8E%A2%E7%B4%A2%E7%B3%BB%E5%88%9736---%E5%AE%89%E5%8D%93%E7%9A%84%E5%AE%89%E5%85%A8%E6%9C%BA%E5%88%B6permission/
                
                if(permInfo.PROTECTION_DANGEROUS ==1)//危险权限，Unity中逐个检查是否已获取权限
                {	
					 sb.append(permName).append('\n');
					 sb.append(permInfo.loadLabel(pm)).append('\n');
					 sb.append(permInfo.loadDescription(pm)).append("\n");
					 
					 sb.append(pgi.loadLabel(pm)).append('\n');
					 sb.append(pgi.loadDescription(pm)).append("\n\n");
                }               
                
            } catch (NameNotFoundException e) {
            	//正常权限
            }            
        }
        CollectPermissions("GetAllPermissions",sb.toString());
    }
	/*
     * 状态返回Unity 
     */
    public static void onCoderReturn(String state )
    {
        String gameObjectName = "GameObject";
        String methodName = "OnCodeReturn";
        String arg0 = state;
        UnityPlayer.UnitySendMessage(gameObjectName, methodName, arg0);
    }
    
    public static void CollectPermissions(String funcationName, String result)
    {
    	 String gameObjectName = "GameObject";
         String methodName = funcationName;
         String arg0 = result;
         UnityPlayer.UnitySendMessage(gameObjectName, methodName, arg0);
    }

    /*
     * Unity调用安卓的Toast
     */
  	public void UnityCallAndroidToast(final String toast )
  	{
  		runOnUiThread(new Runnable(){
  			@Override
  			public void run() {
  				//onCoderReturn("Android:UnityCallAndroidToast()");
  				/*  
  				 * 第一个参数：当前上下午的环境。可用getApplicationContext()或this  
  				 * 第二个参数：要显示的字符串  
  				 * 第三个参数：显示时间的长短。Toast有默认的两个LENGTH_SHORT（短）和LENGTH_LONG（长），也可以使用毫秒2000ms  
  				 * */  
  				Toast.makeText(MainActivity.this,toast,Toast.LENGTH_SHORT).show();  
  			}
  		});
  		
  	}
}
