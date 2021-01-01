package com.heasy.knowroute.service.backend;

import com.heasy.knowroute.bean.ResponseBean;
import com.heasy.knowroute.bean.ResponseCode;
import com.heasy.knowroute.core.Constants;
import com.heasy.knowroute.core.utils.FastjsonUtil;
import com.heasy.knowroute.service.common.HttpService;

public class FixedPointCategoryAPI extends BaseAPI{
    public static String list(){
        String url = "fixedPointCategory/list/" + getLoginService().getUserId();
        ResponseBean responseBean = HttpService.get(getHeasyContext(), url);
        if (responseBean.getCode() == ResponseCode.SUCCESS.code()) {
            return (String) responseBean.getData();
        }else{
            return "[]";
        }
    }

    public static String insert(String name){
        String url = "fixedPointCategory/insert";
        String data = FastjsonUtil.toJSONString("userId", String.valueOf(getLoginService().getUserId()),
                "name", name);

        ResponseBean responseBean = HttpService.postJson(getHeasyContext(), url, data);
        if(responseBean.getCode() == ResponseCode.SUCCESS.code()){
            return Constants.SUCCESS;
        }else{
            return HttpService.getFailureMessage(responseBean);
        }
    }

    public static String update(String id, String name){
        String url = "fixedPointCategory/update";
        String data = FastjsonUtil.toJSONString("id", id, "name", name);

        ResponseBean responseBean = HttpService.postJson(getHeasyContext(), url, data);
        if(responseBean.getCode() == ResponseCode.SUCCESS.code()){
            return Constants.SUCCESS;
        }else{
            return HttpService.getFailureMessage(responseBean);
        }
    }

    public static String delete(String id){
        String url = "fixedPointCategory/delete/" + id;
        ResponseBean responseBean = HttpService.postJson(getHeasyContext(), url, "");
        if(responseBean.getCode() == ResponseCode.SUCCESS.code()){
            return Constants.SUCCESS;
        }else{
            return HttpService.getFailureMessage(responseBean);
        }
    }

    public static String topping(String id){
        String url = "fixedPointCategory/topping/" + id;
        ResponseBean responseBean = HttpService.postJson(getHeasyContext(), url, "");
        if(responseBean.getCode() == ResponseCode.SUCCESS.code()){
            return Constants.SUCCESS;
        }else{
            return HttpService.getFailureMessage(responseBean);
        }
    }

    public static String cancelTopping(String id){
        String url = "fixedPointCategory/cancelTopping/" + id;
        ResponseBean responseBean = HttpService.postJson(getHeasyContext(), url, "");
        if(responseBean.getCode() == ResponseCode.SUCCESS.code()){
            return Constants.SUCCESS;
        }else{
            return HttpService.getFailureMessage(responseBean);
        }
    }

}
