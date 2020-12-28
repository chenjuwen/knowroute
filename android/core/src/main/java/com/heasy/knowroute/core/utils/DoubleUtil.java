package com.heasy.knowroute.core.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class DoubleUtil {
	private static final String DEFAULT_PATTERN = "0.00";
	
	/**
	 * 加法运算
	 */
	public static double add(Double d1, Double d2) {
		BigDecimal bgd1 = new BigDecimal(Double.toString(d1==null?0:d1));
		BigDecimal bgd2 = new BigDecimal(Double.toString(d2==null?0:d2));
		return bgd1.add(bgd2).doubleValue();
	}
	
	/**
	 * 减法运算
	 */
	public static double sub(Double d1, Double d2) {
		BigDecimal bgd1 = new BigDecimal(Double.toString(d1==null?0:d1));
		BigDecimal bgd2 = new BigDecimal(Double.toString(d2==null?0:d2));
		return bgd1.subtract(bgd2).doubleValue();
	}
	
	/**
	 * 乘法运算
	 */
	public static double mul(Double d1, Double d2) {
		BigDecimal bgd1 = new BigDecimal(Double.toString(d1==null?0:d1));
		BigDecimal bgd2 = new BigDecimal(Double.toString(d2==null?0:d2));
		return bgd1.multiply(bgd2).doubleValue();
	}
	
	/**
	 * 除法运算
	 */
	public static double div(Double d1, Double d2) {
		return div(d1, d2, 2);
	}

	/**
	 * 除法运算
	 * @param d1
	 * @param d2
	 * @param scale 小数点位数
	 */
	public static double div(Double d1, Double d2, int scale) {
		BigDecimal bgd1 = new BigDecimal(Double.toString(d1==null?0:d1));
		BigDecimal bgd2 = new BigDecimal(Double.toString(d2==null?1:d2));
		return bgd1.divide(bgd2, scale, BigDecimal.ROUND_HALF_DOWN).doubleValue();
	}

	/**
	 * 处理小数点位数，默认为小数点后两位
	 */
	public static Double decimalNum(Double d) {
		return decimalNum(d, 2);
	}

	/**
	 * 处理小数点位数
	 */
	public static Double decimalNum(Double value, int scale) {
		try{
			BigDecimal a = new BigDecimal(value.doubleValue());
			BigDecimal b = new BigDecimal("1");
			double returnValue = a.divide(b, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
			return returnValue;			
		}catch(Exception ex){
			ex.printStackTrace();
			return value;
		}
	}

	/**
	 * 将double类型的数据格式化成字符串
	 * @param value
	 */
	public static String format(Double value) {
		return format(value, DEFAULT_PATTERN);
	}
	
	public static String format(Double value, String pattern) {
		if(value == null) return null;
		
		if(pattern == null) {
			pattern = DEFAULT_PATTERN;
		}
		
		DecimalFormat df = new DecimalFormat(pattern);
		return df.format(value);
	}

}
