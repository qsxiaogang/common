package com.ccclubs.common.utils.java;

import java.math.BigDecimal;

/**
 * Float相关的工具函数
 */
public class FloatUtils {
	/**
	 * 默认保留2位小数
	 *
	 * @param d
	 * @return
	 */
	public static float formatFloat(double d) {
		return formatFloat(d, 2);
	}

	/**
	 * 保留小数，
	 *
	 * @param d
	 * @return
	 */
	public static float formatFloat(double d, int num) {
		BigDecimal bd = new BigDecimal(d);
		bd = bd.setScale(num, BigDecimal.ROUND_HALF_UP);
		return bd.floatValue();
	}

}
