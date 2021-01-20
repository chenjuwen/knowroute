package com.heasy.knowroute.service;

import android.content.Context;
import android.support.annotation.NonNull;

import com.heasy.knowroute.bean.LoginResultBean;
import com.heasy.knowroute.core.Constants;
import com.heasy.knowroute.core.service.AbstractService;
import com.heasy.knowroute.core.utils.FileUtil;
import com.heasy.knowroute.core.utils.StringUtil;
import com.heasy.knowroute.service.backend.UserAPI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by Administrator on 2020/9/26.
 */
public class LoginServiceImpl extends AbstractService implements LoginService {
    private static final Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);

    private int userId = 0;
    private String userPhone = "";
    private String userNickname = "";

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
                this.userNickname = arr[2];
                logger.debug("userId=" + this.userId + ", userPhone=" + this.userPhone + ", userNickname=" + userNickname);
            }

            successInit = true;
        }catch(Exception ex){
            logger.error("", ex);
        }
    }

    @Override
    public void unInit() {
        super.unInit();
        userId = 0;
        userPhone = "";
        userNickname = "";
    }

    @Override
    public boolean getCaptcha(String phone) {
        try {
            return UserAPI.getCaptcha(phone);
        }catch(Exception ex){
            logger.error("", ex);
            return false;
        }
    }

    @Override
    public String doLogin(String phone, String captcha) {
        try {
            LoginResultBean loginResultBean = UserAPI.login(phone, captcha);
            if(StringUtil.isEmpty(loginResultBean.getErrorMessage())){
                this.userId = loginResultBean.getUserId();
                this.userPhone = phone;
                this.userNickname = loginResultBean.getNickname();

                addAuthorityFile();

                return Constants.SUCCESS;
            }else{
                this.userId = 0;
                this.userPhone = "";
                this.userNickname = "";
                return loginResultBean.getErrorMessage();
            }
        }catch(Exception ex){
            logger.error("", ex);
            return "登录出错！";
        }
    }

    private void addAuthorityFile() {
        String accessFilePath = getAccessFilePath();
        FileUtil.writeFile(this.userId + "," + this.userPhone + "," + this.userNickname, accessFilePath);
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
    public String getNickname() {
        return this.userNickname;
    }

    @Override
    public void setNickname(String nickname) {
        this.userNickname = nickname;
        addAuthorityFile();
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
