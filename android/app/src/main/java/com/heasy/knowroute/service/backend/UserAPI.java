package com.heasy.knowroute.service.backend;

import com.heasy.knowroute.bean.LoginResultBean;
import com.heasy.knowroute.bean.ResponseBean;
import com.heasy.knowroute.bean.ResponseCode;
import com.heasy.knowroute.bean.SimpleUserBean;
import com.heasy.knowroute.bean.UserBean;
import com.heasy.knowroute.core.Constants;
import com.heasy.knowroute.core.utils.AESEncrpt;
import com.heasy.knowroute.core.utils.FastjsonUtil;
import com.heasy.knowroute.core.utils.ParameterUtil;
import com.heasy.knowroute.service.common.HttpService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class UserAPI extends BaseAPI {
    private static final Logger logger = LoggerFactory.getLogger(UserAPI.class);

    public static UserBean getById(int userId){
        String requestUrl = "user/getById?id=" + userId;
        ResponseBean responseBean = HttpService.get(getHeasyContext(), requestUrl);
        if(responseBean.getCode() == ResponseCode.SUCCESS.code()) {
            UserBean userBean = FastjsonUtil.string2JavaBean((String) responseBean.getData(), UserBean.class);
            return userBean;
        }else{
            return null;
        }
    }

    public static SimpleUserBean getByPhone(String phone){
        String requestUrl = "user/getByPhone?phone=" + phone;
        ResponseBean responseBean = HttpService.get(getHeasyContext(), requestUrl);
        if(responseBean.getCode() == ResponseCode.SUCCESS.code()) {
            SimpleUserBean userBean = FastjsonUtil.string2JavaBean((String) responseBean.getData(), SimpleUserBean.class);
            return userBean;
        }else{
            return null;
        }
    }

    public static String cancel(){
        String url = "user/cancel?id=" + getLoginService().getUserId();
        ResponseBean responseBean = HttpService.get(getHeasyContext(), url);
        if (responseBean.getCode() == ResponseCode.SUCCESS.code()) {
            return Constants.SUCCESS;
        }else{
            return "销户失败";
        }
    }

    public static String updateNickname(String newNickname){
        String requestURL = "user/updateNickname";
        String data = FastjsonUtil.toJSONString("userId", String.valueOf(getLoginService().getUserId()), "newNickname", newNickname);

        ResponseBean responseBean = HttpService.postJson(getHeasyContext(), requestURL, data);
        if(responseBean.getCode() == ResponseCode.SUCCESS.code()){
            getLoginService().setNickname(newNickname);
            return Constants.SUCCESS;
        }else{
            return HttpService.getFailureMessage(responseBean);
        }
    }

    public static boolean getCaptcha(String phone){
        String requestUrl = "user/getCaptcha?phone=" + phone;
        ResponseBean responseBean = HttpService.get(getHeasyContext(), requestUrl);
        if (responseBean.getCode() == ResponseCode.SUCCESS.code()) {
            return true;
        }else{
            logger.error(HttpService.getFailureMessage(responseBean));
            return false;
        }
    }

    public static LoginResultBean login(String phone, String captcha) {
        Map<String, String> params = ParameterUtil.toParamMap("phone", phone, "captcha", captcha);
        ResponseBean responseBean = HttpService.post(HttpService.getApiRootAddress(getHeasyContext()) + "user/login", params);
        if (responseBean.getCode() == ResponseCode.SUCCESS.code()) {
            String data = (String) responseBean.getData();
            LoginResultBean bean = FastjsonUtil.string2JavaBean(data, LoginResultBean.class);
            bean.setErrorMessage("");
            return bean;
        } else {
            LoginResultBean bean = new LoginResultBean();
            bean.setErrorMessage(HttpService.getFailureMessage(responseBean));
            return bean;
        }
    }

    public static LoginResultBean refreshToken(){
        String requestUrl = "user/refreshToken";
        ResponseBean responseBean = HttpService.get(getHeasyContext(), requestUrl, false);
        if (responseBean.getCode() == ResponseCode.SUCCESS.code()) {
            String data = (String) responseBean.getData();
            LoginResultBean bean = FastjsonUtil.string2JavaBean(data, LoginResultBean.class);
            bean.setErrorMessage("");
            return bean;
        }else{
            logger.error(HttpService.getFailureMessage(responseBean));
            return null;
        }
    }

}
