package com.sdkplugin.bridge;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.unity3d.player.UnityPlayer;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;

/**
 * 类名 : android u3d 基础类 <br/>
 * 作者 : canyon / 龚阳辉 <br/>
 * 时间 : 2016-05-30 10：30 <br/>
 * 功能 : 取得android相关的信息
 */
public class AndU3DBasic extends AndBasic {

	// 主要的activity对象
	static public Activity m_curActivity = null;

	static final public Activity getCurActivity() {
		if (m_curActivity == null)
			return UnityPlayer.currentActivity;
		return m_curActivity;
	}

	static final public Context getCurContext() {
		return getCurActivity();
	}

	static final public Intent getCurIntent() {
		return getCurActivity().getIntent();
	}

	static final public void sendMsg(String gobjName, String method, String data) {
		UnityPlayer.UnitySendMessage(gobjName, method, data);
	}

	static final public void sendMsgMain(final String gobjName, final String method, final String data) {
		Handler hd = new Handler(Looper.getMainLooper());
		hd.post(new Runnable() {
			public void run() {
				sendMsg(gobjName, method, data);
			}
		});
	}

	static final public ActivityManager getActivityManager() {
		return (ActivityManager) getCurActivity().getSystemService(Context.ACTIVITY_SERVICE);
	}

	static final public ConnectivityManager getConnectivityManager() {
		return (ConnectivityManager) getCurActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
	}

	static final public TelephonyManager getTelephonyManager() {
		return (TelephonyManager) getCurActivity().getSystemService(Context.TELEPHONY_SERVICE);
	}

	static final public WifiManager getWifiManager() {
		return (WifiManager) getCurActivity().getSystemService(Context.WIFI_SERVICE);
	}

	static final public NetworkInfo getNetworkInfo() {
		return getConnectivityManager().getActiveNetworkInfo();
	}

	static final public WifiInfo getWifiInfo() {
		return getWifiManager().getConnectionInfo();
	}

	static final public void runOnUiThread(Runnable action) {
		getCurActivity().runOnUiThread(action);
	}

	static final public String getIMEI() {
		TelephonyManager tmp = getTelephonyManager();
		if (tmp == null)
			return "";
		return tmp.getDeviceId();
	}

	static final public String getIMSI() {
		TelephonyManager tmp = getTelephonyManager();
		if (tmp == null)
			return "";
		return tmp.getSubscriberId();
	}

	// 手机运行商
	static final public String getSimOperator() {
		TelephonyManager tmp = getTelephonyManager();
		if (tmp == null)
			return "";
		return tmp.getSimOperator();
	}

	// 手机运行商名字
	static final public String getSimOperatorName() {
		TelephonyManager tmp = getTelephonyManager();
		if (tmp == null)
			return "";
		return tmp.getSimOperatorName();
	}

