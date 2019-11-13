package com.sdkplugin.bridge;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.net.NetworkInterface;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StatFs;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

/**
 * 类名 : android 基础类 <br/>
 * 作者 : canyon / 龚阳辉 <br/>
 * 时间 : 2016-05-30 10：30 <br/>
 * 功能 : 取得android相关的信息
 */
public class AndBasic {
	// 当前包体的package
	static public String m_curCPkg = "";
	// 网路信号强度
	static protected int m_nNetDB = 0;
	// 网路信号强度等级 (4:GREAT,3:GOOD,2:MODERATE,1:POOR,0:NONE_OR_UNKNOWN);
	static protected int m_nNetDBLevel = 0;

	// 手机型号
	static final public String getModel() {
		return Build.MODEL;
	}

	// 系统定制商
	static final public String getDeviceBrand() {
		return Build.BRAND;
	}

	// 硬件制造商
	static final public String getManufacturer() {
		return Build.MANUFACTURER;
	}

	// 获取系统版本字符串。如4.1.2 或2.2 或2.3等
	static final public String getVersion() {
		return Build.VERSION.RELEASE;
	}

	// 系统的API级别 数字表示
	static final public int getAndVersion() {
		return Build.VERSION.SDK_INT;
	}

	static final public AndPhoneType getPhoneType() {
		String ver = getManufacturer().toLowerCase();
		if (ver.contains("huawei") || ver.contains("honor"))
			return AndPhoneType.PT_HUAWEI;
		else if (ver.contains("xiaomi"))
			return AndPhoneType.PT_XIAOMI;
		else if (ver.contains("oppo"))
			return AndPhoneType.PT_OPPO;
		else if (ver.contains("vivo"))
			return AndPhoneType.PT_VIVO;
		return AndPhoneType.PT_NONE;
	}

	static final public int getPhoneTypeInt() {
		return getPhoneType().ordinal();
	}

	// 转为ip4
	static final String intIP2StringIP(int ip) {
		return String.format("%s.%s.%s.%s", (ip & 0xFF), ((ip >> 8) & 0xFF), ((ip >> 16) & 0xFF), ((ip >> 24) & 0xFF));
	}

	static final public String getMacAddress() {
		// 获取mac地址有一点需要注意的就是android 6.0版本后
		// 通过wifiInfo.getMacAddress ,不管任何手机都会返回"02:00:00:00:00:00"
		// 原因
		// googel官方为了加强权限管理而禁用了getSYstemService(Context.WIFI_SERVICE)方法来获得mac地址

		String macAddress = "02:00:00:00:00:02";
		try {
			// final String netState = getNetWorkStatus();
			// switch (netState) {
			// case "wifi":
			// WifiInfo wifiInfo = getWifiInfo();
			// if (wifiInfo != null) {
			// return wifiInfo.getMacAddress();
			// }
			// break;
			// default:
			// break;
			// }

			StringBuffer buf = new StringBuffer();
			NetworkInterface networkInterface = null;
			networkInterface = NetworkInterface.getByName("eth1");
			if (networkInterface == null) {
				networkInterface = NetworkInterface.getByName("wlan0");
			}
			if (networkInterface != null) {
				byte[] addr = networkInterface.getHardwareAddress();
				for (byte b : addr) {
					buf.append(String.format("%02X:", b));
				}
				if (buf.length() > 0) {
					buf.deleteCharAt(buf.length() - 1);
				}
				macAddress = buf.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return macAddress;
	}

	static final public void setPkgName(Context context) {
		if (context != null)
			m_curCPkg = context.getPackageName();
	}

	static final public void setPkgName(Class<?> clazz) {
		if (clazz != null)
			m_curCPkg = clazz.getPackage().getName();
	}

	static final public void setPkgName(Object obj) {
		if (obj != null)
			setPkgName(obj.getClass());
	}

	// pkg = Package
	static final public String getPkgName(Context context, boolean isRe) {
		isRe = isRe || (m_curCPkg == null || "".equals(m_curCPkg));
		if (isRe)
			setPkgName(context);
		return m_curCPkg;
	}

	static final public String getPkgName(Class<?> clazz, boolean isRe) {
		isRe = isRe || (m_curCPkg == null || "".equals(m_curCPkg));
		if (isRe)
			setPkgName(clazz);
		return m_curCPkg;
	}

	/**
	 * 外部存储是否可读
	 * 
	 * @return 如果可用返回true，否则返回false
	 */
	static final public boolean isExternalStoreReadable() {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED) || state.equals(Environment.MEDIA_MOUNTED_READ_ONLY) || state.equals(Environment.MEDIA_SHARED)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 外部存储是否可写
	 * 
	 * @return 如果可以写则返回true，否则返回false
	 */
	static final public boolean isExternalStoreWritable() {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED) || state.equals(Environment.MEDIA_SHARED)) {
			return true;
		} else {
			return false;
		}
	}

