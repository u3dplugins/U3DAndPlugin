package com.crash;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Arrays;
import java.util.Date;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Environment;

/**
 * 类名 : Android手机异常崩溃捕获 <br/>
 * 作者 : canyon / 龚阳辉 <br/>
 * 时间 : 2020-02-04 09：30 <br/>
 * 功能 :
 */
public class AndCrashHandler implements UncaughtExceptionHandler {
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	private Context mContext;
	private IAndCrash mAndCrash;

	private AndCrashHandler() {
	}

	@Override
	public void uncaughtException(Thread t, Throwable ex) {
		ex.printStackTrace();
		try {
			String _time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			String _info = appendAllInfo(_time, ex);
			// 保存到本地
			saveToSDCard(_time, _info);
			// 下面也可以写上传的服务器的代码
			if (this.mAndCrash != null) {
				this.mAndCrash.sendCrash2Sv(_info);
			}
			// 如果系统提供了默认的异常处理器，则交给系统去结束程序，否则就自己结束自己
			if (this.mDefaultHandler != null) {
				this.mDefaultHandler.uncaughtException(t, ex);
			} else {
				exitGame();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * 导出异常信息到SD卡
	 */
	private void saveToSDCard(String time, String info) {
		// 判断SD卡是否存在
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return;
		}

		String _pd = Environment.getExternalStorageDirectory().getAbsolutePath();
		String _suffix = ".trace";

		String _nm = String.format("%s%s_crash_%s%s", _pd, File.separator, time, _suffix);
		try (FileWriter fw = new FileWriter(new File(_nm)); BufferedWriter bw = new BufferedWriter(fw)) {
			// 往文件中写入数据
			PrintWriter pw = new PrintWriter(bw);
			pw.println(info);
			pw.flush();
			pw.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 获取手机信息
	 */
	@SuppressWarnings("deprecation")
	private String appendPhoneInfo() {
		StringBuffer sb = new StringBuffer();
		try {
			PackageManager pm = mContext.getPackageManager();
			String pkg = mContext.getPackageName();
			PackageInfo pi = pm.getPackageInfo(pkg, PackageManager.GET_ACTIVITIES);
			// pkg名字
			sb.append("Package Name: ").append(pkg).append("\n");

			// App版本
			sb.append("App Version: ");
			sb.append(pi.versionName);
			sb.append("_");
			sb.append(pi.versionCode).append("\n");

			// Android版本号
			sb.append("OS Version: ");
			sb.append(Build.VERSION.RELEASE);
			sb.append("_");
			sb.append(Build.VERSION.SDK_INT).append("\n");

			// 手机制造商
			sb.append("Vendor: ");
			sb.append(Build.MANUFACTURER).append("\n");

			// 手机型号
			sb.append("Model: ");
			sb.append(Build.MODEL).append("\n");

			// CPU架构
			sb.append("CPU: ");
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				sb.append(Arrays.toString(Build.SUPPORTED_ABIS));
			} else {
				sb.append(Build.CPU_ABI);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			sb.append(ex.getMessage());
		}

		return sb.toString();
	}

	private String appendAllInfo(String time, Throwable ex) {
		StringBuffer sb = new StringBuffer();
		try (StringWriter sw = new StringWriter()) {
			PrintWriter pw = new PrintWriter(sw);
			pw.println(time);
			pw.println(appendPhoneInfo());
			pw.println("============");
			if (ex != null) {
				ex.printStackTrace(pw);
				Throwable cause = ex.getCause();
				while (cause != null) {
					cause.printStackTrace(pw);
					cause = cause.getCause();
				}
			}
			pw.close();
			String result = sw.toString();
			sb.append(result);
		} catch (Exception e) {
			e.printStackTrace();
			sb.append(e.getMessage());
		}
		return sb.toString();
	}

	void exitGame() {
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(1);
	}

	public void init(Context context, IAndCrash impl) {
		this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		this.mContext = context.getApplicationContext();
		this.mAndCrash = impl;
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	public void init(Context context) {
		init(context, null);
	}

	private static AndCrashHandler _instance = null;

	static final public AndCrashHandler getInstance() {
		if (_instance == null) {
			_instance = new AndCrashHandler();
		}
		return _instance;
	}
}
