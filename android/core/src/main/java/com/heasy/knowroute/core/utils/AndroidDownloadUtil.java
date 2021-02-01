package com.heasy.knowroute.core.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class AndroidDownloadUtil {
    private static final Logger logger = LoggerFactory.getLogger(AndroidDownloadUtil.class);

    /**
     * 请求下载
     */
    public static long enqueue(Context context, String url){
        //以下两行代码可以让下载的apk文件被直接安装而不用使用Fileprovider,系统7.0或者以上才启动。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder localBuilder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(localBuilder.build());
        }

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle("下载");
        request.setDescription("知途安装包正在下载.....");

        //下载网络需求  手机数据流量、wifi
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);

        //7.0以上的系统适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            request.setRequiresDeviceIdle(false);
            request.setRequiresCharging(false);
        }

        //制定下载的文件类型为APK
        request.setMimeType("application/vnd.android.package-archive");

        // 下载过程通知栏有通知消息
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);

        //大于11版本手机允许扫描
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            //表示允许MediaScanner扫描到这个文件，默认不允许。
            request.allowScanningByMediaScanner();
        }

        //设置漫游状态下是否可以下载
        request.setAllowedOverRoaming(false);

        //设置文件存放目录
        request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS,"knowroute.apk");

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        long id = downloadManager.enqueue(request);
        return id;
    }

    /**
     * 取消下载
     */
    public static void cancelDownload(Context context, long id){
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.remove(id);
    }

    public static void installAPK(Context context, long id){
        DownloadManager.Query query = new DownloadManager.Query();
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        query.setFilterById(id);
        Cursor cursor = downloadManager.query(query);
        if (cursor != null){
            try {
                if (cursor.moveToFirst()){
                    String filename = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                    logger.debug("filename=" + filename);

                    int status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS));
                    if (status == DownloadManager.STATUS_SUCCESSFUL){
                        Uri uri = Uri.fromFile(new File(filename));
                        if (uri != null){
                            Intent installIntent = new Intent(Intent.ACTION_VIEW);
                            installIntent.setDataAndType(uri,"application/vnd.android.package-archive");
                            installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(installIntent);
                        }
                    }
                }
            }catch (Exception ex){
                logger.error("", ex);
            }finally {
                if(cursor != null) {
                    cursor.close();
                }
            }
        }
    }
}