	static final public String getNetWorkStatus() {
		String netWorkType = "none";
		NetworkInfo networkInfo = getNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			int type = networkInfo.getType();
			if (type == ConnectivityManager.TYPE_WIFI) {
				netWorkType = "wifi";
			} else if (type == ConnectivityManager.TYPE_MOBILE) {
				netWorkType = getNetWorkStatusByMobile();
			}
		}
		return netWorkType;
	}

	static final String getNetWorkStatusByMobile() {
		TelephonyManager telephonyManager = getTelephonyManager();
		switch (telephonyManager.getNetworkType()) {
		case TelephonyManager.NETWORK_TYPE_GPRS:
		case TelephonyManager.NETWORK_TYPE_EDGE:
		case TelephonyManager.NETWORK_TYPE_CDMA:
		case TelephonyManager.NETWORK_TYPE_1xRTT:
		case TelephonyManager.NETWORK_TYPE_IDEN:
			return "2g";
		case TelephonyManager.NETWORK_TYPE_UMTS:
		case TelephonyManager.NETWORK_TYPE_EVDO_0:
		case TelephonyManager.NETWORK_TYPE_EVDO_A:
		case TelephonyManager.NETWORK_TYPE_HSDPA:
		case TelephonyManager.NETWORK_TYPE_HSUPA:
		case TelephonyManager.NETWORK_TYPE_HSPA:
		case TelephonyManager.NETWORK_TYPE_EVDO_B:
		case TelephonyManager.NETWORK_TYPE_EHRPD:
		case TelephonyManager.NETWORK_TYPE_HSPAP:
			return "3g";
		case TelephonyManager.NETWORK_TYPE_LTE:
			return "4g";
		default:
			return "unknown";
		}
	}

	static final public void killSelf() {
		getCurActivity().finish();
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	// 定时重启
	static final public void restart(int ms) {
		if (ms < 200)
			ms = 200;

		Intent restartIntent = getCurIntent();
		PendingIntent intent = PendingIntent.getActivity(getCurActivity(), 0, restartIntent,
				Intent.FLAG_ACTIVITY_CLEAR_TOP);

		AlarmManager manager = (AlarmManager) getCurActivity().getSystemService(Context.ALARM_SERVICE);

		manager.set(AlarmManager.RTC, System.currentTimeMillis() + ms, intent);
		killSelf();
	}

	static final public String getIPAddress() {
		try {
			NetworkInfo info = getNetworkInfo();
			if (info != null && info.isConnected()) {
				if (info.getType() == ConnectivityManager.TYPE_MOBILE) {// 当前使用2G/3G/4G网络
					for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en
							.hasMoreElements();) {

						NetworkInterface intf = en.nextElement();
						for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr
								.hasMoreElements();) {
							InetAddress inetAddress = enumIpAddr.nextElement();
							if (!inetAddress.isLoopbackAddress()) {
								if (inetAddress instanceof Inet4Address)
									return inetAddress.getHostAddress();
							}
						}
					}

				} else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
					WifiInfo wifiInfo = getWifiInfo();
					String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());
					return ipAddress;
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return "none";
	}

	static final public String getUUID() {
		try {
			final TelephonyManager mgr = getTelephonyManager();
			final String and_id = android.provider.Settings.Secure.ANDROID_ID;
			String tmDevice = mgr.getDeviceId();
			String tmSerial = mgr.getSimSerialNumber();
			String androidId = android.provider.Settings.Secure.getString(getCurActivity().getContentResolver(),
					and_id);
			if (androidId != null && tmDevice != null) {
				long leastSigBits = ((long) tmDevice.hashCode() << 32);
				if (tmSerial != null)
					leastSigBits = leastSigBits | tmSerial.hashCode();
				UUID deviceUuid = new UUID(androidId.hashCode(), leastSigBits);
				return deviceUuid.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return UUID.randomUUID().toString();
	}

	static final public String getPackageName() {
		return getPackageName(getCurContext());
	}

	static final public PackageInfo getPackageInfo() {
		try {
			return getCurActivity().getPackageManager().getPackageInfo(getPackageName(),
					PackageManager.GET_CONFIGURATIONS);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	static final public String getVersionName() {
		PackageInfo info = getPackageInfo();
		if (info != null)
			return info.versionName;
		return "none";
	}

	static final public int getVersionCode() {
		PackageInfo info = getPackageInfo();
		if (info != null)
			return info.versionCode;
		return -1;
	}

	/** 判断是否安装了包体 **/
	static final public boolean isInstalledApk(String apkPkgName) {
		try {
			getCurActivity().getPackageManager().getApplicationInfo(apkPkgName, 0);
			return true;
		} catch (Exception e) {
		}
		return false;
	}

	static final public String getDiskCacheDir() {
		return getDiskCacheDir(getCurContext());
	}

	static final public String getDiskFileDir() {
		return getDiskFileDir(getCurContext());
	}

	// 监测电池的状态
	static final public Map<String, Object> getMonitorBatteryState(Map<String, Object> map) {
		if (map == null)
			map = new HashMap<String, Object>();

		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent intent = getCurContext().registerReceiver(null, ifilter);
		int rawlevel = intent.getIntExtra("level", 0);// 获得当前电量
		int scale = intent.getIntExtra("scale", 0);// 获得总电量
		int status = intent.getIntExtra("status", 0);// 电池充电状态
		int health = intent.getIntExtra("health", 0);// 电池健康状况
		int voltage = intent.getIntExtra("voltage", 0); // 电池电压(mv)
		int temperature = intent.getIntExtra("temperature", 0); // 电池温度(数值)
		double ct = temperature / 10.0; // 电池摄氏温度，默认获取的非摄氏温度值，需做一下运算转换
		int level = -1;
		if (rawlevel > 0 && scale > 0) {
			level = (rawlevel * 100) / scale;
		}
		map.put("level", level);
		map.put("rawlevel", rawlevel);
		map.put("all", scale);
		map.put("status", status);
		map.put("health", health);
		map.put("voltage", voltage);
		map.put("temperature", temperature);
		map.put("ct", ct);
		return map;
	}

	static PhoneStateListener _mylistener;

	static final private PhoneStateListener getPhoneStateListener() {
		if (_mylistener == null) {
			_mylistener = new PhoneStateListener() {
				@Override
				public void onSignalStrengthsChanged(SignalStrength entity) {
					super.onSignalStrengthsChanged(entity);
					NetworkInfo info = getNetworkInfo();
					if (info != null && info.isAvailable()) {
						switch (info.getType()) {
						case ConnectivityManager.TYPE_WIFI:
							// wifi
							WifiInfo wifiInfo = getWifiInfo();
							m_nNetDB = wifiInfo.getRssi();
							break;
						case ConnectivityManager.TYPE_MOBILE:
							TelephonyManager tm = getTelephonyManager();

							if (tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_LTE) {
								try {
//									int lte_sinr = (Integer) entity.getClass().getMethod("getLteSignalStrength").invoke(entity);
//									int lte_rsrq = (Integer) entity.getClass().getMethod("getLteRsrq").invoke(entity);
//									int lte_rssnr = (Integer) entity.getClass().getMethod("getLteRssnr").invoke(entity);
//									int lte_cqi = (Integer) entity.getClass().getMethod("getLteCqi").invoke(entity);
									int lte_dbm = (Integer) entity.getClass().getMethod("getDbm").invoke(entity);
									int lte_level = (Integer) entity.getClass().getMethod("getLteLevel").invoke(entity);
									int lte_rsrp = (Integer) entity.getClass().getMethod("getLteRsrp").invoke(entity);
									m_nNetDB = lte_rsrp;
									System.out.println(String.format("==4G == dbm=%s,level=%s,rsrp=%s", lte_dbm,lte_level,lte_rsrp));
								} catch (Exception e) {
									e.printStackTrace();
								}
							} else if (tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_HSDPA
									|| tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_HSPA
									|| tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_HSUPA
									|| tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS) {
								// 3G网络最佳范围 >-90dBm 越大越好 ps:中国移动3G获取不到
								// 返回的无效dbm值是正数（85dbm）
								// 在这个范围的已经确定是3G，但不同运营商的3G有不同的获取方法，故在此需做判断
								// 判断运营商与网络类型的工具类在最下方
								m_nNetDB = 0;
								String imsi = tm.getSubscriberId();// 获取当前运营商
								if (imsi != null) {
									if (imsi.startsWith("46000") || imsi.startsWith("46002")) {
										m_nNetDB = 0;// 中国移动3G不可获取，故在此返回0
									} else if (imsi.startsWith("46001")) {
										m_nNetDB = entity.getCdmaDbm();// 中国联通
									} else if (imsi.startsWith("46003")) {
										m_nNetDB = entity.getEvdoDbm();// 中国电信
									}
								}
							} else {
								// 2G网络最佳范围>-90dBm 越大越好
								int asu = entity.getGsmSignalStrength();
								m_nNetDB = -113 + 2 * asu;
							}
							break;
						}
					}

				}
			};
		}
		return _mylistener;
	}

	static public final void reListenerState(TelephonyManager tm, boolean isPause) {
		// 开始监听
		if (tm == null)
			return;

		PhoneStateListener _state = getPhoneStateListener();
		if (isPause) {
			tm.listen(_state, PhoneStateListener.LISTEN_NONE);
		} else {
			tm.listen(_state, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
		}
	}

	static public final void reListenerState(boolean isPause) {
		reListenerState(getTelephonyManager(), isPause);
	}

	// 取得手机信息(目前返回电池信息,信号强度)
	static final public Map<String, Object> getPhoneState(Map<String, Object> map) {
		recalcNetDBLevel();
		map = getMonitorBatteryState(map);
		map.put("netDB", m_nNetDB);
		map.put("netDBLevel", m_nNetDBLevel);
		map.put("imei", getIMEI());
		map.put("imsi", getIMSI());
		map.put("simOperatorName", getSimOperatorName());
		map.put("simOperator", getSimOperator());
		return map;
	}

	static final Map<String, Object> getMemInfo(Map<String, Object> map) {
		ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
		ActivityManager am = getActivityManager();
		am.getMemoryInfo(memInfo);
		map.put("am_total", memInfo.totalMem);
		map.put("am_free", memInfo.availMem);
		return map;
	}

	static final public Map<String, Object> getMemCpuInfo(Map<String, Object> map) {
		if (map == null)
			map = new HashMap<String, Object>();
		map = getMemoryInfo(map);
		map = getMemInfo(map);
		map = getCpuInfo(map);
		
		long lmt_1mb = 1024 * 1024;
		long lmt_1 = 3000 * lmt_1mb;
		long lmt_2 = 1024 * lmt_1mb;

		double maxGHz = (double) map.get("max_cpu_ghz");

		long rom_total = (long) map.get("rom_total");
		double rom_rate = (double) map.get("rom_rate");
		if(rom_rate <= 0)
			rom_rate = 1;
		long memery = (long) map.get("memory");
		long max_total = Math.max(rom_total, memery);
		String strSize = Formatter.formatFileSize(getCurContext(), max_total).toLowerCase();
		map.put("total_size_str", strSize);
		
		long am_total = (long) map.get("am_total");
		strSize = Formatter.formatFileSize(getCurContext(), am_total).toLowerCase();
		map.put("am_total_str", strSize);
		
		max_total = Math.min(max_total, am_total);
		max_total = (long)(max_total / rom_rate);
		// 3高 ，2中 ，1低
		int model_type = 2;
		int numCore = getNumberOfCPUCores();
		if (maxGHz >= 1.8 && numCore >= 4 && max_total >= lmt_1) {
			model_type = 3;
		} else if (maxGHz <= 1.5 && max_total <= lmt_2) {
			model_type = 1;
		}
		map.put("num_core", numCore);
		map.put("model_type", model_type);
		return map;
	}
}