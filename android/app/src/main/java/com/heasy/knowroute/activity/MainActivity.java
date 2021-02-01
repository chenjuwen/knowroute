package com.heasy.knowroute.activity;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.heasy.knowroute.HeasyApplication;
import com.heasy.knowroute.WebViewWrapperFactory;
import com.heasy.knowroute.bean.VersionInfoBean;
import com.heasy.knowroute.core.DefaultDaemonThread;
import com.heasy.knowroute.core.HeasyContext;
import com.heasy.knowroute.core.event.ExitAppEvent;
import com.heasy.knowroute.core.service.ServiceEngineFactory;
import com.heasy.knowroute.core.utils.AndroidDownloadUtil;
import com.heasy.knowroute.core.utils.AndroidUtil;
import com.heasy.knowroute.core.utils.StringUtil;
import com.heasy.knowroute.core.webview.WebViewWrapper;
import com.heasy.knowroute.event.TokenEvent;
import com.heasy.knowroute.event.UpdateVersionEvent;
import com.heasy.knowroute.map.HeasyLocationService;
import com.heasy.knowroute.service.LoginService;
import com.heasy.knowroute.service.LoginServiceImpl;
import com.heasy.knowroute.service.VersionService;
import com.heasy.knowroute.service.VersionServiceImpl;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MainActivity extends BaseActivity{
    private static final Logger logger = LoggerFactory.getLogger(MainActivity.class);
    private WebViewWrapper webViewWrapper;
    private boolean versionChecked = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hideActionBar();

        //HeasyApplication
        HeasyApplication heasyApplication = (HeasyApplication)getApplication();
        heasyApplication.addActivity(this); //后面需要用到MainActivity

        ServiceEngineFactory.getServiceEngine().getEventService().register(this);

        initWebViewWrapper();

        setContentView(webViewWrapper.getWebView());

        //启动定位服务
        if(!serviceRunning()) {
            logger.info("start HeasyLocationService...");
            Intent serviceIntent = new Intent(MainActivity.this, HeasyLocationService.class);
            doStartService(serviceIntent);
        }

        logger.info("MainActivity Created");
    }

    private void doStartService(Intent serviceIntent){
        logger.debug("doStartService >> " + Build.VERSION.SDK_INT);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startServiceForSDK26(serviceIntent);
        }else{
            startService(serviceIntent);
        }
    }

    @TargetApi(26)
    private void startServiceForSDK26(Intent serviceIntent ){
        logger.debug("startForegroundService...");
        startForegroundService(serviceIntent);
    }

    /**
     * 查看服务是否正在运行
     */
    private boolean serviceRunning(){
        boolean result = false;
        ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> list = am.getRunningServices(100);
        if(list != null && list.size() > 0){
            for(ActivityManager.RunningServiceInfo info : list){
                String className = info.service.getClassName().toString();
                if(className.equalsIgnoreCase(HeasyLocationService.class.getName())){
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    private void initWebViewWrapper(){
        logger.info("init WebViewWrapper...");
        HeasyContext heasyContext = ServiceEngineFactory.getServiceEngine().getHeasyContext();
        WebViewWrapperFactory.build(heasyContext);

        //webViewWrapper
        webViewWrapper = WebViewWrapperFactory.getWebViewWrapper();

        String mainPage = ServiceEngineFactory.getServiceEngine().getConfigurationService().getConfigBean().getMainPage();
        webViewWrapper.loadUrlFromAsset(mainPage);
    }

    private void destroyWebViewWrapper(){
        if(webViewWrapper != null) {
            webViewWrapper.destroy();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if(HeasyLocationService.getHeasyLocationClient() != null){
            HeasyLocationService.getHeasyLocationClient().restart();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        logger.debug("MainActivity resume...");

        if(!versionChecked) {
            checkAndUpdateVersion();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ServiceEngineFactory.getServiceEngine().getEventService().unregister(this);

        destroyWebViewWrapper();

        HeasyApplication heasyApplication = (HeasyApplication)getApplication();
        heasyApplication.finishActivity(this);

        logger.info("MainActivity Destroy");
    }

    /**
     * 退出应用
     * @param exitAppEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void exitApp(ExitAppEvent exitAppEvent){
        //finish和按back键处理过程一样：onPause、onStop、onDestory
        //finish();

        HeasyApplication heasyApplication = (HeasyApplication)getApplication();
        heasyApplication.exit();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleTokenEvent(TokenEvent tokenEvent){
        if(tokenEvent != null){
            AndroidUtil.showToast(MainActivity.this, "Token有误，请重新登录！", Toast.LENGTH_LONG);

            //清除登录信息
            LoginService loginService = ServiceEngineFactory.getServiceEngine().getService(LoginServiceImpl.class);
            if(loginService != null){
                loginService.cleanCache();
            }

            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
    }

    private void checkAndUpdateVersion(){
        new DefaultDaemonThread(){
            @Override
            public void run() {
                try {
                    logger.debug("check and update version...");
                    VersionService versionService = ServiceEngineFactory.getServiceEngine().getService(VersionServiceImpl.class);
                    VersionInfoBean versionInfoBean = versionService.getVersionInfo();
                    if(StringUtil.isNotEmpty(versionInfoBean.getLastedVersionURL())) {
                        ServiceEngineFactory.getServiceEngine().getEventService()
                                .postEvent(new UpdateVersionEvent(this,
                                        versionInfoBean.getLastedVersion(), versionInfoBean.getLastedVersionURL()));
                    }else{
                        versionChecked = true;
                    }
                }catch (Exception ex){
                    logger.error("", ex);
                }
            }
        }.start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleUpdateVersionEvent(final UpdateVersionEvent event){
        if(event != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("版本更新");
            builder.setMessage("发现新版本 V" + event.getVersionName());
            builder.setCancelable(false);
            builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    versionChecked = true;
                    AndroidDownloadUtil.enqueue(MainActivity.this, event.getDownloadURL());
                }
            });
            builder.show();
        }
    }

}
