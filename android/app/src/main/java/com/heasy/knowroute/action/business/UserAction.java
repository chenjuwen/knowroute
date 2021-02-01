package com.heasy.knowroute.action.business;

import com.alibaba.fastjson.JSONObject;
import com.heasy.knowroute.bean.ResponseCode;
import com.heasy.knowroute.bean.VersionInfoBean;
import com.heasy.knowroute.core.Constants;
import com.heasy.knowroute.core.HeasyContext;
import com.heasy.knowroute.core.annotation.JSActionAnnotation;
import com.heasy.knowroute.core.utils.FastjsonUtil;
import com.heasy.knowroute.core.webview.Action;
import com.heasy.knowroute.service.LoginService;
import com.heasy.knowroute.service.LoginServiceImpl;
import com.heasy.knowroute.service.VersionService;
import com.heasy.knowroute.service.VersionServiceImpl;
import com.heasy.knowroute.service.backend.UserAPI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JSActionAnnotation(name = "UserAction")
public class UserAction implements Action {
    private static final Logger logger = LoggerFactory.getLogger(UserAction.class);

    @Override
    public String execute(HeasyContext heasyContext, String jsonData, String extend) {
        JSONObject jsonObject = FastjsonUtil.string2JSONObject(jsonData);
        LoginService loginService = heasyContext.getServiceEngine().getService(LoginServiceImpl.class);

        if ("cancelAccount".equalsIgnoreCase(extend)){
            try {
                return UserAPI.cancel();
            }catch (Exception ex){
                logger.error("", ex);
                return "销户失败";
            }

        }else if("my-info".equalsIgnoreCase(extend)){
            try {
                VersionService versionService = heasyContext.getServiceEngine().getService(VersionServiceImpl.class);
                VersionInfoBean versionInfoBean = versionService.getVersionInfo();

                String currentVersion = "V" + versionInfoBean.getCurrentVersion(); //当前版本
                String downloadURL = versionInfoBean.getLastedVersionURL(); //最新版本下载地址
                String nickname = loginService.getNickname();

                String jsonStr = FastjsonUtil.toJSONString("currentVersion", currentVersion,
                        "downloadURL", downloadURL, "nickname", nickname);
                logger.debug(jsonStr);

                return jsonStr;
            }catch (Exception ex){
                logger.error("", ex);
            }
            return "{}";

        }else if("updateNickname".equalsIgnoreCase(extend)){
            try {
                String newNickname = FastjsonUtil.getString(jsonObject, "new_nickname");
                return UserAPI.updateNickname(newNickname);
            }catch (Exception ex){
                logger.error("", ex);
                return ResponseCode.FAILURE.message();
            }
        }

        return Constants.SUCCESS;
    }

}
