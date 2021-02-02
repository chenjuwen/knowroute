package com.heasy.knowroute.action.common;

import com.alibaba.fastjson.JSONObject;
import com.heasy.knowroute.core.Constants;
import com.heasy.knowroute.core.HeasyContext;
import com.heasy.knowroute.core.annotation.JSActionAnnotation;
import com.heasy.knowroute.core.utils.AndroidDownloadUtil;
import com.heasy.knowroute.core.utils.FastjsonUtil;
import com.heasy.knowroute.core.webview.Action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JSActionAnnotation(name = "VersionAction")
public class VersionAction implements Action {
    private static final Logger logger = LoggerFactory.getLogger(VersionAction.class);

    @Override
    public String execute(HeasyContext heasyContext, String jsonData, String extend) {
        JSONObject jsonObject = FastjsonUtil.string2JSONObject(jsonData);

        if("download".equalsIgnoreCase(extend)){
            //下载最新版本
            String downloadURL = FastjsonUtil.getString(jsonObject, "downloadURL");
            AndroidDownloadUtil.enqueue(heasyContext.getServiceEngine().getAndroidContext(), downloadURL);
        }

        return Constants.SUCCESS;
    }

}
