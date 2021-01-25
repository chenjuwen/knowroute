package com.heasy.knowroute.service;

import android.content.Context;

import com.heasy.knowroute.bean.LoginResultBean;
import com.heasy.knowroute.core.Constants;
import com.heasy.knowroute.core.service.AbstractService;
import com.heasy.knowroute.core.utils.FastjsonUtil;
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

    private LoginResultBean loginResult;

    @Override
    public void init() {
        try {
            //read authority file
            String authorityContent = FileUtil.readTextFile(getAuthorityFilePath());
            if(StringUtil.isNotEmpty(authorityContent)) {
                logger.debug(authorityContent);
                this.loginResult = FastjsonUtil.string2JavaBean(authorityContent, LoginResultBean.class);
            }else{
                this.loginResult = new LoginResultBean();
            }
            successInit = true;
        }catch(Exception ex){
            logger.error("", ex);
        }
    }

    @Override
    public void unInit() {
        super.unInit();
        this.loginResult = null;
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
                loginResult = loginResultBean;
                addAuthorityFile();
                return Constants.SUCCESS;
            }else{
                loginResult = loginResultBean;
                return loginResultBean.getErrorMessage();
            }
        }catch(Exception ex){
            logger.error("", ex);
            return "登录出错！";
        }
    }

    @Override
    public int getUserId() {
        return this.loginResult.getUserId();
    }

    @Override
    public String getPhone() {
        return StringUtil.trimToEmpty(this.loginResult.getPhone());
    }

    @Override
    public String getNickname() {
        return StringUtil.trimToEmpty(this.loginResult.getNickname());
    }

    @Override
    public void setNickname(String nickname) {
        this.loginResult.setNickname(nickname);
        addAuthorityFile();
    }

    @Override
    public String getToken() {
        return StringUtil.trimToEmpty(this.loginResult.getToken());
    }

    @Override
    public boolean isLogin() {
        return this.loginResult.getUserId() > 0;
    }

    @Override
    public boolean cleanCache() {
        try {
            this.loginResult = new LoginResultBean();

            //delete authority file
            FileUtil.deleteFile(getAuthorityFilePath());
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
        String data = FastjsonUtil.object2String(this.loginResult);
        FileUtil.writeFile(data, filePath);
    }

}
