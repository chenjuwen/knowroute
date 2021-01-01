package com.heasy.knowroute.service.backend;

import com.heasy.knowroute.bean.ResponseBean;
import com.heasy.knowroute.bean.ResponseCode;
import com.heasy.knowroute.bean.FixedPointInfoBean;
import com.heasy.knowroute.core.utils.FastjsonUtil;
import com.heasy.knowroute.service.common.HttpService;

import java.util.List;

public class FixedPointInfoAPI extends BaseAPI {
    public static List<FixedPointInfoBean> list(int categoryId){
        String url = "fixedPointInfo/list/" + getLoginService().getUserId() + "/" + categoryId;
        ResponseBean responseBean = HttpService.get(getHeasyContext(), url);
        if (responseBean.getCode() == ResponseCode.SUCCESS.code()) {
            List<FixedPointInfoBean> pointList = FastjsonUtil.arrayString2List((String) responseBean.getData(), FixedPointInfoBean.class);
            return pointList;
        }else{
            return null;
        }
    }

    public static Integer saveOrUpdate(String data){
        String url = "fixedPointInfo/saveOrUpdate";
        ResponseBean responseBean = HttpService.postJson(getHeasyContext(), url, data);
        if (responseBean.getCode() == ResponseCode.SUCCESS.code()) {
            String result = (String) responseBean.getData();
            Integer id = FastjsonUtil.string2JSONObject(result).getIntValue("id");
            return id;
        }else{
            return null;
        }
    }

    public static String deleteById(int userId, int id){
        String url = "fixedPointInfo/deleteById/" + userId + "/" + id;
        ResponseBean responseBean = HttpService.postJson(getHeasyContext(), url, "");
        if (responseBean.getCode() == ResponseCode.SUCCESS.code()) {
            return "";
        }else{
            return HttpService.getFailureMessage(responseBean);
        }
    }

}
