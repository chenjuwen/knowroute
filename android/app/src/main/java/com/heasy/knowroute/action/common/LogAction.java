package com.heasy.knowroute.action.common;

import com.heasy.knowroute.core.Constants;
import com.heasy.knowroute.core.HeasyContext;
import com.heasy.knowroute.core.annotation.JSActionAnnotation;
import com.heasy.knowroute.core.utils.StringUtil;
import com.heasy.knowroute.core.webview.Action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志记录
 */
@JSActionAnnotation(name = "Log")
public class LogAction implements Action {
    private static final Logger logger = LoggerFactory.getLogger(LogAction.class);

    @Override
    public String execute(HeasyContext heasyContext, String jsonData, String extend) {
        String logLevel = StringUtil.trimToEmpty(extend).toUpperCase();
        String logMessage = StringUtil.trimToEmpty(jsonData);

        switch (logLevel){
            case "INFO":
                logger.info(logMessage);
                break;
            case "WARN":
                logger.warn(logMessage);
                break;
            case "ERROR":
                logger.error(logMessage);
                break;
            default:
                logger.debug(logMessage);
        }

        return Constants.SUCCESS;
    }

}