	static final public String outDir(String fp, boolean isRep) {
		if (fp == null || fp.length() <= 0)
			return "";

		if (isRep) {
			fp = fp.replaceAll("\\\\", "/");
			fp = fp.replaceAll("\\", "/");
		}

		if (!fp.endsWith("/") && !fp.endsWith("\\")) {
			fp += "/";
		}
		return fp;
	}

	static final public String getDiskCacheDir(Context context) {
		String _path = "";
		if (context == null)
			return _path;

		if (isExternalStoreWritable() || !Environment.isExternalStorageRemovable()) {
			_path = context.getExternalCacheDir().getAbsolutePath();
		} else {
			_path = context.getCacheDir().getAbsolutePath();
		}
		return outDir(_path, false);
	}

	static final public String getDiskFileDir(Context context) {
		String _path = "";
		if (context == null)
			return _path;

		if (isExternalStoreWritable() || !Environment.isExternalStorageRemovable()) {
			_path = context.getExternalFilesDir(null).getAbsolutePath();
		} else {
			_path = context.getFilesDir().getAbsolutePath();
		}
		return outDir(_path, false);
	}

	static final public File getObbFile(Context context) {
		if (context == null)
			return null;
		return context.getObbDir();
	}

	static final public String getObbDir(Context context, boolean isAbs) {
		File _fl = getObbFile(context);
		String _path = "";
		if (_fl == null || !_fl.exists())
			return _path;
		if (isAbs)
			_path = _fl.getAbsolutePath();
		else
			_path = _fl.getPath();
		return outDir(_path, true);
	}

	static protected final void recalcNetDBLevel() {
		if (m_nNetDB > -20) {
			// 25 默认规格
			m_nNetDBLevel = 0;
		} else if (m_nNetDB >= -49) {
			m_nNetDBLevel = 4;
		} else if (m_nNetDB >= -73) {
			m_nNetDBLevel = 3;
		} else if (m_nNetDB >= -97) {
			m_nNetDBLevel = 2;
		} else if (m_nNetDB >= -120) {
			m_nNetDBLevel = 1;
		} else {
			m_nNetDBLevel = 0;
		}
	}

	static final private Map<String, Object> reStatFs(Map<String, Object> map, StatFs stat, String strHead) {
		long blockSize = stat.getBlockSizeLong();
		long totalBlocks = stat.getBlockCountLong();
		long availableBlocks = stat.getAvailableBlocksLong();
		long totalSize = totalBlocks * blockSize;
		long availSize = availableBlocks * blockSize;

		map.put(strHead + "_block", blockSize);
		map.put(strHead + "_rate", blockSize / 1024.0);
		map.put(strHead + "_total", totalSize);
		map.put(strHead + "_free", availSize);
		map.put(strHead + "_used", (totalSize - availSize));
		// Formatter.formatFileSize(this, totalSize);
		return map;
	}

	static final String parseFileForValue(BufferedReader br, String key) {
		try {
			String str = br.readLine();
			while (str != null && !str.contains(key)) {
				str = br.readLine();
			}
			// System.out.println("=== 1 = " + str);
			if (str != null && str.contains(key)) {
				String[] arr = str.split("\\s+");
				// System.out.println("=== 2 = " + arr[1]);
				return arr[1];
			}
		} catch (Exception e) {
		}
		return null;
	}

