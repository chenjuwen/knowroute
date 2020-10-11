package com.heasy.knowroute.core.webview;

import com.heasy.knowroute.core.HeasyContext;

/**
 * 图片裁剪后的回调接口类
 */
public interface PictureCropCallback {
    void execute(HeasyContext heasyContext, String jsonData, String imagePath);
}
