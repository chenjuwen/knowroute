package com.heasy.knowroute.action.business;

import com.alibaba.fastjson.JSONObject;
import com.heasy.knowroute.action.AbstractAction;
import com.heasy.knowroute.core.Constants;
import com.heasy.knowroute.core.HeasyContext;
import com.heasy.knowroute.core.annotation.JSActionAnnotation;
import com.heasy.knowroute.core.utils.FastjsonUtil;
import com.heasy.knowroute.service.LoginService;
import com.heasy.knowroute.service.LoginServiceImpl;
import com.heasy.knowroute.service.backend.FriendAPI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Administrator on 2020/10/23.
 */
@JSActionAnnotation(name = "FriendAction")
public class FriendAction extends AbstractAction {
    private static final Logger logger = LoggerFactory.getLogger(FriendAction.class);

    @Override
    public String execute(HeasyContext heasyContext, String jsonData, String extend) {
        JSONObject jsonObject = FastjsonUtil.string2JSONObject(jsonData);
        LoginService loginService = heasyContext.getServiceEngine().getService(LoginServiceImpl.class);

        if("getFriendList".equalsIgnoreCase(extend)){
            try {
                return FriendAPI.list();
            }catch (Exception ex){
                logger.error("", ex);
                return "[]";
            }

        }else if ("checkFriend".equalsIgnoreCase(extend)){
            try {
                String phone = FastjsonUtil.getString(jsonObject, "phone");
                return FriendAPI.checkFriend(phone);
            }catch (Exception ex){
                logger.error("", ex);
                return "";
            }

        }else if("invite".equalsIgnoreCase(extend)){
            try {
                String phone = FastjsonUtil.getString(jsonObject, "phone");
                return FriendAPI.invite(phone);
            }catch (Exception ex){
                logger.error("", ex);
                return "邀请失败";
            }

        }else if("add".equalsIgnoreCase(extend)){
            try {
                String phone = FastjsonUtil.getString(jsonObject, "phone");
                return FriendAPI.add(phone);
            }catch (Exception ex){
                logger.error("", ex);
                return "添加好友失败";
            }

        }else if("confirm_add".equalsIgnoreCase(extend)){
            String id = FastjsonUtil.getString(jsonObject, "id");
            String pass = FastjsonUtil.getString(jsonObject, "pass");
            return FriendAPI.confirmAdd(id, pass);

        }else if("delete".equalsIgnoreCase(extend)){
            String id = FastjsonUtil.getString(jsonObject, "id");
            return FriendAPI.delete(id);

        }else if("updateNickname".equalsIgnoreCase(extend)){
            String id = FastjsonUtil.getString(jsonObject, "id");
            String newNickname = FastjsonUtil.getString(jsonObject, "newNickname");
            return FriendAPI.updateNickname(id, newNickname);
        }

        return Constants.SUCCESS;
    }


}
