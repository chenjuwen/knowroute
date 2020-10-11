package com.heasy.knowroute.action.common;

import com.heasy.knowroute.core.Constants;
import com.heasy.knowroute.core.HeasyContext;
import com.heasy.knowroute.core.annotation.JSActionAnnotation;
import com.heasy.knowroute.core.event.ExitAppEvent;
import com.heasy.knowroute.core.webview.Action;
import com.heasy.knowroute.action.ActionNames;

/**
 * 退出应用
 */
@JSActionAnnotation(name = ActionNames.ExitApp)
public class ExitAppAction implements Action {
    @Override
    public String execute(final HeasyContext heasyContext, String jsonData, String extend) {
        heasyContext.getServiceEngine().getEventService().postEvent(new ExitAppEvent(this));
        return Constants.SUCCESS;
    }

}
