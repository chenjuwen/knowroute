package com.heasy.knowroute.action.common;

import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.heasy.knowroute.HeasyApplication;
import com.heasy.knowroute.core.Constants;
import com.heasy.knowroute.core.DefaultDaemonThread;
import com.heasy.knowroute.core.HeasyContext;
import com.heasy.knowroute.core.annotation.JSActionAnnotation;
import com.heasy.knowroute.core.utils.AndroidUtil;
import com.heasy.knowroute.core.utils.FastjsonUtil;
import com.heasy.knowroute.core.utils.VersionUtil;
import com.heasy.knowroute.core.webview.Action;
import com.heasy.knowroute.service.AndroidBuiltinService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

@JSActionAnnotation(name = "VersionAction")
public class VersionAction implements Action {
    private static final Logger logger = LoggerFactory.getLogger(VersionAction.class);

    @Override
    public String execute(HeasyContext heasyContext, String jsonData, String extend) {
        String versionName = "V" +VersionUtil.getVersionName(heasyContext.getServiceEngine().getAndroidContext());
        return versionName;
    }

}
