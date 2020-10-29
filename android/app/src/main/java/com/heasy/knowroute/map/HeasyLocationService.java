package com.heasy.knowroute.map;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

import com.heasy.knowroute.R;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 定位服务
 */
public class HeasyLocationService extends Service {
    private static final Logger logger = LoggerFactory.getLogger(HeasyLocationService.class);
    private HeasyLocationClient heasyLocationClient;

    @Override
    public void onCreate() {
        super.onCreate();

        heasyLocationClient = new HeasyLocationClient(getApplicationContext());
        heasyLocationClient.init();

        //启动前台Service
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.logo);
        builder.setContentTitle("知途正在保护您的安全");
        builder.setContentText("关闭知途会导致位置信息丢失，请谨慎操作。");
        builder.setDefaults(Notification.DEFAULT_SOUND);
        builder.setAutoCancel(false);

        Intent notifyIntent = new Intent(this, NotificationReceiver.class);
        PendingIntent pIntent = PendingIntent.getBroadcast(this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pIntent);

        Notification notification = builder.build();

        //NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        //notificationManager.notify(999, notification);

        startForeground(999, notification);

        logger.info("HeasyLocationService created");
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
        }

        //停止前台Service
        stopForeground(true);

        logger.info("HeasyLocationService destroy");
    }

}
