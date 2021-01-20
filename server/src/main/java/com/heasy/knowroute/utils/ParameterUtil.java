package com.heasy.knowroute.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ParameterUtil {
    public static final String CHARACTER_NAME = "utf-8";

    public static String encodeParamValue(String value){
        if(StringUtil.isEmpty(value)){
            return "";
        }
        try {
            return URLEncoder.encode(value, CHARACTER_NAME);
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }

    public static String toParamString(Map<String, String> params, boolean encodeValue){
        StringBuilder sb = new StringBuilder();
        int index = 0;
        for(Iterator<String> it=params.keySet().iterator(); it.hasNext();){
            String key = it.next();
            String value = params.get(key);

            if(index > 0){
                sb.append("&");
            }

            sb.append(key);
            sb.append("=");

            if(encodeValue) {
                sb.append(encodeParamValue(value));
            }else{
                sb.append(value);
            }

            ++index;
        }
        return sb.toString();
    }

    /**
     * ParamString
     */
    public static String toParamString(String key, String value){
        StringBuilder sb = new StringBuilder();
        sb.append(key + "=" + value);
        return sb.toString();
    }

    public static String toParamString(String key1, String value1, String key2, String value2){
        StringBuilder sb = new StringBuilder();
        sb.append(key1 + "=" + value1);
        sb.append("&" + key2 + "=" + value2);
        return sb.toString();
    }

    public static String toParamString(String key1, String value1, String key2, String value2, 
    		String key3, String value3){
        StringBuilder sb = new StringBuilder();
        sb.append(key1 + "=" + value1);
        sb.append("&" + key2 + "=" + value2);
        sb.append("&" + key3 + "=" + value3);
        return sb.toString();
    }

    public static String toParamString(String key1, String value1, String key2, String value2, 
    		String key3, String value3, String key4, String value4){
        StringBuilder sb = new StringBuilder();
        sb.append(key1 + "=" + value1);
        sb.append("&" + key2 + "=" + value2);
        sb.append("&" + key3 + "=" + value3);
        sb.append("&" + key4 + "=" + value4);
        return sb.toString();
    }

    public static String toParamString(String key1, String value1, String key2, String value2, 
    		String key3, String value3, String key4, String value4, String key5, String value5){
        StringBuilder sb = new StringBuilder();
        sb.append(key1 + "=" + value1);
        sb.append("&" + key2 + "=" + value2);
        sb.append("&" + key3 + "=" + value3);
        sb.append("&" + key4 + "=" + value4);
        sb.append("&" + key5 + "=" + value5);
        return sb.toString();
    }

    public static String toParamString(String key1, String value1, String key2, String value2, 
    		String key3, String value3, String key4, String value4, 
    		String key5, String value5, String key6, String value6){
        StringBuilder sb = new StringBuilder();
        sb.append(key1 + "=" + value1);
        sb.append("&" + key2 + "=" + value2);
        sb.append("&" + key3 + "=" + value3);
        sb.append("&" + key4 + "=" + value4);
        sb.append("&" + key5 + "=" + value5);
        sb.append("&" + key6 + "=" + value6);
        return sb.toString();
    }

    public static String toParamString(String key1, String value1, String key2, String value2, 
    		String key3, String value3, String key4, String value4, 
    		String key5, String value5, String key6, String value6,
    		String key7, String value7, String key8, String value8){
        StringBuilder sb = new StringBuilder();
        sb.append(key1 + "=" + value1);
        sb.append("&" + key2 + "=" + value2);
        sb.append("&" + key3 + "=" + value3);
        sb.append("&" + key4 + "=" + value4);
        sb.append("&" + key5 + "=" + value5);
        sb.append("&" + key6 + "=" + value6);
        sb.append("&" + key7 + "=" + value7);
        sb.append("&" + key8 + "=" + value8);
        return sb.toString();
    }

    /**
     * ParamMap
     */
    public static Map<String, String> toParamMap(String key, String value){
        Map<String, String> params = new HashMap<>();
        params.put(key, value);
        return params;
    }

    public static Map<String, String> toParamMap(String key1, String value1, String key2, String value2){
        Map<String, String> params = new HashMap<>();
        params.put(key1, value1);
        params.put(key2, value2);
        return params;
    }

    public static Map<String, String> toParamMap(String key1, String value1, String key2, String value2,
                                                 String key3, String value3){
        Map<String, String> params = new HashMap<>();
        params.put(key1, value1);
        params.put(key2, value2);
        params.put(key3, value3);
        return params;
    }

    public static Map<String, String> toParamMap(String key1, String value1, String key2, String value2,
                                                 String key3, String value3, String key4, String value4){
        Map<String, String> params = new HashMap<>();
        params.put(key1, value1);
        params.put(key2, value2);
        params.put(key3, value3);
        params.put(key4, value4);
        return params;
    }

    public static Map<String, String> toParamMap(String key1, String value1, String key2, String value2,
                                                 String key3, String value3, String key4, String value4,
                                                 String key5, String value5){
        Map<String, String> params = new HashMap<>();
        params.put(key1, value1);
        params.put(key2, value2);
        params.put(key3, value3);
        params.put(key4, value4);
        params.put(key5, value5);
        return params;
    }

}
