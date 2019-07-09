package com.sdkplugin.bridge;

/**
 * android手机类型
 */
public enum AndPhoneType {
	PT_NONE(0), // 无
	PT_HUAWEI(1), // 华为 HuaWei
	PT_XIAOMI(2), // 小米 XiaoMi
	PT_OPPO(3), // oppo
	PT_VIVO(4);// vivo

	private int index;

	public int getIndex() {
		return index;
	}

	private AndPhoneType(int index) {
		this.index = index;
	}

	static public AndPhoneType get(int ordinal) {
		if (ordinal < 0)
			return null;
		AndPhoneType[] _vals = AndPhoneType.values();
		if (ordinal >= _vals.length)
			return null;
		return _vals[ordinal];
	}

	static public AndPhoneType getByIndex(int index) {
		for (AndPhoneType got : AndPhoneType.values()) {
			if (got.getIndex() == index) {
				return got;
			}
		}
		return null;
	}
}
