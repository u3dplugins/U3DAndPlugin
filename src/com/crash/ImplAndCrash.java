package com.crash;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.interfaces.IAndCrash;

public class ImplAndCrash implements IAndCrash {
	String _url0 = "http://60.205.217.89:9902/statistics", _url = null;

	private ImplAndCrash() {
	}

	@Override
	public void sendCrash2Sv(final String info) {
		if (info == null || info.isEmpty())
			return;

		Thread _t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String msg = String.format("cmd=log&name=crash&val=%s", info);
					byte[] bts = msg.getBytes("UTF-8");
					URL _url = new URL(getUrl());
					HttpURLConnection conn = (HttpURLConnection) _url.openConnection();
					// 设置请求数据类型 - 浏览器编码类型
					conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
					// 编码格式
					conn.setRequestProperty("Charset", "UTF-8");
					// 设置是否使用缓存 - POST不能使用缓存?
					conn.setUseCaches(false);
					// 维持长连接 Keep-Alive close
					conn.setRequestProperty("Connection", "close");
					// 设置接受所有类型
					conn.setRequestProperty("Accept-Charset", "UTF-8");
					conn.setRequestProperty("Accept", "*/*");
					conn.setRequestProperty("Content-Length", String.valueOf(bts.length));
					// 请求超时
					conn.setConnectTimeout(60000);
					// 读取超时
					conn.setReadTimeout(60000);

					// 是否设置输入的内容
					conn.setDoInput(true);
					// 是否设置输出的内容
					conn.setDoOutput(true);

					conn.setRequestMethod("POST");

					// 建立实际的连接
					conn.connect();

					OutputStream outPut = conn.getOutputStream();
					outPut.write(bts);
					outPut.flush();
					outPut.close();
					conn.getResponseCode();
					conn.disconnect();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		_t.start();
	}

	public ImplAndCrash init(String url) {
		this._url = url;
		return this;
	}

	protected String getUrl() {
		if (this._url == null || this._url.isEmpty())
			return this._url0;
		return this._url;
	}

	public static void main(String[] args) {
		getInstance().sendCrash2Sv("time:" + System.currentTimeMillis());
	}

	private static ImplAndCrash _instance = null;

	static final public ImplAndCrash getInstance() {
		if (_instance == null) {
			_instance = new ImplAndCrash();
		}
		return _instance;
	}
}
