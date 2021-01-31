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
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.heasy.knowroute.R;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 定位服务
 */
public class HeasyLocationService extends Service {
    private static final Logger logger = LoggerFactory.getLogger(HeasyLocationService.class);
    public static final int NOTIFICATION_ID = 2021;
    public static final String CHANNEL_ID = "channel2021";
    public static final int REQUEST_CODE = 2021;

    private static HeasyLocationClient heasyLocationClient;

    @Override
    public void onCreate() {
        super.onCreate();

        heasyLocationClient = new HeasyLocationClient(getApplicationContext());
        heasyLocationClient.init();

        logger.info("sdk int is " + Build.VERSION.SDK_INT);
        Notification notification = getNotification();
        startForeground(NOTIFICATION_ID, notification);

        logger.info("HeasyLocationService created");
    }

    @TargetApi(26)
    private Notification.Builder getNotificationBuilderWithChannel() {
        NotificationManager manager = (NotificationManager)getSystemService (NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel (CHANNEL_ID, "前台服务", NotificationManager.IMPORTANCE_HIGH);
        manager.createNotificationChannel (channel);

        Notification.Builder builder = new Notification.Builder (this, CHANNEL_ID);
        return builder;
    }

    private Notification.Builder getNotificationBuilder(){
        Notification.Builder builder = new Notification.Builder(this);
        return builder;
    }

    private Notification getNotification() {
        Notification.Builder builder = null;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = getNotificationBuilderWithChannel();
        }else{
            builder = getNotificationBuilder();
        }

        builder.setSmallIcon(R.drawable.logo);
        builder.setContentTitle("知途正在保护您的安全");
        builder.setContentText("关闭知途会导致位置信息丢失，请谨慎操作。");
        builder.setDefaults(Notification.DEFAULT_SOUND);
        builder.setAutoCancel(false);
        builder.setWhen(System.currentTimeMillis()); //设置该通知发生的时间

        Intent notifyIntent = new Intent(this, NotificationReceiver.class);
        PendingIntent pIntent = PendingIntent.getBroadcast(this, REQUEST_CODE, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
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

        stopForeground(true);

        logger.info("HeasyLocationService destroy");
    }

    public static HeasyLocationClient getHeasyLocationClient() {
        return heasyLocationClient;
    }
}
