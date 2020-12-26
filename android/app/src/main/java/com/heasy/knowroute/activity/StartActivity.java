package com.heasy.knowroute.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.heasy.knowroute.R;
import com.heasy.knowroute.ServiceEngineImpl;
import com.heasy.knowroute.WebViewWrapperFactory;
import com.heasy.knowroute.core.HeasyContext;
import com.heasy.knowroute.core.service.ServiceEngineFactory;
import com.heasy.knowroute.core.utils.VersionUtil;
import com.heasy.knowroute.service.LoginService;
import com.heasy.knowroute.service.LoginServiceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class StartActivity extends BaseActivity {
    private static final Logger logger = LoggerFactory.getLogger(StartActivity.class);
    private final int SDK_PERMISSION_REQUEST = 127;
    private Thread thread = null;
    private Handler handler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideStatusBar();
        setContentView(R.layout.activity_start);
        hideActionBar();

        TextView textView = (TextView) findViewById(R.id.versionRelease);
        textView.setText("版本：" + VersionUtil.getVersionName(getApplicationContext()));

        logger.debug(String.valueOf(Build.VERSION.SDK_INT));

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

        checkAndRequestPermission();

        thread = new MainThread();
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * 运行时权限控制，在Anddroid6.0版本开始支持
     */
    @TargetApi(23)
    private void checkAndRequestPermission(){
        if (Build.VERSION.SDK_INT >= 23) { //Android6.0以上
            ArrayList<String> permissions = new ArrayList<String>();

            if(checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.INTERNET);
            }
            if(checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.ACCESS_NETWORK_STATE);
            }
            if(checkSelfPermission(Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.ACCESS_WIFI_STATE);
            }
            if(checkSelfPermission(Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.CHANGE_WIFI_STATE);
            }

            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if(checkSelfPermission(Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS);
            }

            if(checkSelfPermission(Manifest.permission.SYSTEM_ALERT_WINDOW) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.SYSTEM_ALERT_WINDOW);
            }

            if(checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.READ_PHONE_STATE);
            }
            if(checkSelfPermission(Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.FOREGROUND_SERVICE);
            }
            if(checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.SEND_SMS);
            }
            if(checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.READ_CONTACTS);
            }

            if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }

            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
            }
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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

                TimeUnit.MILLISECONDS.sleep(2000);

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
