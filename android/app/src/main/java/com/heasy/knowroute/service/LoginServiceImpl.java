package com.heasy.knowroute.service;

import android.content.Context;
import android.support.annotation.NonNull;

import com.alibaba.fastjson.JSONObject;
import com.heasy.knowroute.action.ResponseBean;
import com.heasy.knowroute.action.ResponseCode;
import com.heasy.knowroute.core.Constants;
import com.heasy.knowroute.core.service.AbstractService;
import com.heasy.knowroute.core.utils.FastjsonUtil;
import com.heasy.knowroute.core.utils.FileUtil;
import com.heasy.knowroute.core.utils.ParameterUtil;
import com.heasy.knowroute.core.utils.StringUtil;
import com.heasy.knowroute.okhttp.RequestBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;

import okhttp3.Request;

/**
 * Created by Administrator on 2020/9/26.
 */
public class LoginServiceImpl extends AbstractService implements LoginService {
    private static final Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);
    private String userPhone = "";
    private int userId = 0;

    @Override
    public void init() {
        try {
            //read authority file
            String accessFilePath = getAccessFilePath();
            String text = FileUtil.readTextFile(accessFilePath);
            if(StringUtil.isNotEmpty(text)) {
                String[] arr = text.split(",");
                this.userId = Integer.parseInt(arr[0]);
                this.userPhone = arr[1];
                logger.debug("userId=" + this.userId + ", userPhone=" + this.userPhone);
            }

            successInit = true;
        }catch(Exception ex){
            logger.error("", ex);
        }
    }

    @Override
    public void unInit() {
        super.unInit();
        userPhone = "";
        userId = 0;
    }

    @Override
    public String getCaptche(String phone) {
        try {
            String requestUrl = "user/getCaptche?phone=" + phone;

            ResponseBean responseBean = HttpService.httpGet(getHeasyContext(), requestUrl);
            if (responseBean.getCode() == ResponseCode.SUCCESS.code()) {
                JSONObject obj = FastjsonUtil.string2JSONObject((String)responseBean.getData());
                return FastjsonUtil.getString(obj, "captche");
            }else{
                logger.error(HttpService.getFailureMessage(responseBean));
            }
        }catch(Exception ex){
            logger.error("", ex);
        }
        return "";
    }

    @Override
    public String doLogin(String phone, String captche) {
        try {
            Map<String, String> params = ParameterUtil.toParamMap("phone", phone, "captche", captche);
            ResponseBean responseBean = HttpService.httpPost(HttpService.getApiRootAddress(getHeasyContext()) + "user/login", params);

            if(responseBean.getCode() == ResponseCode.SUCCESS.code()){
                this.userPhone = phone;
                this.userId = FastjsonUtil.string2JSONObject((String)responseBean.getData()).getIntValue("id");

                addAuthorityFile();

                return Constants.SUCCESS;
            }else{
                return HttpService.getFailureMessage(responseBean);
            }
        }catch(Exception ex){
            logger.error("", ex);
            return "登录出错！";
        }
    }

    private void addAuthorityFile() {
        String accessFilePath = getAccessFilePath();
        FileUtil.writeFile(this.userId + "," + this.userPhone, accessFilePath);
    }

    @Override
    public int getUserId() {
        return this.userId;
    }

    @Override
    public String getPhone() {
        return this.userPhone;
    }

    @Override
    public boolean isLogin() {
        return this.userId > 0;
    }

    @Override
    public boolean cleanCache() {
        try {
            //delete authority file
            String accessFilePath = getAccessFilePath();
            FileUtil.deleteFile(accessFilePath);
        }catch(Exception ex){
            logger.error("", ex);
        }
        return true;
    }

    /**
     * 获取登录授权信息
     */
    @NonNull
    private String getAccessFilePath() {
        String accessFilePath = getHeasyContext().getServiceEngine().getAndroidContext()
                .getDir(Constants.AUTH_DIR, Context.MODE_PRIVATE).getPath();
        accessFilePath += File.separator + Constants.AUTH_DATA_FILE_NAME;
        return accessFilePath;
    }
}
