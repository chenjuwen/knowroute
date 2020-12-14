package com.heasy.knowroute.core.webview;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.webkit.DownloadListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultDownloadListener implements DownloadListener {
    private static final Logger logger = LoggerFactory.getLogger(DefaultDownloadListener.class);
    private Activity activity;

    public DefaultDownloadListener(Activity activity){
        this.activity = activity;
    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
        logger.debug("url=" + url);
        logger.debug("userAgent=" + userAgent);
        logger.debug("contentDisposition=" + contentDisposition);
        logger.debug("mimeType=" + mimeType);
        logger.debug("contentLength=" + contentLength);

        //调用系统内置的浏览器进行下载
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        activity.startActivity(intent);
    }
}
