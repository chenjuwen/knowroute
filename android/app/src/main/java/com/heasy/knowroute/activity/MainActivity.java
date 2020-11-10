package com.heasy.knowroute.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.heasy.knowroute.HeasyApplication;
import com.heasy.knowroute.ServiceEngineFactory;
import com.heasy.knowroute.WebViewWrapperFactory;
import com.heasy.knowroute.core.HeasyContext;
import com.heasy.knowroute.core.event.ExitAppEvent;
import com.heasy.knowroute.core.webview.WebViewWrapper;
import com.heasy.knowroute.map.HeasyLocationService;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MainActivity extends BaseActivity{
    private static final Logger logger = LoggerFactory.getLogger(MainActivity.class);
    private WebViewWrapper webViewWrapper;
    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
            startService(serviceIntent);
        }
        logger.info("MainActivity Created");
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
        if(exitAppEvent != null) {
            logger.debug(exitAppEvent.getSource().getClass().getName());
        }

        //finish和按back键处理过程一样：onPause、onStop、onDestory
        //finish();
        finishAffinity();

        System.exit(0);
        logger.info("App exit");
    }

    /*
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(webViewWrapper.canGoBack()){
                webViewWrapper.goBack();
                return true;
            }else {
                if ((System.currentTimeMillis() - exitTime) > 2000) {
                    Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    exitTime = System.currentTimeMillis();
                } else {
                    exitApp(null);
                }
                return true; //此行代码很关键
            }
        }else{
            return super.onKeyDown(keyCode, event);
        }
    }
    */

}
