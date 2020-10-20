package com.heasy.knowroute.core.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2018/2/13.
 */
public class SharedPreferencesUtil {
    private static final String FILE_NAME = "config";

    private static SharedPreferences getSharedPreferences(Context context){
        return context.getSharedPreferences(FILE_NAME, Activity.MODE_PRIVATE);
    }

    /***
     * get方法
     */
    public static String getString(Context context, String key){
        return getSharedPreferences(context).getString(key, "");
    }

    public static String getString(Context context, String key, String defaultValue){
        return getSharedPreferences(context).getString(key, defaultValue);
    }

    public static boolean getBoolean(Context context, String key, boolean defaultValue){
        return getSharedPreferences(context).getBoolean(key, defaultValue);
    }

    public static float getFloat(Context context, String key, float defaultValue){
        return getSharedPreferences(context).getFloat(key, defaultValue);
    }

    public static int getInt(Context context, String key, int defaultValue){
        return getSharedPreferences(context).getInt(key, defaultValue);
    }

    public static long getLong(Context context, String key, long defaultValue){
        return getSharedPreferences(context).getLong(key, defaultValue);
    }

    public static String getStringSetValue(Context context, String key){
        Set<String> set = getSharedPreferences(context).getStringSet(key, null);
        StringBuffer sb = new StringBuffer();
        if(set != null){
            int i = 0;
            for(Iterator<String> it=set.iterator(); it.hasNext();){
                if(i > 0){
                    sb.append(",");
                }
                sb.append(it.next());
                ++i;
            }
        }
        return sb.toString();
    }

    /***
     * put方法
     */
    public static void putString(Context context, String key, String value){
        getSharedPreferences(context).edit().putString(key, value).commit();
    }

    public static void putBoolean(Context context, String key, boolean value){
        getSharedPreferences(context).edit().putBoolean(key, value).commit();
    }

    public static void putFloat(Context context, String key, float value){
        getSharedPreferences(context).edit().putFloat(key, value).commit();
    }

    public static void putInt(Context context, String key, int value){
        getSharedPreferences(context).edit().putInt(key, value).commit();
    }

    public static void putLong(Context context, String key, long value){
        getSharedPreferences(context).edit().putLong(key, value).commit();
    }

    public static void put(Context context, Map<String, Object> map){
        if(map != null){
            SharedPreferences.Editor editor = getSharedPreferences(context).edit();
            for(Iterator<String> it = map.keySet().iterator(); it.hasNext();){
                String key = it.next();
                Object value = map.get(key);
                editor.putString(key, String.valueOf(value));
            }
            editor.commit();
        }
    }


    /***
     * remove方法
     */
    public static void remove(Context context, String...keys){
        if(keys != null && keys.length > 0){
            SharedPreferences.Editor editor = getSharedPreferences(context).edit();
            for(String key : keys) {
                editor.remove(key);
            }
            editor.commit();
        }
    }


    /***
     * clear方法
     */
    public static void clear(Context context){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.clear();
        editor.commit();
    }

}
