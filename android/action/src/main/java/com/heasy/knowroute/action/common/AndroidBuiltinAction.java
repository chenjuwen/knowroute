package com.heasy.knowroute.action.common;

import com.heasy.knowroute.core.Constants;
import com.heasy.knowroute.core.HeasyContext;
import com.heasy.knowroute.core.annotation.JSActionAnnotation;
import com.heasy.knowroute.core.webview.Action;
import com.heasy.knowroute.service.AndroidBuiltinService;

/**
 * Android自带组件
 */
@JSActionAnnotation(name = "AndroidBuiltin")
public class AndroidBuiltinAction implements Action {
    @Override
    public String execute(HeasyContext heasyContext, String jsonData, String extend) {
        if("getContactInfo".equalsIgnoreCase(extend)){
            AndroidBuiltinService.getContactInfo(heasyContext);
        }

        return Constants.SUCCESS;
    }

}
