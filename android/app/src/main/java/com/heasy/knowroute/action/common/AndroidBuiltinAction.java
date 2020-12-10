package com.heasy.knowroute.action.common;

import android.app.ProgressDialog;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.heasy.knowroute.HeasyApplication;
import com.heasy.knowroute.core.Constants;
import com.heasy.knowroute.core.DefaultDaemonThread;
import com.heasy.knowroute.core.HeasyContext;
import com.heasy.knowroute.core.annotation.JSActionAnnotation;
import com.heasy.knowroute.core.utils.AndroidUtil;
import com.heasy.knowroute.core.utils.FastjsonUtil;
import com.heasy.knowroute.core.webview.Action;
import com.heasy.knowroute.service.AndroidBuiltinService;

import java.util.concurrent.TimeUnit;

/**
 * Android自带组件
 */
@JSActionAnnotation(name = "AndroidBuiltin")
public class AndroidBuiltinAction implements Action {
    private ProgressDialog progressDialog;
    private DefaultDaemonThread dismissProgressThread;

    @Override
    public String execute(HeasyContext heasyContext, String jsonData, String extend) {
        JSONObject jsonObject = FastjsonUtil.string2JSONObject(jsonData);

        if("getContactInfo".equalsIgnoreCase(extend)){
            AndroidBuiltinService.getContactInfo(heasyContext);
        }else if("showToast".equalsIgnoreCase(extend)){
            String message = FastjsonUtil.getString(jsonObject, "message");
            String duration = FastjsonUtil.getString(jsonObject, "duration");

            if("1".equalsIgnoreCase(duration)) {
                AndroidUtil.showToast(heasyContext.getServiceEngine().getAndroidContext(), message, Toast.LENGTH_LONG);
            }else{
                AndroidUtil.showToast(heasyContext.getServiceEngine().getAndroidContext(), message);
            }

        }else if("showProgress".equalsIgnoreCase(extend)){
            String message = FastjsonUtil.getString(jsonObject, "message");

            dismissProgress();
            interruptThread();

            HeasyApplication heasyApplication = (HeasyApplication)heasyContext.getServiceEngine().getAndroidContext();
            progressDialog = AndroidUtil.showLoadingDialog(heasyApplication.getMainActivity(), message);

            dismissProgressThread = new DefaultDaemonThread(){
                @Override
                public void run() {
                    try {
                        TimeUnit.SECONDS.sleep(20);
                        dismissProgress();
                    } catch (Exception e) {

                    }
                }
            };
            dismissProgressThread.start();

        }else if("dismissProgress".equalsIgnoreCase(extend)){
            dismissProgress();
        }else if("sleep".equalsIgnoreCase(extend)){
            try {
                String milliSeconds = FastjsonUtil.getString(jsonObject, "milliSeconds");
                TimeUnit.MILLISECONDS.sleep(Integer.parseInt(milliSeconds));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return Constants.SUCCESS;
    }

    private void dismissProgress(){
        if(progressDialog != null){
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    private void interruptThread(){
        if(dismissProgressThread != null){
            try{
                dismissProgressThread.interrupt();
                dismissProgressThread = null;
            }catch (Exception ex){

            }
        }
    }

}
