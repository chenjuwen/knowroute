package com.heasy.knowroute;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.heasy.knowroute.core.utils.AndroidDownloadUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 安装包下载完成后，该广播接收到通知
 */
public class DownLoadCompleteReceiver extends BroadcastReceiver {
    private static final Logger logger = LoggerFactory.getLogger(DownLoadCompleteReceiver.class);

    @Override
    public void onReceive(Context context, Intent intent) {
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())){
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            logger.debug("id=" + id);
            AndroidDownloadUtil.installAPK(context, id);
        }
    }
}
