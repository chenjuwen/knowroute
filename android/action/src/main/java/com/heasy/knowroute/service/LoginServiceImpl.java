package com.heasy.knowroute.service;

import android.content.Context;
import android.support.annotation.NonNull;

import com.heasy.knowroute.action.ResponseBean;
import com.heasy.knowroute.action.ResponseCode;
import com.heasy.knowroute.bean.UserBean;
import com.heasy.knowroute.core.Constants;
import com.heasy.knowroute.core.service.AbstractService;
import com.heasy.knowroute.core.utils.FileUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by Administrator on 2020/9/26.
 */
public class LoginServiceImpl extends AbstractService implements LoginService {
    private static final Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);
    private String userRole = "";

    @Override
    public void init() {
        try {
            //read authority file
            String accessFilePath = getAccessFilePath();
            userRole = FileUtil.readTextFile(accessFilePath);

            successInit = true;
        }catch(Exception ex){
            logger.error("", ex);
        }
    }

    @Override
    public void unInit() {
        super.unInit();
        userRole = "";
    }

    @Override
    public String checkLogin(String account, String password) {
        try {
            userRole = "";

            String requestUrl = "/login/checkLogin?account=" + account + "&password=" + password;
            ResponseBean responseBean = HttpService.httpGet(getHeasyContext(), requestUrl);

            if(responseBean.getCode() == ResponseCode.SUCCESS.code()){
                UserService userService = getHeasyContext().getServiceEngine().getService(UserServiceImpl.class);
                UserBean userBean = userService.getUser(account);
                if(userBean != null){
                    userRole = userBean.getRole();

                    //write authority file
                    String accessFilePath = getAccessFilePath();
                    FileUtil.writeFile(userRole, accessFilePath);

                    return Constants.SUCCESS;
                }else{
                    throw new RuntimeException();
                }
            }else{
                return HttpService.getFailureMessage(responseBean);
            }
        }catch(Exception ex){
            logger.error("", ex);
            return "登录出错！";
        }
    }

    @Override
    public boolean isAdministrator() {
        return "admin".equalsIgnoreCase(userRole);
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
