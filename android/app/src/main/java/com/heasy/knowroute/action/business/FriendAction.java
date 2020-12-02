package com.heasy.knowroute.action.business;

import com.alibaba.fastjson.JSONObject;
import com.heasy.knowroute.ServiceEngineFactory;
import com.heasy.knowroute.action.AbstractAction;
import com.heasy.knowroute.action.ResponseBean;
import com.heasy.knowroute.action.ResponseCode;
import com.heasy.knowroute.bean.UserBean;
import com.heasy.knowroute.common.EnumConstants;
import com.heasy.knowroute.core.Constants;
import com.heasy.knowroute.core.HeasyContext;
import com.heasy.knowroute.core.annotation.JSActionAnnotation;
import com.heasy.knowroute.core.utils.FastjsonUtil;
import com.heasy.knowroute.map.HeasyLocationService;
import com.heasy.knowroute.map.bean.LocationBean;
import com.heasy.knowroute.service.AndroidBuiltinService;
import com.heasy.knowroute.service.HttpService;
import com.heasy.knowroute.service.LoginService;
import com.heasy.knowroute.service.LoginServiceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Administrator on 2020/10/23.
 */
@JSActionAnnotation(name = "FriendAction")
public class FriendAction extends AbstractAction {
    private static final Logger logger = LoggerFactory.getLogger(FriendAction.class);

    @Override
    public String execute(HeasyContext heasyContext, String jsonData, String extend) {
        JSONObject jsonObject = FastjsonUtil.string2JSONObject(jsonData);
        LoginService loginService = heasyContext.getServiceEngine().getService(LoginServiceImpl.class);

        if("getFriendList".equalsIgnoreCase(extend)){
            try {
                String url = "friend/list?userId=" + loginService.getUserId();
                ResponseBean responseBean = HttpService.httpGet(heasyContext, url);
                if (responseBean.getCode() == ResponseCode.SUCCESS.code()) {
                    return (String) responseBean.getData();
                }
            }catch (Exception ex){
                logger.error("", ex);
            }
            return "[]";

        }else if ("checkFriend".equalsIgnoreCase(extend)){
            try {
                String phone = FastjsonUtil.getString(jsonObject, "phone");
                String url = "friend/check?userId=" + loginService.getUserId() + "&phone=" + phone;

                ResponseBean responseBean = HttpService.httpGet(heasyContext, url);
                if (responseBean.getCode() == ResponseCode.SUCCESS.code()) {
                    JSONObject obj = FastjsonUtil.string2JSONObject((String)responseBean.getData());
                    String result = FastjsonUtil.getString(obj, "result");
                    return result;
                }
            }catch (Exception ex){
                logger.error("", ex);
            }
            return "";

        }else if("invite".equalsIgnoreCase(extend)){
            try {
                String phone = FastjsonUtil.getString(jsonObject, "phone");

                if(HeasyLocationService.getHeasyLocationClient() != null){
                    LocationBean locationBean = HeasyLocationService.getHeasyLocationClient().getCurrentLocation();
                    if(locationBean != null){
                        //发送邀请好友的系统消息
                        String requestUrl = "message/insert";
                        String bodyData = FastjsonUtil.toJSONString(
                                "content", "请求添加您为好友",
                                "category", EnumConstants.MessageCategory.INVITE_FRIEND.name(),
                                "sender", String.valueOf(loginService.getUserId()),
                                "receiver", phone,
                                "status", "0");

                        ResponseBean responseBean = HttpService.httpPost(ServiceEngineFactory.getServiceEngine().getHeasyContext(), requestUrl, bodyData);
                        if(responseBean.getCode() == ResponseCode.SUCCESS.code()){
                            String mid = FastjsonUtil.getString(FastjsonUtil.string2JSONObject((String)responseBean.getData()), "id");

                            //给好友发送邀请短信
                            double longitude = locationBean.getLongitude();
                            double latitude = locationBean.getLatitude();

                            String message = "分享给您一个app，定位寻人用【知途】：http://www.knowroute.cn/knowroute/invite?mid=" + mid;
                            message += "&lng=" + longitude + "&lat=" + latitude;

                            AndroidBuiltinService.sendSMS(phone, message);

                            return Constants.SUCCESS;
                        }else{
                            return HttpService.getFailureMessage(responseBean);
                        }
                    }
                }

            }catch (Exception ex){
                logger.error("", ex);
            }
            return "邀请失败";

        }else if("add".equalsIgnoreCase(extend)){
            String phone = FastjsonUtil.getString(jsonObject, "phone");

            int friendUserId = getUserId(phone);
            if(friendUserId == 0){
                return "用户不存在";
            }

            //发送添加好友的系统消息
            String requestUrl = "message/insert";
            String bodyData = FastjsonUtil.toJSONString(
                    "content", "请求添加您为好友",
                    "category", EnumConstants.MessageCategory.ADD_FRIEND.name(),
                    "sender", String.valueOf(loginService.getUserId()),
                    "receiver", phone,
                    "owner",String.valueOf(friendUserId),
                    "status", "0");

            ResponseBean responseBean = HttpService.httpPost(ServiceEngineFactory.getServiceEngine().getHeasyContext(), requestUrl, bodyData);
            if(responseBean.getCode() == ResponseCode.SUCCESS.code()){
                return Constants.SUCCESS;
            }else{
                return HttpService.getFailureMessage(responseBean);
            }
        }

        return Constants.SUCCESS;
    }

    private int getUserId(String phone){
        String requestUrl = "user/getByPhone?phone=" + phone;
        ResponseBean responseBean = HttpService.httpGet(ServiceEngineFactory.getServiceEngine().getHeasyContext(), requestUrl);
        if(responseBean.getCode() == ResponseCode.SUCCESS.code()) {
            UserBean userBean = FastjsonUtil.string2JavaBean((String) responseBean.getData(), UserBean.class);
            return userBean.getId();
        }else{
            return 0;
        }
    }

}
