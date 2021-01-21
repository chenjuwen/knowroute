package com.heasy.knowroute.service;

import android.content.Context;

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
    private String token = "";

    @Override
    public void init() {
        try {
            //read authority file
            String authorityContent = FileUtil.readTextFile(getAuthorityFilePath());
            if(StringUtil.isNotEmpty(authorityContent)) {
                String[] arr = authorityContent.split(",");
                this.userId = Integer.parseInt(arr[0]);
                this.userPhone = arr[1];
                this.userNickname = arr[2];
                logger.debug("userId=" + this.userId + ", userPhone=" + this.userPhone + ", userNickname=" + userNickname);
            }

            //read token file
            String tokenContent = FileUtil.readTextFile(getTokenFilePath());
            if(StringUtil.isNotEmpty(tokenContent)) {
                this.token = tokenContent;
                logger.debug("token=" + this.token);
            }

            successInit = true;
        }catch(Exception ex){
            logger.error("", ex);
        }
    }

    @Override
    public void unInit() {
        super.unInit();
        reset();
    }

    private void reset() {
        userId = 0;
        userPhone = "";
        userNickname = "";
        token = "";
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
                this.token = loginResultBean.getToken();

                addAuthorityFile();
                addTokenFile();

                return Constants.SUCCESS;
            }else{
                reset();
                return loginResultBean.getErrorMessage();
            }
        }catch(Exception ex){
            logger.error("", ex);
            return "登录出错！";
        }
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
    public String getToken() {
        return this.token;
    }

    @Override
    public boolean isLogin() {
        return this.userId > 0;
    }

    @Override
    public boolean cleanCache() {
        try {
            reset();

            //delete authority file
            FileUtil.deleteFile(getAuthorityFilePath());

            //delete token file
            FileUtil.deleteFile(getTokenFilePath());

        }catch(Exception ex){
            logger.error("", ex);
        }
        return true;
    }

    /**
     * 获取登录授权信息
     */
    private String getAuthorityFilePath() {
        String filePath = getHeasyContext().getServiceEngine().getAndroidContext()
                .getDir(Constants.AUTH_DIR, Context.MODE_PRIVATE).getPath();
        filePath += File.separator + Constants.AUTHORITY_FILE_NAME;
        return filePath;
    }

    private void addAuthorityFile() {
        String filePath = getAuthorityFilePath();
        FileUtil.writeFile(this.userId + "," + this.userPhone + "," + this.userNickname, filePath);
    }

    /**
     * 获取token信息
     */
    private String getTokenFilePath(){
        String filePath = getHeasyContext().getServiceEngine().getAndroidContext()
                .getDir(Constants.AUTH_DIR, Context.MODE_PRIVATE).getPath();
        filePath += File.separator + Constants.TOKEN_FILE_NAME;
        return filePath;
    }

    private void addTokenFile(){
        String filePath = getTokenFilePath();
        FileUtil.writeFile(this.token, filePath);
    }

}
