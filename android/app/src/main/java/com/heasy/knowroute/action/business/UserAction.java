package com.heasy.knowroute.action.business;

import com.alibaba.fastjson.JSONObject;
import com.heasy.knowroute.action.ResponseBean;
import com.heasy.knowroute.action.ResponseCode;
import com.heasy.knowroute.core.Constants;
import com.heasy.knowroute.core.HeasyContext;
import com.heasy.knowroute.core.annotation.JSActionAnnotation;
import com.heasy.knowroute.core.utils.FastjsonUtil;
import com.heasy.knowroute.core.webview.Action;
import com.heasy.knowroute.service.HttpService;
import com.heasy.knowroute.service.LoginService;
import com.heasy.knowroute.service.LoginServiceImpl;
import com.heasy.knowroute.service.VersionService;
import com.heasy.knowroute.service.VersionServiceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JSActionAnnotation(name = "UserAction")
public class UserAction implements Action {
    private static final Logger logger = LoggerFactory.getLogger(UserAction.class);

    @Override
    public String execute(HeasyContext heasyContext, String jsonData, String extend) {
        JSONObject jsonObject = FastjsonUtil.string2JSONObject(jsonData);
        LoginService loginService = heasyContext.getServiceEngine().getService(LoginServiceImpl.class);
        VersionService versionService = heasyContext.getServiceEngine().getService(VersionServiceImpl.class);

        if ("cancelAccount".equalsIgnoreCase(extend)){
            try {
                String url = "user/cancel?id=" + loginService.getUserId();
                ResponseBean responseBean = HttpService.get(heasyContext, url);
                if (responseBean.getCode() == ResponseCode.SUCCESS.code()) {
                    return Constants.SUCCESS;
                }
            }catch (Exception ex){
                logger.error("", ex);
            }
            return "销户失败";
        }else if("my-info".equalsIgnoreCase(extend)){
            try {
                String currentVersion = "V" + versionService.getCurrentVersion(); //当前版本
                String downloadURL = versionService.getLastedVersionDownloadURL(); //最新版本下载地址
                String nickname = loginService.getNickname();

                String jsonStr = FastjsonUtil.toJSONString("currentVersion", currentVersion,
                        "downloadURL", downloadURL, "nickname", nickname);
                logger.debug(jsonStr);

                return jsonStr;
            }catch (Exception ex){
                logger.error("", ex);
            }
            return "{}";
        }

        return Constants.SUCCESS;
    }
}
