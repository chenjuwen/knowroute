package com.heasy.knowroute.action.business;

import com.alibaba.fastjson.JSONObject;
import com.heasy.knowroute.action.AbstractAction;
import com.heasy.knowroute.action.ResponseBean;
import com.heasy.knowroute.action.ResponseCode;
import com.heasy.knowroute.activity.HelpMapActivity;
import com.heasy.knowroute.activity.FixedPointNavigationActivity;
import com.heasy.knowroute.activity.RouteTrackActivity;
import com.heasy.knowroute.bean.UserBean;
import com.heasy.knowroute.core.Constants;
import com.heasy.knowroute.core.HeasyContext;
import com.heasy.knowroute.core.annotation.JSActionAnnotation;
import com.heasy.knowroute.core.service.ServiceEngineFactory;
import com.heasy.knowroute.core.utils.FastjsonUtil;
import com.heasy.knowroute.core.utils.ParameterUtil;
import com.heasy.knowroute.service.HttpService;
import com.heasy.knowroute.service.LoginService;
import com.heasy.knowroute.service.LoginServiceImpl;

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
            String nickName = FastjsonUtil.getString(jsonObject, "nickName");

            if("me".equalsIgnoreCase(userId)){
                userId = String.valueOf(loginService.getUserId());
            }

            Map<String, String> params = new HashMap<>();
            params.put("userId", userId);
            params.put("nickName", nickName);

            startActivity(heasyContext, RouteTrackActivity.class, params);

        }else if("startLocate".equalsIgnoreCase(extend)){
            String phone = FastjsonUtil.getString(jsonObject, "phone");

            String requestUrl = "user/getByPhone?phone=" + phone;
            ResponseBean responseBean = HttpService.get(ServiceEngineFactory.getServiceEngine().getHeasyContext(), requestUrl);
            if(responseBean.getCode() == ResponseCode.SUCCESS.code()) {
                UserBean userBean = FastjsonUtil.string2JavaBean((String) responseBean.getData(), UserBean.class);

                if(userBean == null){
                    return "无此用户";
                }

                Map<String, String> params = new HashMap<>();
                params.put("userId", String.valueOf(userBean.getId()));
                params.put("nickName", userBean.getNickname());

                startActivity(heasyContext, RouteTrackActivity.class, params);
            }
        }else if("helpMap".equalsIgnoreCase(extend)){
            String userId = FastjsonUtil.getString(jsonObject, "userId");
            startActivity(heasyContext, HelpMapActivity.class, ParameterUtil.toParamMap("userId", userId));
        }else if("pointNavigation".equalsIgnoreCase(extend)){ //定点导航
            startActivity(heasyContext, FixedPointNavigationActivity.class, null);
        }

        return Constants.SUCCESS;
    }
}
