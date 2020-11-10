package com.heasy.knowroute.action.business;

import com.alibaba.fastjson.JSONObject;
import com.heasy.knowroute.action.AbstractAction;
import com.heasy.knowroute.activity.RouteTrackActivity;
import com.heasy.knowroute.core.Constants;
import com.heasy.knowroute.core.HeasyContext;
import com.heasy.knowroute.core.annotation.JSActionAnnotation;
import com.heasy.knowroute.core.utils.DatetimeUtil;
import com.heasy.knowroute.core.utils.FastjsonUtil;
import com.heasy.knowroute.core.utils.ParameterUtil;
import com.heasy.knowroute.core.utils.StringUtil;
import com.heasy.knowroute.map.HeasyLocationClient;
import com.heasy.knowroute.map.HeasyLocationService;
import com.heasy.knowroute.map.bean.LocationBean;
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

        if("getCurrentPosition".equalsIgnoreCase(extend)){
            LocationBean locationBean = HeasyLocationService.getHeasyLocationClient().getCurrentLocation();
            if(locationBean == null){
                locationBean = new LocationBean();
            }
            locationBean.setTime(DatetimeUtil.getToday(DatetimeUtil.DEFAULT_PATTERN_DT2));
            return FastjsonUtil.object2String(locationBean);

        }else if("viewTrack".equalsIgnoreCase(extend)){
            String userId = FastjsonUtil.getString(jsonObject, "userId");
            String nickName = FastjsonUtil.getString(jsonObject, "nickName");

            if("me".equalsIgnoreCase(userId)){
                userId = String.valueOf(loginService.getUserId());
            }

            Map<String, String> params = new HashMap<>();
            params.put("userId", userId);
            params.put("nickName", nickName);

            startActivity(heasyContext, RouteTrackActivity.class, params);
        }

        return Constants.SUCCESS;
    }
}
