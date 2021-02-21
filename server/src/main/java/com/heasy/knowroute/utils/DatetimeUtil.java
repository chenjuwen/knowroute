package com.heasy.knowroute.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Administrator on 2018/2/13.
 */
public class DatetimeUtil {
    /**
     * 格式为 yyyy-MM-dd
     */
    public static final String DEFAULT_PATTERN = "yyyy-MM-dd";
    
    /**
     * 格式为 yyyy-MM-dd HH:mm:ss
     */
    public static final String DEFAULT_PATTERN_DT = "yyyy-MM-dd HH:mm:ss";
    
    /**
     * 格式为 yyyy-MM-dd HH:mm
     */
    public static final String DEFAULT_PATTERN_DT2 = "yyyy-MM-dd HH:mm";

    public static Calendar getCalendar(){
        Calendar cal = Calendar.getInstance(Locale.CHINA);

        if(cal.getTimeZone().getID().equalsIgnoreCase("GMT")){
            cal.add(Calendar.HOUR_OF_DAY, 8);
        }

        return cal;
    }

    public static Timestamp nowTimestamp(){
        return new Timestamp(getCalendar().getTimeInMillis());
    }

    public static Date nowDate(){
        return getCalendar().getTime();
    }

    public static long nowTimeInMillis(){
        return getCalendar().getTimeInMillis();
    }

    public static String getToday(){
    	return getToday(DEFAULT_PATTERN_DT);
    }

    public static String getToday(String pattern){
        if(StringUtil.isEmpty(pattern)) {
            pattern = DEFAULT_PATTERN;
        }

        Calendar cal = getCalendar();
        String today = formatDate(cal.getTime(), pattern);

        return today;
    }

    public static String changeDateFormat(String date, String fromPattern, String toPattern){
        try{
            SimpleDateFormat sdf1 = new SimpleDateFormat(fromPattern);
            Date newDate = sdf1.parse(date);

            SimpleDateFormat sdf2 = new SimpleDateFormat(toPattern);
            return sdf2.format(newDate);

        }catch(Exception ex){
            return date;
        }
    }
    
    public static String formatDate(Date date){
    	return formatDate(date, DEFAULT_PATTERN_DT);
    }

    public static String formatDate(Date date, String pattern){
        if(date == null) {
            return "";
        }

        if(StringUtil.isEmpty(pattern)) {
            pattern = DEFAULT_PATTERN;
        }

        try{
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            return sdf.format(date);
        }catch(Exception ex){
            return "";
        }
    }

    public static String formatDate(Timestamp timestamp, String pattern){
        if(timestamp == null) {
            return "";
        }
        return formatDate(new Date(timestamp.getTime()), pattern);
    }

    public static Timestamp toTimestamp(String date, String pattern){
        try{
            if(StringUtil.isEmpty(date)) return null;
            if(StringUtil.isEmpty(pattern)) pattern = DEFAULT_PATTERN;

            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            Date dt = sdf.parse(date);
            return new Timestamp(dt.getTime());
        }catch(Exception ex){
            return null;
        }
    }

    public static Date toDate(String date, String pattern){
        try{
            if(StringUtil.isEmpty(date)) return null;
            if(StringUtil.isEmpty(pattern)) pattern = DEFAULT_PATTERN;

            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            return sdf.parse(date);
        }catch(Exception ex){
            return null;
        }
    }
    
    public static Date toDate(String date){
    	return toDate(date, DEFAULT_PATTERN_DT);
    }

    public static Date toDate(Timestamp timestamp){
        try{
            if(timestamp == null) return null;
            Date dt = new Date(timestamp.getTime());
            return dt;
        }catch(Exception ex){
            return null;
        }
    }

    public static Date toDate(long timeInMillis){
        Calendar cal = getCalendar();
        cal.setTimeInMillis(timeInMillis);
        return cal.getTime();
    }

    /**
     * 为指定日期增加若干日期数，可正可负
     *
     * @param sDate 日期字符串，格式为：yyyyMMdd or yyyyMM
     * @param datepart 日期类型字段，如年、月、日、时、分、秒等
     * @param value 要增加的日期数
     * @param pattern 日期的格式
     */
    public static String add(String sDate, int datepart, int value, String pattern){
        String ret = "";

        if(StringUtil.isEmpty(sDate)) {
            throw new RuntimeException("sDate must be not empty");
        }

        if(StringUtil.isEmpty(pattern)) {
            throw new RuntimeException("outPattern must be not empty");
        }

        Date date = toDate(sDate, pattern);
        ret = formatDate(add(date, datepart, value), pattern);
        return ret;
    }
    
    public static Date add(Date date, int datepart, int value) {
    	Calendar cal = getCalendar();
    	cal.setTime(date);
    	cal.add(datepart, value);
    	return cal.getTime();
    }
    
    public static String add(Date date, int datepart, int value, String returnPattern) {
    	return formatDate(add(date, datepart, value), returnPattern);
    }
    
    public static Date add(int milliSeconds) {
    	Calendar cal = getCalendar();
    	cal.add(Calendar.MILLISECOND, milliSeconds);
    	return cal.getTime();
    }
    
    /**
     * 当前日期增加若干日期数，可正可负
     * 
     * @param datepart 日期类型字段，如年、月、日、时、分、秒等
     * @param value 要增加的日期数
     * @return 返回yyyy-MM-dd HH:mm:ss格式的日期字符串 
     */
    public static String add(int datepart, int value) {
    	return add(nowDate(), datepart, value, DEFAULT_PATTERN_DT);
    }
    
    public static String add(int datepart, int value, String returnPattern) {
    	return add(getToday(), datepart, value, returnPattern);
    }

    public static String getSimpleWeekName(){
        String dayNames[] = {"日", "一", "二", "三", "四", "五", "六"};
        int dayOfWeek = getCalendar().get(Calendar.DAY_OF_WEEK) - 1;
        if(dayOfWeek < 0){
            dayOfWeek = 0;
        }
        return dayNames[dayOfWeek];
    }

    public static String getFullWeekName(){
        String dayNames[] = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        int dayOfWeek = getCalendar().get(Calendar.DAY_OF_WEEK) - 1;
        if(dayOfWeek < 0){
            dayOfWeek = 0;
        }
        return dayNames[dayOfWeek];
    }

    /**
     * 两个日期相差几个小时
     * @param startDate 开始日期
     * @param endDate 结束日期
     */
    public static long differHours(Date startDate, Date endDate) {
    	if(startDate == null || endDate == null) {
    		return 0;
    	}
    	
		long milliseconds = endDate.getTime() - startDate.getTime();
        return milliseconds / (60*60*1000);
    }

    /**
     * 两个日期相差多少分钟
     * @param startDate 开始日期
     * @param endDate 结束日期
     */
    public static long differMinutes(Date startDate, Date endDate) {
    	if(startDate == null || endDate == null) {
    		return 0;
    	}
    	long milliseconds = endDate.getTime() - startDate.getTime();
        return milliseconds / (60*1000);
    }
    
}
