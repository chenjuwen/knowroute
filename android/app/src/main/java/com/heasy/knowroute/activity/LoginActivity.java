package com.heasy.knowroute.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.heasy.knowroute.R;
import com.heasy.knowroute.core.utils.AndroidUtil;
import com.heasy.knowroute.core.utils.StringUtil;
import com.heasy.knowroute.service.CommonService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginActivity extends BaseActivity implements View.OnClickListener{
    private static final Logger logger = LoggerFactory.getLogger(StartActivity.class);

    private EditText phone;
    private EditText captcha;
    private Button btnGetCaptcha;
    private Button btnLogin;
    private TextView tvAgreement;
    private TextView tvPolicy;

    private ProgressDialog progressDialog;

    private String loginCaptcha = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideStatusBar();
        setContentView(R.layout.activity_login);
        hideActionBar();

        initComponent();
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
                getCaptche();
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
        String s_phone = StringUtil.trimToEmpty(phone.getText().toString());
        if(s_phone.length() != 11){
            AndroidUtil.showToast(getApplicationContext(), "请输入11位手机号码");
            return;
        }

        if(StringUtil.isEmpty(loginCaptcha)){
            AndroidUtil.showToast(getApplicationContext(), "请先获取验证码");
            return;
        }

        String s_captcha = StringUtil.trimToEmpty(captcha.getText().toString());
        if(s_captcha.length() <= 0){
            AndroidUtil.showToast(getApplicationContext(), "请输入验证码");
            return;
        }

        if(!loginCaptcha.equalsIgnoreCase(s_captcha)){
            AndroidUtil.showToast(getApplicationContext(), "验证码错误");
            return;
        }
    }

    /**
     * 获取验证码
     */
    private void getCaptche(){
        String s_phone = StringUtil.trimToEmpty(phone.getText().toString());
        if(s_phone.length() != 11){
            AndroidUtil.showToast(getApplicationContext(), "请输入11位手机号码");
            return;
        }

        try {
            progressDialog = AndroidUtil.showLoadingDialog(this, "正在获取验证码");

            loginCaptcha = StringUtil.getFourDigitRandomNumber(); //四位随机码

            //发送短信
            String message = "【知途】验证码" + loginCaptcha + "，您正在登录，若非本人操作，请勿泄露。";
            boolean b = CommonService.sendSMS(s_phone, message);

            if(!b){
                progressDialog.dismiss();
                AndroidUtil.showToast(getApplicationContext(), "获取验证码失败");
                return;
            }

            progressDialog.dismiss();
            AndroidUtil.showToast(getApplicationContext(), "获取验证码成功");

            final String oldText = btnGetCaptcha.getText().toString();
            int seconds = 60;

            //倒计时
            new CountDownTimer(seconds * 1000 + 1050, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    btnGetCaptcha.setEnabled(false);
                    btnGetCaptcha.setText((millisUntilFinished / 1000 - 1) + "秒后重试"); //剩余时间
                }

                @Override
                public void onFinish() {
                    btnGetCaptcha.setEnabled(true);
                    btnGetCaptcha.setText(oldText);

                }
            }.start();

        }catch (Exception ex){
            logger.error("", ex);
            progressDialog.dismiss();
            AndroidUtil.showToast(getApplicationContext(), "获取验证码失败");
        }
    }
}
