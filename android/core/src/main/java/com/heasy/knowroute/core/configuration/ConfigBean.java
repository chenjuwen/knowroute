package com.heasy.knowroute.core.configuration;

/**
 * Created by Administrator on 2018/11/10.
 */
public class ConfigBean {
    public static final String DEFAULT_CHARSET_UTF8 = "UTF-8";
    public static final String CONFIG_FILE_NAME = "config.xml";

    private String sdcardRootPath = "/sdcard/lottery/";
    private String actionBasePackage = "com.heasy.app.action";
    private String serviceBasePackage = "com.heasy.app";
    private String webviewLoadBasePath = "file:///android_asset/html/";

    //主页面
    private String mainPage = "main.html";

    //登陆页面
    private String loginPage = "login.html";

    private String apiAddress;

    public String getSdcardRootPath() {
        return sdcardRootPath;
    }

    public void setSdcardRootPath(String sdcardRootPath) {
        this.sdcardRootPath = sdcardRootPath;
    }

    public String getActionBasePackage() {
        return actionBasePackage;
    }

    public void setActionBasePackage(String actionBasePackage) {
        this.actionBasePackage = actionBasePackage;
    }

    public String getServiceBasePackage() {
        return serviceBasePackage;
    }

    public void setServiceBasePackage(String serviceBasePackage) {
        this.serviceBasePackage = serviceBasePackage;
    }

    public String getWebviewLoadBasePath() {
        return webviewLoadBasePath;
    }

    public void setWebviewLoadBasePath(String webviewLoadBasePath) {
        this.webviewLoadBasePath = webviewLoadBasePath;
    }

    public String getMainPage() {
        return mainPage;
    }

    public void setMainPage(String mainPage) {
        this.mainPage = mainPage;
    }

    public String getLoginPage() {
        return loginPage;
    }

    public void setLoginPage(String loginPage) {
        this.loginPage = loginPage;
    }

    public String getApiAddress() {
        return apiAddress;
    }

    public void setApiAddress(String apiAddress) {
        this.apiAddress = apiAddress;
    }
}
