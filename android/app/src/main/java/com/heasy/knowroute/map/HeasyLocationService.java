package com.heasy.knowroute.map;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.heasy.knowroute.HeasyApplication;
import com.heasy.knowroute.R;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 定位服务
 */
public class HeasyLocationService extends Service {
    private static final Logger logger = LoggerFactory.getLogger(HeasyLocationService.class);
    private static HeasyLocationClient heasyLocationClient;

    @Override
    public void onCreate() {
        super.onCreate();

        heasyLocationClient = new HeasyLocationClient(getApplicationContext());
        heasyLocationClient.init();

        if(Build.VERSION.SDK_INT >= HeasyApplication.ANDROID8_SDK_INT) {
            Notification notification = getNotificationWithChannel();
            startForeground(2000, notification);
            //heasyLocationClient.enableLocInForeground(notification);
        }else{
            Notification notification = getNotification();
            startForeground(2000, notification);
        }

        logger.info("HeasyLocationService created");
    }

    @TargetApi (26)
    private Notification getNotificationWithChannel() {
        NotificationManager manager = (NotificationManager)getSystemService (NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel ("channel0001", "知途前台服务", NotificationManager.IMPORTANCE_HIGH);
        manager.createNotificationChannel (channel);

        Notification.Builder builder = new Notification.Builder (this, "channel0001");
        builder.setSmallIcon(R.drawable.logo);
        builder.setContentTitle("知途正在保护您的安全");
        builder.setContentText("关闭知途会导致位置信息丢失，请谨慎操作。");
        builder.setDefaults(Notification.DEFAULT_SOUND);
        builder.setAutoCancel(false);
        builder.setWhen(System.currentTimeMillis()); //设置该通知发生的时间

        Intent notifyIntent = new Intent(this, NotificationReceiver.class);
        PendingIntent pIntent = PendingIntent.getBroadcast(this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pIntent);

        return builder.build();
    }

    private Notification getNotification() {
        //启动前台Service
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.drawable.logo);
        builder.setContentTitle("知途正在保护您的安全");
        builder.setContentText("关闭知途会导致位置信息丢失，请谨慎操作。");
        builder.setDefaults(Notification.DEFAULT_SOUND);
        builder.setAutoCancel(false);
        builder.setWhen(System.currentTimeMillis()); //设置该通知发生的时间

        Intent notifyIntent = new Intent(this, NotificationReceiver.class);
        PendingIntent pIntent = PendingIntent.getBroadcast(this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pIntent);

        return builder.build();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(heasyLocationClient != null) {
            heasyLocationClient.destroy();
            heasyLocationClient = null;
        }

        //if(Build.VERSION.SDK_INT >= HeasyApplication.ANDROID8_SDK_INT) {
        //    heasyLocationClient.disableLocInForeground();
        //}else{
            //停止前台Service
            stopForeground(true);
        //}

        logger.info("HeasyLocationService destroy");
    }

    public static HeasyLocationClient getHeasyLocationClient() {
        return heasyLocationClient;
    }
}
