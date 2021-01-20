package com.heasy.knowroute.utils;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.RandomStringUtils;

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
	 * 获取随机数
	 */
	public static String getRandomNumber(int count){
		return RandomStringUtils.randomNumeric(count);
	}
	
//	public static void main(String[] args) {
//		System.out.println(RandomStringUtils.random(4, "utf-8")); //数字、小写字母、减号
//		
//		//纯数字
//		System.out.println(RandomStringUtils.random(4, false, true)); 
//		System.out.println(RandomStringUtils.randomNumeric(4));
//		
//		//纯字母，大小写
//		System.out.println(RandomStringUtils.random(4, true, false)); 
//		System.out.println(RandomStringUtils.randomAlphabetic(4));
//		
//		//大小写字母、数字
//		System.out.println(RandomStringUtils.random(4, true, true)); 
//		System.out.println(RandomStringUtils.randomAlphanumeric(4));
//		
//		//ascii
//		System.out.println(RandomStringUtils.randomAscii(4));
//	}
}
