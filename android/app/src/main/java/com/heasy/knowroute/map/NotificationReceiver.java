package com.heasy.knowroute.map;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.heasy.knowroute.activity.MainActivity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Administrator on 2020/10/26.
 */
public class NotificationReceiver extends BroadcastReceiver {
    private static final Logger logger = LoggerFactory.getLogger(NotificationReceiver.class);

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean appAlive = isAppAlive(context, "com.heasy.knowroute");
        logger.debug("app运行状态 >> appAlive=" + appAlive);

        if(appAlive){
            logger.debug("在BroadcastReceiver中启动MainActivity");
            //app存活，但可能不在Task栈中，需要启动主Activity
            Intent mainIntent = new Intent(context, MainActivity.class);
            //mainIntent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            context.startActivity(mainIntent);

        }else{
            logger.debug("在BroadcastReceiver中启动app");
            //app没启动，则启动app
            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("com.heasy.knowroute");
            launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK  | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            context.startActivity(launchIntent);
        }
    }

    private static boolean isAppAlive(Context context, String packageName){
        ActivityManager activityManager =
                (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> processList
                = activityManager.getRunningAppProcesses();

        for(int i = 0; i < processList.size(); i++){
            if(processList.get(i).processName.equals(packageName)){
                return true;
            }
        }

        return false;
    }
}
