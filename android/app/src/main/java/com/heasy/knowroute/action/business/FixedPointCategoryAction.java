package com.heasy.knowroute.action.business;

import com.alibaba.fastjson.JSONObject;
import com.heasy.knowroute.action.ResponseBean;
import com.heasy.knowroute.action.ResponseCode;
import com.heasy.knowroute.core.Constants;
import com.heasy.knowroute.core.HeasyContext;
import com.heasy.knowroute.core.annotation.JSActionAnnotation;
import com.heasy.knowroute.core.utils.FastjsonUtil;
import com.heasy.knowroute.core.webview.Action;
import com.heasy.knowroute.service.HttpService;
import com.heasy.knowroute.service.LoginService;
import com.heasy.knowroute.service.LoginServiceImpl;
import com.heasy.knowroute.service.UserService;
import com.heasy.knowroute.service.UserServiceImpl;
import com.heasy.knowroute.service.VersionService;
import com.heasy.knowroute.service.VersionServiceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JSActionAnnotation(name = "FixedPointCategoryAction")
public class FixedPointCategoryAction implements Action {
    private static final Logger logger = LoggerFactory.getLogger(FixedPointCategoryAction.class);

    @Override
    public String execute(HeasyContext heasyContext, String jsonData, String extend) {
        JSONObject jsonObject = FastjsonUtil.string2JSONObject(jsonData);
        LoginService loginService = heasyContext.getServiceEngine().getService(LoginServiceImpl.class);

        if ("list".equalsIgnoreCase(extend)){
            try {
                String url = "fixedPointCategory/list/" + loginService.getUserId();
                ResponseBean responseBean = HttpService.get(heasyContext, url);
                if (responseBean.getCode() == ResponseCode.SUCCESS.code()) {
                    return (String) responseBean.getData();
                }
            }catch (Exception ex){
                logger.error("", ex);
            }
            return "[]";

        }else if("insert".equalsIgnoreCase(extend)){
            try {
                String name = FastjsonUtil.getString(jsonObject, "name");

                String url = "fixedPointCategory/insert";
                String data = FastjsonUtil.toJSONString("userId", String.valueOf(loginService.getUserId()),
                        "name", name);

                ResponseBean responseBean = HttpService.postJson(heasyContext, url, data);
                if(responseBean.getCode() == ResponseCode.SUCCESS.code()){
                    return Constants.SUCCESS;
                }else{
                    return HttpService.getFailureMessage(responseBean);
                }
            }catch (Exception ex){
                logger.error("", ex);
            }
            return ResponseCode.FAILURE.message();
        }else if("update".equalsIgnoreCase(extend)){
            try {
                String id = FastjsonUtil.getString(jsonObject, "id");
                String name = FastjsonUtil.getString(jsonObject, "name");

                String url = "fixedPointCategory/update";
                String data = FastjsonUtil.toJSONString("id", id, "name", name);

                ResponseBean responseBean = HttpService.postJson(heasyContext, url, data);
                if(responseBean.getCode() == ResponseCode.SUCCESS.code()){
                    return Constants.SUCCESS;
                }else{
                    return HttpService.getFailureMessage(responseBean);
                }
            }catch (Exception ex){
                logger.error("", ex);
            }
            return ResponseCode.FAILURE.message();
        }else if("delete".equalsIgnoreCase(extend)){
            try {
                String id = FastjsonUtil.getString(jsonObject, "id");
                String url = "fixedPointCategory/delete/" + id;

                ResponseBean responseBean = HttpService.postJson(heasyContext, url, "");
                if(responseBean.getCode() == ResponseCode.SUCCESS.code()){
                    return Constants.SUCCESS;
                }else{
                    return HttpService.getFailureMessage(responseBean);
                }
            }catch (Exception ex){
                logger.error("", ex);
            }
            return ResponseCode.FAILURE.message();
        }else if("topping".equalsIgnoreCase(extend)){
            try {
                String id = FastjsonUtil.getString(jsonObject, "id");
                String url = "fixedPointCategory/topping/" + id;

                ResponseBean responseBean = HttpService.postJson(heasyContext, url, "");
                if(responseBean.getCode() == ResponseCode.SUCCESS.code()){
                    return Constants.SUCCESS;
                }else{
                    return HttpService.getFailureMessage(responseBean);
                }
            }catch (Exception ex){
                logger.error("", ex);
            }
            return ResponseCode.FAILURE.message();
        }else if("cancelTopping".equalsIgnoreCase(extend)){
            try {
                String id = FastjsonUtil.getString(jsonObject, "id");
                String url = "fixedPointCategory/cancelTopping/" + id;

                ResponseBean responseBean = HttpService.postJson(heasyContext, url, "");
                if(responseBean.getCode() == ResponseCode.SUCCESS.code()){
                    return Constants.SUCCESS;
                }else{
                    return HttpService.getFailureMessage(responseBean);
                }
            }catch (Exception ex){
                logger.error("", ex);
            }
            return ResponseCode.FAILURE.message();
        }

        return Constants.SUCCESS;
    }
}
