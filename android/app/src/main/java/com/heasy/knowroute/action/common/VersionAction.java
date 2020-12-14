package com.heasy.knowroute.action.common;

import com.heasy.knowroute.core.Constants;
import com.heasy.knowroute.core.HeasyContext;
import com.heasy.knowroute.core.annotation.JSActionAnnotation;
import com.heasy.knowroute.core.webview.Action;
import com.heasy.knowroute.service.VersionService;
import com.heasy.knowroute.service.VersionServiceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JSActionAnnotation(name = "VersionAction")
public class VersionAction implements Action {
    private static final Logger logger = LoggerFactory.getLogger(VersionAction.class);
    @Override
    public String execute(HeasyContext heasyContext, String jsonData, String extend) {
        VersionService versionService = heasyContext.getServiceEngine().getService(VersionServiceImpl.class);

        if("current".equalsIgnoreCase(extend)) {
            return versionService.getCurrentVersion();
        }else if("lasted".equalsIgnoreCase(extend)){
            return versionService.getLastedVersion();
        }

        return Constants.SUCCESS;
    }

}
