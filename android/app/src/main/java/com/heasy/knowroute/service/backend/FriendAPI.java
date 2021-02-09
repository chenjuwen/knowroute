package com.heasy.knowroute.service.backend;

import com.alibaba.fastjson.JSONObject;
import com.heasy.knowroute.bean.FriendBean;
import com.heasy.knowroute.bean.ResponseBean;
import com.heasy.knowroute.bean.ResponseCode;
import com.heasy.knowroute.bean.UserBean;
import com.heasy.knowroute.core.Constants;
import com.heasy.knowroute.core.service.ServiceEngineFactory;
import com.heasy.knowroute.core.utils.FastjsonUtil;
import com.heasy.knowroute.map.HeasyLocationService;
import com.heasy.knowroute.map.bean.LocationBean;
import com.heasy.knowroute.service.LoginService;
import com.heasy.knowroute.service.LoginServiceImpl;
import com.heasy.knowroute.service.common.AndroidBuiltinService;
import com.heasy.knowroute.service.common.HttpService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class FriendAPI extends BaseAPI {
    private static final Logger logger = LoggerFactory.getLogger(FriendAPI.class);
    private static List<FriendBean> friendList = new ArrayList<>();

    public static void cleanCache(){
        friendList.clear();
    }

    public static List<FriendBean> getFriendList(boolean removeFirstRecord){
        if(friendList.size() == 0){
            try {
                String jsonData = list();
                friendList = FastjsonUtil.arrayString2List(jsonData, FriendBean.class);
                if(removeFirstRecord && friendList.size() > 0){
                    friendList.remove(0);
                }
            }catch (Exception ex){
                logger.error("", ex);
            }
        }
        return friendList;
    }

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
            LoginService loginService = getHeasyContext().getServiceEngine().getService(LoginServiceImpl.class);
            String message = "【知途】" + loginService.getPhone() + " 请求添加您为好友，请登录知途APP了解详细信息。";
            AndroidBuiltinService.sendSMS(phone, message);

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

    public static String forbidLookTrace(String id, int traceFlag){
        String requestURL = "friend/forbidLookTrace/" + id + "/" + traceFlag;
        ResponseBean responseBean = HttpService.postJson(getHeasyContext(), requestURL, "");
        if(responseBean.getCode() == ResponseCode.SUCCESS.code()){
            return Constants.SUCCESS;
        }else{
            return HttpService.getFailureMessage(responseBean);
        }
    }

    /**
     * 是否禁止好友查看轨迹
     * viewTrackUserId想看userId的轨迹
     * @param userId 轨迹被看的用户
     * @param  viewTrackUserId 想看轨迹的用户
     */
    public static boolean checkForbid(int userId, int viewTrackUserId){
        String requestURL = "friend/checkForbid/" + userId + "/" + viewTrackUserId;
        ResponseBean responseBean = HttpService.get(getHeasyContext(), requestURL);
        if(responseBean.getCode() == ResponseCode.SUCCESS.code()){
            String data = (String)responseBean.getData();
            String forbid = FastjsonUtil.getString(FastjsonUtil.string2JSONObject(data), "forbid");
            if("true".equalsIgnoreCase(forbid)){
                return true;
            }
        }
        return false;
    }

}
