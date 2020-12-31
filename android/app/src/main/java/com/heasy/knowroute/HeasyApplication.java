package com.heasy.knowroute;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.heasy.knowroute.core.service.ServiceEngineFactory;
import com.heasy.knowroute.map.HeasyLocationService;
import com.heasy.knowroute.service.LoginService;
import com.heasy.knowroute.service.LoginServiceImpl;

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
        SDKInitializer.initialize(this);
        SDKInitializer.setCoordType(CoordType.BD09LL);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        exit();
    }

    public void exit(){
        instance = null;

        try{
            Activity activity = getMainActivity();
            Intent serviceIntent = new Intent(activity, HeasyLocationService.class);
            stopService(serviceIntent);
            System.out.print("HeasyLocationService stoped!");
        }catch (Exception ex){
            ex.printStackTrace();
        }

        finishAllActivity();

        try {
            //清除登录信息
            LoginService loginService = ServiceEngineFactory.getServiceEngine().getService(LoginServiceImpl.class);
            if(loginService != null){
                loginService.cleanCache();
            }

            ServiceEngineFactory.getServiceEngine().close();
            System.out.print("ServiceEngine closed!");
        }catch (Exception ex){
            ex.printStackTrace();
        }

        System.exit(0);
        System.out.print("app exit!");
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
