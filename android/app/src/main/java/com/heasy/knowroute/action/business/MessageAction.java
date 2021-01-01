package com.heasy.knowroute.action.business;

import com.heasy.knowroute.core.Constants;
import com.heasy.knowroute.core.HeasyContext;
import com.heasy.knowroute.core.annotation.JSActionAnnotation;
import com.heasy.knowroute.core.webview.Action;
import com.heasy.knowroute.service.backend.MessageAPI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JSActionAnnotation(name = "MessageAction")
public class MessageAction implements Action {
    private static final Logger logger = LoggerFactory.getLogger(MessageAction.class);

    @Override
    public String execute(HeasyContext heasyContext, String jsonData, String extend) {
        if ("list".equalsIgnoreCase(extend)){
            try {
                return MessageAPI.list();
            }catch (Exception ex){
                logger.error("", ex);
                return "[]";
            }
        }

        return Constants.SUCCESS;
    }

}
