package com.heasy.knowroute.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.heasy.knowroute.R;
import com.heasy.knowroute.core.Constants;
import com.heasy.knowroute.core.DefaultDaemonThread;
import com.heasy.knowroute.core.service.ServiceEngineFactory;
import com.heasy.knowroute.core.utils.AndroidUtil;
import com.heasy.knowroute.core.utils.StringUtil;
import com.heasy.knowroute.service.LoginService;
import com.heasy.knowroute.service.LoginServiceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginActivity extends BaseActivity implements View.OnClickListener{
    private static final Logger logger = LoggerFactory.getLogger(LoginActivity.class);

    private EditText phone;
    private EditText captcha;
    private Button btnGetCaptcha;
    private Button btnLogin;
    private TextView tvAgreement;
    private TextView tvPolicy;

    private Dialog loadingDialog;

    private boolean captchaObtained = false;
    private String mobilephone = "";
    private Handler handler = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideStatusBar();
        setContentView(R.layout.activity_login);
        hideActionBar();

        handler = new Handler(){
            @Override
            public void handleMessage(Message message) {
                int type = message.what;
                String result = (String)message.obj;
                logger.debug("result=" + result);

                loadingDialog.dismiss();

                if(type == 1){ //获取验证码
                    if(!"true".equalsIgnoreCase(result)){
                        AndroidUtil.showToast(getApplicationContext(), "获取验证码失败");
                        return;
                    }
                    captchaObtained = true;
                    handleCountDownTimer();

                }else if(type == 2){ //登录
                    if(Constants.SUCCESS.equalsIgnoreCase(result)){
                        captchaObtained = false;
                        logger.debug("start MainActivity...");
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        AndroidUtil.showToast(getApplicationContext(), result);
                    }
                }
            }
        };

        initComponent();
    }

    private void handleCountDownTimer() {
        final String oldText = btnGetCaptcha.getText().toString();
        int seconds = 60;

        //倒计时
        new CountDownTimer(seconds * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                btnGetCaptcha.setEnabled(false);
                btnGetCaptcha.setText((millisUntilFinished / 1000) + "秒后重试"); //剩余时间
            }

            @Override
            public void onFinish() {
                btnGetCaptcha.setEnabled(true);
                btnGetCaptcha.setText(oldText);
            }
        }.start();
    }

    private void initComponent(){
        phone = (EditText)findViewById(R.id.phone);
        captcha = (EditText)findViewById(R.id.captcha);

        btnGetCaptcha = (Button)findViewById(R.id.btnGetCaptcha);
        btnGetCaptcha.setOnClickListener(this);

        btnLogin = (Button)findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);

        tvAgreement = (TextView)findViewById(R.id.tvAgreement);
        tvAgreement.setOnClickListener(this);

        tvPolicy = (TextView)findViewById(R.id.tvPolicy);
        tvPolicy.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnGetCaptcha:
                getCaptcha();
                break;
            case R.id.btnLogin:
                doLogin();
                break;
            case R.id.tvAgreement:
                break;
            case R.id.tvPolicy:
                break;
        }
    }

    private void doLogin(){
        mobilephone = StringUtil.trimToEmpty(phone.getText().toString());
        if(mobilephone.length() != 11){
            AndroidUtil.showToast(getApplicationContext(), "请输入11位手机号码");
            return;
        }

        if(!captchaObtained){
            AndroidUtil.showToast(getApplicationContext(), "请获取验证码");
            return;
        }

        final String s_captcha = StringUtil.trimToEmpty(captcha.getText().toString());
        if(s_captcha.length() <= 0){
            AndroidUtil.showToast(getApplicationContext(), "请输入验证码");
            return;
        }

        loadingDialog = AndroidUtil.showLoadingDialog(this, "登录中...");

        //登录处理
        new DefaultDaemonThread(){
            @Override
            public void run() {
                logger.debug("start login...");
                LoginService loginService = ServiceEngineFactory.getServiceEngine().getService(LoginServiceImpl.class);
                String result = loginService.doLogin(mobilephone, s_captcha);
                logger.debug("result=" + result);

                Message message = new Message();
                message.what = 2;
                message.obj = result;
                handler.sendMessage(message);
            }
        }.start();
    }

    /**
     * 获取验证码
     */
    private void getCaptcha(){
        mobilephone = StringUtil.trimToEmpty(phone.getText().toString());
        if(mobilephone.length() != 11){
            AndroidUtil.showToast(getApplicationContext(), "请输入11位手机号码");
            return;
        }

        loadingDialog = AndroidUtil.showLoadingDialog(this, "获取中...");

        //获取验证码
        new DefaultDaemonThread() {
            @Override
            public void run() {
                LoginService loginService = ServiceEngineFactory.getServiceEngine().getService(LoginServiceImpl.class);
                boolean b = loginService.getCaptcha(mobilephone);

                Message message = new Message();
                message.what = 1;
                message.obj = String.valueOf(b);
                handler.sendMessage(message);
            }
        }.start();
    }

}
