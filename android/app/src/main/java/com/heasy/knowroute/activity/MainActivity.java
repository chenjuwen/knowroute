package com.heasy.knowroute.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.Toast;

import com.heasy.knowroute.WebViewWrapperFactory;
import com.heasy.knowroute.core.HeasyApplication;
import com.heasy.knowroute.core.HeasyContext;
import com.heasy.knowroute.core.event.ExitAppEvent;
import com.heasy.knowroute.core.service.ServiceEngineFactory;
import com.heasy.knowroute.core.webview.WebViewWrapper;
import com.heasy.knowroute.map.HeasyLocationService;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MainActivity extends AppCompatActivity{
    private static final Logger logger = LoggerFactory.getLogger(MainActivity.class);
    private WebViewWrapper webViewWrapper;
    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //activity设置属性： android:theme="@style/Theme.AppCompat.Light.NoActionBar"

        //隐藏android的标题栏
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);

        //隐藏activity的actionBar
        //getSupportActionBar().hide();

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
        //webViewWrapper.loadUrl("file:///sdcard/sdcard_file.html");
    }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ServiceEngineFactory.getServiceEngine().getEventService().unregister(this);

        _destroy();

        HeasyApplication heasyApplication = (HeasyApplication)getApplication();
        heasyApplication.finishActivity(this);
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

        _destroy();
        finish();
        System.exit(0);
    }

    private void _destroy(){
        if(webViewWrapper != null) {
            webViewWrapper.destroy();
        }

        ServiceEngineFactory.getServiceEngine().close();
    }

}
