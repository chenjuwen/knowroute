package com.heasy.knowroute.map;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.heasy.knowroute.activity.MainActivity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 通知接收器
 */
public class NotificationReceiver extends BroadcastReceiver {
    private static final Logger logger = LoggerFactory.getLogger(NotificationReceiver.class);

    @Override
    public void onReceive(Context context, Intent intent) {
        showProcessInfo(context);

        boolean appAlive = isAppAlive(context, context.getPackageName());
        logger.debug("app运行状态 >> appAlive=" + appAlive);

        if(appAlive){
            logger.debug("在BroadcastReceiver中启动MainActivity");
            Intent mainIntent = new Intent(context, MainActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mainIntent);

        }else{
            logger.debug("在BroadcastReceiver中启动app");
            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
            launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK  | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            context.startActivity(launchIntent);
        }
    }

    private void showProcessInfo(Context context) {
        ActivityManager mAm = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager mPM = context.getPackageManager();

        //最近开的task，HOME键长按会看到这个
        List<ActivityManager.RecentTaskInfo> taskList = mAm.getRecentTasks(100, 0);
        for (ActivityManager.RecentTaskInfo rti : taskList) {
            ResolveInfo ri = mPM.resolveActivity(rti.baseIntent, 0);
            if (ri != null) {
                logger.info("showRecentTask" + " " + ri.loadLabel(mPM));
            }
        }

        // 运行中的任务
        List<ActivityManager.RunningTaskInfo> taskList2 = mAm.getRunningTasks(100);
        for (ActivityManager.RunningTaskInfo rti : taskList2) {
            if(rti.baseActivity.getClassName().indexOf("com.heasy.knowroute") >= 0) {
                logger.info("showRunningTasks" + ", baseActivity=" + rti.baseActivity.getClassName());
            }

            if(rti.topActivity.getClassName().indexOf("com.heasy.knowroute") >=0) {
                logger.info("showRunningTasks" + ", topActivity=" + rti.topActivity.getClassName());
            }
        }

        // 运行中的作为app容器的process
        List<ActivityManager.RunningAppProcessInfo> processList = mAm.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo rapi : processList) {
            if(rapi.processName.indexOf("com.heasy.knowroute") >= 0) {
                logger.info("showRunningAppProcesses" + " " + rapi.processName);
            }
        }

        // 运行中的后台服务
        List<ActivityManager.RunningServiceInfo> rsiList = mAm.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo rsi : rsiList) {
            if(rsi.process.indexOf("com.heasy.knowroute") >= 0) {
                logger.info("showRunningServices" + " " + rsi.process);
            }
        }
    }

    private boolean isAppAlive(Context context, String packageName){
        ActivityManager activityManager =
                (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> processList = activityManager.getRunningAppProcesses();

        for(int i = 0; i < processList.size(); i++){
            if(processList.get(i).processName.equals(packageName)){
                return true;
            }
        }

        return false;
    }
}