	static final private long memory() {
		long all = 0;
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader("/proc/meminfo");
			br = new BufferedReader(fr);
			all = Integer.valueOf(parseFileForValue(br, "MemTotal")).intValue() * 1024; // 获得系统总内存，单位是KB，乘以1024转换为Byte
		} catch (Exception ex) {
		} finally {
			try {
				if (fr != null)
					fr.close();
			} catch (Exception ex) {
			}
			try {
				if (br != null)
					br.close();
			} catch (Exception ex) {
			}
		}
		return all;
	}

	static final Map<String, Object> getMemoryInfo(Map<String, Object> map) {
		File path = Environment.getDataDirectory();
		StatFs stat = null;
		if (path != null && path.exists()) {
			stat = new StatFs(path.getPath());
			map = reStatFs(map, stat, "rom");
		}
		// path = Environment.getExternalStorageDirectory();
		// if (path != null && path.exists()) {
		// stat = new StatFs(path.getPath());
		// map = reStatFs(map, stat, "sd_card");
		// }
		map.put("memory", memory());
		return map;
	}

	static final private long minCPU() {
		long all = 0;
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq");
			br = new BufferedReader(fr);
			String str = br.readLine();
			all = Long.parseLong(str.trim());
		} catch (Exception ex) {
		} finally {
			try {
				if (fr != null)
					fr.close();
			} catch (Exception ex) {
			}
			try {
				if (br != null)
					br.close();
			} catch (Exception ex) {
			}
		}
		return all;
	}

	static final private long maxCPU() {
		long all = 0;
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq");
			br = new BufferedReader(fr);
			String str = br.readLine();
			all = Long.parseLong(str.trim());
		} catch (Exception ex) {
		} finally {
			try {
				if (fr != null)
					fr.close();
			} catch (Exception ex) {
			}
			try {
				if (br != null)
					br.close();
			} catch (Exception ex) {
			}
		}
		return all;
	}

	static final private long currCPU() {
		long all = 0;
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");
			br = new BufferedReader(fr);
			String str = br.readLine();
			all = Long.parseLong(str.trim());
		} catch (Exception ex) {
		} finally {
			try {
				if (fr != null)
					fr.close();
			} catch (Exception ex) {
			}
			try {
				if (br != null)
					br.close();
			} catch (Exception ex) {
			}
		}
		return all;
	}

	// CPU频率（单位KHZ）
	// Hz（赫）、kHz（千赫）、MHz（兆赫）、GHz（吉赫）。其中1GHz=1000MHz，1MHz=1000kHz，1kHz=1000Hz
	static final Map<String, Object> getCpuInfo(Map<String, Object> map) {
		long maxK = maxCPU();
		double maxGHz = maxK * 0.001 * 0.001;
		map.put("min_cpu", minCPU());
		map.put("max_cpu", maxK);
		map.put("cur_cpu", currCPU());
		map.put("max_cpu_ghz", maxGHz);
		return map;
	}

	public static final int DEVICEINFO_UNKNOWN = -1;
	private static final FileFilter CPU_FILTER = new FileFilter() {
		@Override
		public boolean accept(File pathname) {
			String path = pathname.getName();
			if (path.startsWith("cpu")) {
				for (int i = 3; i < path.length(); i++) {
					if (path.charAt(i) >= '0' && path.charAt(i) <= '9') {
						return true;
					}
				}
			}
			return false;
		}
	};

	public static final int getNumberOfCPUCores() {
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
			return 1;
		}
		int cores;
		try {
			cores = new File("/sys/devices/system/cpu/").listFiles(CPU_FILTER).length;
		} catch (Exception e) {
			cores = DEVICEINFO_UNKNOWN;
		}
		return cores;
	}

	static final public void runInMainThread(Runnable r) {
		Handler hd = new Handler(Looper.getMainLooper());
		hd.post(r);
	}

	static final public WindowManager getWinMgr(Context ctx) {
		return (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
	}

	static final public Display getWinDisplay(Context ctx) {
		return getWinMgr(ctx).getDefaultDisplay();
	}

	static final public int[] getScreenWidthAndHeight(Context ctx) {
		Display mWd = getWinDisplay(ctx);
		DisplayMetrics dm = new DisplayMetrics();
		// 获取屏幕信息
		mWd.getMetrics(dm);
		int screenWidth = dm.widthPixels;
		int screenHeigh = dm.heightPixels;
		return new int[] { screenWidth, screenHeigh };
	}

	static final public View getTopView(Activity act) {
		return act.getWindow().getDecorView();
	}

	static final public View getCurView(Activity act) {
		return act.getCurrentFocus();
	}

	// rotation: 0（Surface.ROTATION_0---竖屏正向）、1（Surface.ROTATION_90---横屏正向）、
	// 2（Surface.ROTATION_180---竖屏反向）、3（Surface.ROTATION_270---横屏反向）
	static final public int getRotation(Context ctx) {
		return getWinDisplay(ctx).getRotation();
	}
}