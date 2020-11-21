package com.heasy.knowroute;

import android.app.Activity;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2017/9/14.
 */
public class HeasyApplication extends MultiDexApplication {
    private static HeasyApplication instance;
    private ConcurrentHashMap<String, Activity> activities = new ConcurrentHashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initMapSDK();

        //创建日志文件存储目录
        File logDir = new File("/sdcard/heasy/logs");
        if(!logDir.exists()){
            logDir.mkdirs();
        }
    }

    private void initMapSDK(){
        // 在使用 SDK 各组件之前初始化 context 信息，传入 ApplicationContext
        // 默认本地个性化地图初始化方法
        SDKInitializer.initialize(this);

        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        exit();
    }

    public void exit(){
        instance = null;

        finishAllActivity();

        try {
            ServiceEngineFactory.getServiceEngine().close();
        }catch (Exception ex){
            ex.printStackTrace();
        }

        System.exit(0);
    }

    public static HeasyApplication getInstance(){
        return instance;
    }

    public void addActivity(Activity activity){
        activities.put(activity.getClass().getSimpleName(), activity);
    }

    public Activity getMainActivity(){
        return activities.get("MainActivity");
    }

    public void finishActivity(Activity activity){
        if (activity != null) {
            this.activities.remove(activity.getClass().getSimpleName());
            activity.finish();
        }
    }

    public void finishAllActivity(){
        for (Activity activity : activities.values()) {
            if (null != activity) {
                activity.finish();
            }
        }
        activities.clear();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
