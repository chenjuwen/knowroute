package com.heasy.knowroute.action.lottery;

import com.alibaba.fastjson.JSONObject;
import com.heasy.knowroute.core.Constants;
import com.heasy.knowroute.core.HeasyContext;
import com.heasy.knowroute.core.annotation.JSActionAnnotation;
import com.heasy.knowroute.core.utils.FastjsonUtil;
import com.heasy.knowroute.core.webview.Action;
import com.heasy.knowroute.service.UserService;
import com.heasy.knowroute.service.UserServiceImpl;

@JSActionAnnotation(name = "UserAction")
public class UserAction implements Action {
    @Override
    public String execute(HeasyContext heasyContext, String jsonData, String extend) {
        JSONObject jsonObject = FastjsonUtil.string2JSONObject(jsonData);

        if("changePassword".equalsIgnoreCase(extend)) {
            String account = FastjsonUtil.getString(jsonObject, "account");
            String old_password = FastjsonUtil.getString(jsonObject, "old_password");
            String new_password = FastjsonUtil.getString(jsonObject, "new_password");

            UserService userService = heasyContext.getServiceEngine().getService(UserServiceImpl.class);
            String result = userService.changePassword(account, old_password, new_password);
            return result;
        }

        return Constants.SUCCESS;
    }
}
