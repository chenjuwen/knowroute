package com.heasy.knowroute.service.backend;

import com.heasy.knowroute.bean.ResponseBean;
import com.heasy.knowroute.bean.ResponseCode;
import com.heasy.knowroute.core.Constants;
import com.heasy.knowroute.core.service.ServiceEngineFactory;
import com.heasy.knowroute.core.utils.FastjsonUtil;
import com.heasy.knowroute.core.utils.StringUtil;
import com.heasy.knowroute.service.LoginService;
import com.heasy.knowroute.service.common.AndroidBuiltinService;
import com.heasy.knowroute.service.common.HttpService;

public class ContactAPI extends BaseAPI{
    public static String save(String contactName, String contactPhone){
        LoginService loginService = getLoginService();

        if(contactPhone.equalsIgnoreCase(loginService.getPhone())){
            return "不能添加自己为紧急联系人";
        }

        String url = "contact/save";
        String data = FastjsonUtil.toJSONString("userId", String.valueOf(loginService.getUserId()),
                "contactName", contactName,
                "contactPhone", contactPhone);

        ResponseBean responseBean = HttpService.postJson(getHeasyContext(), url, data);
        if(responseBean.getCode() == ResponseCode.SUCCESS.code()){
            return Constants.SUCCESS;
        }else{
            return HttpService.getFailureMessage(responseBean);
        }
    }

    public static String update(String id, String contactName, String contactPhone){
        LoginService loginService = getLoginService();

        if(contactPhone.equalsIgnoreCase(loginService.getPhone())){
            return "不能添加自己为紧急联系人";
        }

        String url = "contact/update";
        String data = FastjsonUtil.toJSONString("id", id,
                "userId", String.valueOf(loginService.getUserId()),
                "contactName", contactName,
                "contactPhone", contactPhone);

        ResponseBean responseBean = HttpService.postJson(getHeasyContext(), url, data);
        if(responseBean.getCode() == ResponseCode.SUCCESS.code()){
            return Constants.SUCCESS;
        }else{
            return HttpService.getFailureMessage(responseBean);
        }
    }

    public static String delete(String id){
        String url = "contact/delete?id=" + id;
        ResponseBean responseBean = HttpService.get(getHeasyContext(), url);
        if (responseBean.getCode() == ResponseCode.SUCCESS.code()) {
            return Constants.SUCCESS;
        } else {
            return HttpService.getFailureMessage(responseBean);
        }
    }

    public static String getAll(){
        LoginService loginService = getLoginService();
        String url = "contact/getAll?userId=" + loginService.getUserId();
        ResponseBean responseBean = HttpService.get(getHeasyContext(), url);
        if (responseBean.getCode() == ResponseCode.SUCCESS.code()) {
            return (String) responseBean.getData();
        }else{
            return "[]";
        }
    }

    public static String notify(String phones){
        LoginService loginService = getLoginService();

        String message = "【知途】您好，您的朋友" + loginService.getPhone() + "向您发起了紧急求助！";
        message += "点击详情查看TA的位置 " + HttpService.getApiRootAddress(ServiceEngineFactory.getServiceEngine().getHeasyContext()) + "helpme?u=" + loginService.getUserId();
        message += "&p=" + loginService.getPhone();

        if (StringUtil.isNotEmpty(phones)) {
            String[] arr = phones.split(",");
            for (int i = 0; i < arr.length; i++) {
                String phone = arr[i];

                //短信通知
                AndroidBuiltinService.sendSMS(phone, message);

                try {
                    //站内信通知
                    String requestURL = "contact/notify";
                    String data = FastjsonUtil.toJSONString("userId", String.valueOf(loginService.getUserId()),
                            "helpPhone", loginService.getPhone(),
                            "friendPhone", phone);
                    HttpService.postJson(getHeasyContext(), requestURL, data);
                }catch (Exception ex){

                }
            }
            return Constants.SUCCESS;
        } else {
            return "没有紧急联系人，请先添加";
        }
    }

}
