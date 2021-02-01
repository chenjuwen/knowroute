package com.heasy.knowroute.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.heasy.knowroute.R;
import com.heasy.knowroute.ServiceEngineImpl;
import com.heasy.knowroute.WebViewWrapperFactory;
import com.heasy.knowroute.core.HeasyContext;
import com.heasy.knowroute.core.service.ServiceEngineFactory;
import com.heasy.knowroute.service.LoginService;
import com.heasy.knowroute.service.LoginServiceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class StartActivity extends BaseActivity {
    private static final Logger logger = LoggerFactory.getLogger(StartActivity.class);
    private Thread thread = null;
    private Handler handler = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideStatusBar();
        setContentView(R.layout.activity_start);
        hideActionBar();

        handler = new Handler(){
            @Override
            public void handleMessage(Message message) {
                if(message.what == 1) {
                    LoginService loginService = ServiceEngineFactory.getServiceEngine().getService(LoginServiceImpl.class);
                    if(loginService.isLogin()){
                        logger.debug("start MainActivity...");
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }else{
                        logger.debug("start LoginActivity...");
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                    }
                }

                finish();
            }
        };

        thread = new MainThread();
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(thread != null){
            thread.interrupt();
            thread = null;
        }
    }

    class MainThread extends Thread{
        @Override
        public void run() {
            try {
                initServiceEngine();
                initActionDispatcher();

                TimeUnit.MILLISECONDS.sleep(3000);

                handler.sendEmptyMessage(1);
            } catch (Exception ex) {
                logger.error("", ex);
            }
        }

        /**
         * 初始化服务引擎
         */
        private void initServiceEngine(){
            logger.debug("open ServiceEngine...");

            ServiceEngineFactory.setServiceEngine(new ServiceEngineImpl());

            ServiceEngineFactory.getServiceEngine().setAndroidContext(getApplicationContext());
            ServiceEngineFactory.getServiceEngine().setHeasyContext(new HeasyContext());
            ServiceEngineFactory.getServiceEngine().open();
        }

        /**
         * 扫描加载Action类
         */
        private void initActionDispatcher(){
            HeasyContext heasyContext = ServiceEngineFactory.getServiceEngine().getHeasyContext();
            String actionBasePackages = ServiceEngineFactory.getServiceEngine().getConfigurationService().getConfigBean().getActionBasePackage();
            WebViewWrapperFactory.initActionDispatcher(heasyContext, actionBasePackages);
        }
    }

}
