package com.heasy.knowroute.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.heasy.knowroute.utils.DatetimeUtil;

public class Test {
	public static void main(String[] args) {
		System.out.println(addressResolution("广东省云浮市罗定市罗平镇平垌茶岗5队"));
		System.out.println(addressResolution("罗定市罗平镇平垌榃边村"));
		System.out.println(addressResolution("广东省广州市天河区东圃黄村莲溪大街5号603室"));
	}
	
	public static List<Map<String,String>> addressResolution(String address){
        String regex="(?<province>[^省]+自治区|.*?省|.*?行政区|.*?市)?(?<city>[^市]+自治州|.*?地区|.*?行政单位|.+盟|市辖区|.*?市|.*?县)(?<county>[^县]+县|.+区|.+市|.+旗|.+海域|.+岛)?(?<town>[^区]+区|.+镇)?(?<village>.*)";
        Matcher m=Pattern.compile(regex).matcher(address);
        String province=null,city=null,county=null,town=null,village=null;
        List<Map<String,String>> table=new ArrayList<Map<String,String>>();
        Map<String,String> row=null;
        while(m.find()){
            row=new LinkedHashMap<String,String>();
            province=m.group("province");
            row.put("province", province==null?"":province.trim());
            city=m.group("city");
            row.put("city", city==null?"":city.trim());
            county=m.group("county");
            row.put("county", county==null?"":county.trim());
            town=m.group("town");
            row.put("town", town==null?"":town.trim());
            village=m.group("village");
            row.put("village", village==null?"":village.trim());
            table.add(row);
            System.out.println(m.group(2) + "," + m.group(3));
        }
        return table;
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
