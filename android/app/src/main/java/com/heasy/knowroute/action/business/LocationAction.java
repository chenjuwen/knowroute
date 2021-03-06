package com.heasy.knowroute.action.business;

import com.alibaba.fastjson.JSONObject;
import com.heasy.knowroute.action.AbstractAction;
import com.heasy.knowroute.activity.FixedPointNavigationActivity;
import com.heasy.knowroute.activity.HelpMapActivity;
import com.heasy.knowroute.activity.RouteTrackActivity;
import com.heasy.knowroute.bean.SimpleUserBean;
import com.heasy.knowroute.core.Constants;
import com.heasy.knowroute.core.HeasyContext;
import com.heasy.knowroute.core.annotation.JSActionAnnotation;
import com.heasy.knowroute.core.utils.AndroidUtil;
import com.heasy.knowroute.core.utils.FastjsonUtil;
import com.heasy.knowroute.core.utils.ParameterUtil;
import com.heasy.knowroute.service.LoginService;
import com.heasy.knowroute.service.LoginServiceImpl;
import com.heasy.knowroute.service.backend.FriendAPI;
import com.heasy.knowroute.service.backend.PositionAPI;
import com.heasy.knowroute.service.backend.UserAPI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2020/10/23.
 */
@JSActionAnnotation(name = "LocationAction")
public class LocationAction extends AbstractAction {
    private static final Logger logger = LoggerFactory.getLogger(LocationAction.class);

    @Override
    public String execute(HeasyContext heasyContext, String jsonData, String extend) {
        JSONObject jsonObject = FastjsonUtil.string2JSONObject(jsonData);
        LoginService loginService = heasyContext.getServiceEngine().getService(LoginServiceImpl.class);

        if("viewTrack".equalsIgnoreCase(extend)){ //查看历史轨迹
            String userId = FastjsonUtil.getString(jsonObject, "userId");
            String relatedUserId = FastjsonUtil.getString(jsonObject, "relatedUserId");
            String nickName = FastjsonUtil.getString(jsonObject, "nickname");

            if("0".equalsIgnoreCase(relatedUserId)){
                relatedUserId = String.valueOf(loginService.getUserId());
            }else{
                //好友是否设置了禁止查看轨迹
                boolean forbid = FriendAPI.checkForbid(Integer.parseInt(userId), Integer.parseInt(relatedUserId));
                if(forbid){
                    AndroidUtil.showToast(heasyContext.getServiceEngine().getAndroidContext(), "禁止查看轨迹");
                    return "";
                }
            }

            Map<String, String> params = new HashMap<>();
            params.put("userId", relatedUserId);
            params.put("nickName", nickName);

            startActivity(heasyContext, RouteTrackActivity.class, params);

        }else if("startLocate".equalsIgnoreCase(extend)){
            String phone = FastjsonUtil.getString(jsonObject, "phone");

            SimpleUserBean userBean = UserAPI.getByPhone(phone);
            if(userBean == null){
                return "无此用户";
            }

            //好友是否设置了禁止查看轨迹
            boolean forbid = FriendAPI.checkForbid(loginService.getUserId(), userBean.getId());
            if(forbid){
                AndroidUtil.showToast(heasyContext.getServiceEngine().getAndroidContext(), "禁止查看轨迹");
                return Constants.SUCCESS;
            }

            Map<String, String> params = new HashMap<>();
            params.put("userId", String.valueOf(userBean.getId()));
            params.put("nickName", userBean.getNickname());

            startActivity(heasyContext, RouteTrackActivity.class, params);

        }else if("helpMap".equalsIgnoreCase(extend)){
            String userId = FastjsonUtil.getString(jsonObject, "userId");
            startActivity(heasyContext, HelpMapActivity.class, ParameterUtil.toParamMap("userId", userId));

        }else if("pointNavigation".equalsIgnoreCase(extend)){ //定点导航
            startActivity(heasyContext, FixedPointNavigationActivity.class, null);
        }else if("cleanup".equalsIgnoreCase(extend)){
            String monthsAgo = FastjsonUtil.getString(jsonObject, "monthsAgo");
            return PositionAPI.cleanup(monthsAgo);
        }

        return Constants.SUCCESS;
    }
}
