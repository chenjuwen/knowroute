package com.heasy.knowroute.core.utils;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
	public static String trimToEmpty(String text){
		if(text == null){
			return "";
		}else{
			return text.trim();
		}
	}
	
	public static boolean isEmpty(String text){
		return (text == null || text.length() == 0);
	}
	
	public static boolean isNotEmpty(String text){
		return (text != null && text.length() > 0);
	}
	
	public static String getUUIDString(){
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
	
	/**
	 * 验证是否是手机号码
	 */
	public static boolean isMobile(String phone) {
		Pattern p = Pattern.compile("^[1][3-9][0-9]{9}$");
		Matcher m = p.matcher(phone);
		boolean b = m.matches();
		return b;
	}
	
	/**
	 * 获取四位随机数
	 */
	public static String getFourDigitRandomNumber(){
		return String.valueOf((int)((Math.random()*9+1)*1000));
	}
}
