package com.heasy.knowroute.service.backend;

import com.heasy.knowroute.bean.PointBean;
import com.heasy.knowroute.bean.ResponseBean;
import com.heasy.knowroute.bean.ResponseCode;
import com.heasy.knowroute.core.Constants;
import com.heasy.knowroute.core.utils.DatetimeUtil;
import com.heasy.knowroute.core.utils.FastjsonUtil;
import com.heasy.knowroute.core.utils.ParameterUtil;
import com.heasy.knowroute.map.bean.LocationBean;
import com.heasy.knowroute.service.common.HttpService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

public class PositionAPI extends BaseAPI {
    private static final Logger logger = LoggerFactory.getLogger(PositionAPI.class);

    public static List<PointBean> getPoints(String userId, Date startDate, Date endDate){
        String requestUrl = "position/getPoints?userId=" + userId
                + "&startDate=" + ParameterUtil.encodeParamValue(DatetimeUtil.formatDate(startDate, DatetimeUtil.DEFAULT_PATTERN_DT2))
                + "&endDate=" + ParameterUtil.encodeParamValue(DatetimeUtil.formatDate(endDate, DatetimeUtil.DEFAULT_PATTERN_DT2));

        ResponseBean responseBean = HttpService.get(getHeasyContext(), requestUrl);
        if(responseBean.getCode() == ResponseCode.SUCCESS.code()) {
            String data = (String) responseBean.getData();
            //logger.debug(data);
            List<PointBean> dataList = FastjsonUtil.arrayString2List(data, PointBean.class);
            return dataList;
        }else{
            return null;
        }
    }

    public static String insert(List<LocationBean> dataList){
        String requestUrl = "position/insert";
        String jsonData = FastjsonUtil.object2ArrayString(dataList);
        ResponseBean responseBean = HttpService.postJson(getHeasyContext(), requestUrl, jsonData);
        if(responseBean.getCode() == ResponseCode.SUCCESS.code()){
            return "";
        }else{
            return HttpService.getFailureMessage(responseBean);
        }
    }

    public static String cleanup(String monthsAgo){
        String requestUrl = "position/cleanup/" + getLoginService().getUserId() + "/" + monthsAgo;
        ResponseBean responseBean = HttpService.postJson(getHeasyContext(), requestUrl, "");
        if(responseBean.getCode() == ResponseCode.SUCCESS.code()){
            return Constants.SUCCESS;
        }else{
            return HttpService.getFailureMessage(responseBean);
        }
    }

}
