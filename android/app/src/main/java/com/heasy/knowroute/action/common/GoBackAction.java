package com.heasy.knowroute.action.common;

import com.heasy.knowroute.core.Constants;
import com.heasy.knowroute.core.HeasyContext;
import com.heasy.knowroute.core.annotation.JSActionAnnotation;
import com.heasy.knowroute.core.webview.Action;

/**
 * 返回上一页
 */
@JSActionAnnotation(name = "GoBack")
public class GoBackAction implements Action {
    @Override
    public String execute(HeasyContext heasyContext, String jsonData, String extend) {
        heasyContext.getJsInterface().goBack();
        return Constants.SUCCESS;
    }
}
