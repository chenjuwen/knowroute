package com.heasy.knowroute.core.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by Administrator on 2018/2/13.
 */
public class VersionUtil {
    /**
     * 获取 versionCode 信息，比如2
     */
    public static String getVersionCode(Context context){
        String versionCode = "";
        try {
            PackageInfo packageInfo = getPackageInfo(context);
            if(packageInfo != null) {
                versionCode = String.valueOf(packageInfo.versionCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 获取 versionName 信息，比如0.6.0
     */
    public static String getVersionName(Context context){
        String versionName = "";
        try {
            PackageInfo packageInfo = getPackageInfo(context);
            if(packageInfo != null) {
                versionName = String.valueOf(packageInfo.versionName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    private static PackageInfo getPackageInfo(Context context)throws Exception{
        PackageManager packageManager = context.getPackageManager();
        return packageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
    }

    /**
     * 获取手机Android操作系统的API Level，比如23
     * @return
     */
    public static int getVersionSDK_INT(){
        return android.os.Build.VERSION.SDK_INT;
    }

    /**
     * 获取手机Android操作系统的版本号，比如6.0.1
     */
    public static String getVersionRelease(){
        return android.os.Build.VERSION.RELEASE;
    }

}
