package com.heasy.knowroute.service.backend;

import com.alibaba.fastjson.JSONArray;
import com.heasy.knowroute.bean.ResponseBean;
import com.heasy.knowroute.bean.ResponseCode;
import com.heasy.knowroute.core.utils.DatetimeUtil;
import com.heasy.knowroute.core.utils.FastjsonUtil;
import com.heasy.knowroute.core.utils.ParameterUtil;
import com.heasy.knowroute.core.utils.StringUtil;
import com.heasy.knowroute.map.bean.LocationBean;
import com.heasy.knowroute.service.common.HttpService;

import java.util.Date;

public class PositionAPI extends BaseAPI {
    public static JSONArray getPoints(String userId, Date startDate, Date endDate){
        String requestUrl = "position/getPoints?userId=" + userId
                + "&startDate=" + ParameterUtil.encodeParamValue(DatetimeUtil.formatDate(startDate, DatetimeUtil.DEFAULT_PATTERN_DT2))
                + "&endDate=" + ParameterUtil.encodeParamValue(DatetimeUtil.formatDate(endDate, DatetimeUtil.DEFAULT_PATTERN_DT2));

        ResponseBean responseBean = HttpService.get(getHeasyContext(), requestUrl);
        if(responseBean.getCode() == ResponseCode.SUCCESS.code()) {
            JSONArray jsonArray = FastjsonUtil.string2JSONArray((String) responseBean.getData());
            return jsonArray;
        }else{
            return null;
        }
    }

    public static String insert(LocationBean locationBean){
        String requestUrl = "position/insert";

        String jsonData = FastjsonUtil.toJSONString(
                "id", StringUtil.getUUIDString(),
                "userId", String.valueOf(getLoginService().getUserId()),
                "longitude", String.valueOf(locationBean.getLongitude()),
                "latitude", String.valueOf(locationBean.getLatitude()),
                "address", locationBean.getAddress(),
                "times", locationBean.getTime());

        ResponseBean responseBean = HttpService.postJson(getHeasyContext(), requestUrl, jsonData);
        if(responseBean.getCode() == ResponseCode.SUCCESS.code()){
            return "";
        }else{
            return HttpService.getFailureMessage(responseBean);
        }
    }

}
