package com.heasy.knowroute.test;

import java.util.Date;

import com.heasy.knowroute.utils.DatetimeUtil;

public class Test {
	public static void main(String[] args) {
		System.out.println(12345 / 1000.0);
	}

	private static void dt() {
		Date d1 = DatetimeUtil.toDate("2020-11-06 21:10", DatetimeUtil.DEFAULT_PATTERN_DT2);
		Date d2 = DatetimeUtil.toDate("2020-11-07 21:10", DatetimeUtil.DEFAULT_PATTERN_DT2);
		long milliseconds = d2.getTime() - d1.getTime();
        long day = milliseconds / (24 * 60 * 60 * 1000);
        long hour = (milliseconds / (60 * 60 * 1000) - day * 24);
        long min = ((milliseconds / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long s = (milliseconds / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        System.out.println("" + day + "天" + hour + "小时" + min + "分" + s + "秒");
        
        System.out.println(milliseconds / (60*60*1000));
	}
}
