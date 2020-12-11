package com.heasy.knowroute.utils;

import java.sql.Timestamp;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.heasy.knowroute.common.DateJsonValueProcessor;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

public class JsonUtil {
    /**
     * 将json格式的字符串 转成 JSONObject对象
     */
    public static JSONObject string2object(String jsonData){
        if(StringUtil.isNotEmpty(jsonData)) {
            return JSONObject.fromObject(jsonData);
        }
        return null;
    }

    /**
     * 根据key从JSONObject对象获取其对应的值
     */
    public static String getString(JSONObject jsonObject, String key){
        if(jsonObject != null && jsonObject.containsKey(key)){
            String value = StringUtil.trimToEmpty(jsonObject.getString(key));
            return value;
        }
        return "";
    }

    public static String getString(JSONObject jsonObject, String key, String defaultValue){
        String value = defaultValue;
        if(jsonObject != null && jsonObject.containsKey(key)){
            value = StringUtil.trimToEmpty(jsonObject.getString(key));
        }
        return value;
    }
    
    public static String object2ArrayString(Object object) {
    	return JSONArray.fromObject(object).toString(2);
    }
    
    public static String object2ArrayString(Object object, JsonConfig jsonConfig) {
    	return JSONArray.fromObject(object, jsonConfig).toString(2);
    }

    public static String object2String(Object object){
        return object2String(object, getJsonConfigOfDate());
    }
    
    public static String object2String(Object object, JsonConfig jsonConfig) {
        if(object == null) {
        	return null;
        }
        
        if(jsonConfig != null) {
        	return JSONObject.fromObject(object, jsonConfig).toString(2);
        }else {
        	return JSONObject.fromObject(object).toString(2);
        }
    }

    /**
     * 将json格式字符串 转成 JavaBean对象
     */
    public static <T> T string2JavaBean(String jsonString, Class<T> clazz){
        //key转小写
        Matcher matcher = Pattern.compile("\"([A-Za-z0-9]+)\":").matcher(jsonString);
        while(matcher.find()){
            jsonString = jsonString.replace(matcher.group(), matcher.group().toLowerCase());
        }

        JSONObject object = JSONObject.fromObject(jsonString);
        T bean = (T)JSONObject.toBean(object, clazz);
        return bean;
    }

    public static String toJSONString(String key, String value){
        JSONObject object = new JSONObject();
        object.put(key, value);
        return object.toString(2);
    }

    public static String toJSONString(String key1, String value1, String key2, String value2){
        JSONObject object = new JSONObject();
        object.put(key1, value1);
        object.put(key2, value2);
        return object.toString(2);
    }

    public static String toJSONString(String key1, String value1, String key2, String value2, String key3, String value3){
        JSONObject object = new JSONObject();
        object.put(key1, value1);
        object.put(key2, value2);
        object.put(key3, value3);
        return object.toString(2);
    }

    public static String toJSONString(String key1, String value1, String key2, String value2, String key3, String value3, String key4, String value4){
        JSONObject object = new JSONObject();
        object.put(key1, value1);
        object.put(key2, value2);
        object.put(key3, value3);
        object.put(key4, value4);
        return object.toString(2);
    }

    public static String toJSONString(String key1, String value1, String key2, String value2, String key3, String value3, String key4, String value4, String key5, String value5){
        JSONObject object = new JSONObject();
        object.put(key1, value1);
        object.put(key2, value2);
        object.put(key3, value3);
        object.put(key4, value4);
        object.put(key5, value5);
        return object.toString(2);
    }
    
    /**
     * 对json对象中的日期字段进行格式化处理
     */
    public static JsonConfig getJsonConfigOfDate() {
    	return getJsonConfigOfDate(DatetimeUtil.DEFAULT_PATTERN_DT);
    }
    
    public static JsonConfig getJsonConfigOfDate(String pattern) {
    	JsonConfig config = new JsonConfig();
    	config.registerJsonValueProcessor(Date.class, new DateJsonValueProcessor(pattern));
    	config.registerJsonValueProcessor(Timestamp.class, new DateJsonValueProcessor(pattern));
    	return config;
    }

}
