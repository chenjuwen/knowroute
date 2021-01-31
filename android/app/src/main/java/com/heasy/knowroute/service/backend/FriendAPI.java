package com.heasy.knowroute.service.backend;

import com.alibaba.fastjson.JSONObject;
import com.heasy.knowroute.bean.ResponseBean;
import com.heasy.knowroute.bean.ResponseCode;
import com.heasy.knowroute.bean.UserBean;
import com.heasy.knowroute.core.Constants;
import com.heasy.knowroute.core.service.ServiceEngineFactory;
import com.heasy.knowroute.core.utils.FastjsonUtil;
import com.heasy.knowroute.map.HeasyLocationService;
import com.heasy.knowroute.map.bean.LocationBean;
import com.heasy.knowroute.service.common.AndroidBuiltinService;
import com.heasy.knowroute.service.common.HttpService;

public class FriendAPI extends BaseAPI {
    public static String list(){
        String url = "friend/list?userId=" + getLoginService().getUserId();
        ResponseBean responseBean = HttpService.get(getHeasyContext(), url);
        if (responseBean.getCode() == ResponseCode.SUCCESS.code()) {
            return (String) responseBean.getData();
        }else{
            return "[]";
        }
    }

    public static String checkFriend(String phone){
        String url = "friend/check?userId=" + getLoginService().getUserId() + "&phone=" + phone;
        ResponseBean responseBean = HttpService.get(getHeasyContext(), url);
        if (responseBean.getCode() == ResponseCode.SUCCESS.code()) {
            JSONObject obj = FastjsonUtil.string2JSONObject((String)responseBean.getData());
            String result = FastjsonUtil.getString(obj, "result");
            return result;
        }else{
            return "";
        }
    }

    public static String invite(String phone){
        if(HeasyLocationService.getHeasyLocationClient() == null){
            return "无位置信息，不能邀请好友";
        }

        LocationBean locationBean = HeasyLocationService.getHeasyLocationClient().getCurrentLocation();
        if(locationBean == null){
            return "无位置信息，不能邀请好友";
        }

        String requestURL = "friend/invite";
        String data = FastjsonUtil.toJSONString("userId", String.valueOf(getLoginService().getUserId()),
                "phone", phone);

        ResponseBean responseBean = HttpService.postJson(getHeasyContext(), requestURL, data);
        if(responseBean.getCode() == ResponseCode.SUCCESS.code()){
            String mid = FastjsonUtil.getString(FastjsonUtil.string2JSONObject((String)responseBean.getData()), "id");

            //给好友发送邀请短信
            double longitude = locationBean.getLongitude();
            double latitude = locationBean.getLatitude();

            String message = "分享给您一个app，定位寻人用【知途】：" + HttpService.getApiRootAddress(ServiceEngineFactory.getServiceEngine().getHeasyContext()) + "invite?mid=" + mid;
            message += "&lng=" + longitude + "&lat=" + latitude;

            AndroidBuiltinService.sendSMS(phone, message);

            return Constants.SUCCESS;
        }else{
            return HttpService.getFailureMessage(responseBean);
        }
    }

    public static String add(String phone){
        UserBean userBean = UserAPI.getByPhone(phone);
        if(userBean == null){
            return "用户不存在";
        }

        String requestURL = "friend/add";
        String data = FastjsonUtil.toJSONString("userId", String.valueOf(getLoginService().getUserId()),
                "friendUserId", String.valueOf(userBean.getId()), "phone", phone);

        ResponseBean responseBean = HttpService.postJson(getHeasyContext(), requestURL, data);
        if(responseBean.getCode() == ResponseCode.SUCCESS.code()){
            return Constants.SUCCESS;
        }else{
            return HttpService.getFailureMessage(responseBean);
        }
    }

    public static String confirmAdd(String id, String pass){
        String requestURL = "friend/confirmAdd";
        String data = FastjsonUtil.toJSONString("id", id, "pass", pass);

        ResponseBean responseBean = HttpService.postJson(getHeasyContext(), requestURL, data);
        if(responseBean.getCode() == ResponseCode.SUCCESS.code()){
            return Constants.SUCCESS;
        }else{
            return HttpService.getFailureMessage(responseBean);
        }
    }

    public static String delete(String id){
        String requestURL = "friend/delete/" + id;
        ResponseBean responseBean = HttpService.postJson(getHeasyContext(), requestURL, "");
        if(responseBean.getCode() == ResponseCode.SUCCESS.code()){
            return Constants.SUCCESS;
        }else{
            return HttpService.getFailureMessage(responseBean);
        }
    }

    public static String updateNickname(String id, String newNickname){
        String requestURL = "friend/updateNickname";
        String data = FastjsonUtil.toJSONString("id", id, "newNickname", newNickname);
        ResponseBean responseBean = HttpService.postJson(getHeasyContext(), requestURL, data);
        if(responseBean.getCode() == ResponseCode.SUCCESS.code()){
            return Constants.SUCCESS;
        }else{
            return HttpService.getFailureMessage(responseBean);
        }
    }

}
