package com.heasy.knowroute.action.business;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.alibaba.fastjson.JSONObject;
import com.example.library.ActivityBackWrapper;
import com.example.library.RxActivity;
import com.heasy.knowroute.HeasyApplication;
import com.heasy.knowroute.activity.FriendListActivity;
import com.heasy.knowroute.bean.ResponseCode;
import com.heasy.knowroute.core.Constants;
import com.heasy.knowroute.core.HeasyContext;
import com.heasy.knowroute.core.annotation.JSActionAnnotation;
import com.heasy.knowroute.core.utils.FastjsonUtil;
import com.heasy.knowroute.core.webview.Action;
import com.heasy.knowroute.service.backend.ContactAPI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.functions.Action1;

@JSActionAnnotation(name = "ContactsAction")
public class ContactsAction implements Action {
    private static final Logger logger = LoggerFactory.getLogger(ContactsAction.class);

    @Override
    public String execute(HeasyContext heasyContext, String jsonData, String extend) {
        JSONObject jsonObject = FastjsonUtil.string2JSONObject(jsonData);

        if("save".equalsIgnoreCase(extend)){
            try{
                String contact_name = FastjsonUtil.getString(jsonObject, "contact_name");
                String contact_phone = FastjsonUtil.getString(jsonObject, "contact_phone");
                return ContactAPI.save(contact_name, contact_phone);
            }catch (Exception ex){
                logger.error("", ex);
                return ResponseCode.FAILURE.message();
            }

        }else if("update".equalsIgnoreCase(extend)){
            try {
                String id = FastjsonUtil.getString(jsonObject, "id");
                String contact_name = FastjsonUtil.getString(jsonObject, "contact_name");
                String contact_phone = FastjsonUtil.getString(jsonObject, "contact_phone");
                return ContactAPI.update(id, contact_name, contact_phone);
            }catch (Exception ex){
                logger.error("", ex);
                return ResponseCode.FAILURE.message();
            }

        }else if("delete".equalsIgnoreCase(extend)){
            try {
                String id = FastjsonUtil.getString(jsonObject, "id");
                return ContactAPI.delete(id);
            }catch (Exception ex){
                logger.error("", ex);
                return ResponseCode.FAILURE.message();
            }

        }else if("getAll".equalsIgnoreCase(extend)){
            try {
                return ContactAPI.list();
            }catch (Exception ex){
                logger.error("", ex);
            }
            return "[]";

        }else if("sendHelp".equalsIgnoreCase(extend)){
            try {
                String phones = FastjsonUtil.getString(jsonObject, "phones");
                return ContactAPI.notify(phones);
            }catch (Exception ex){
                logger.error("", ex);
                return "发送求助信息失败";
            }
        }else if("selectFromFriends".equalsIgnoreCase(extend)){
            HeasyApplication heasyApplication = (HeasyApplication)heasyContext.getServiceEngine().getAndroidContext();
            final FragmentActivity activity = (FragmentActivity)heasyApplication.getMainActivity();
            final Intent intent = new Intent(activity, FriendListActivity.class);

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    RxActivity
                        .startActivityForResult(activity, intent, 11)
                        .subscribe(new Action1<ActivityBackWrapper>() {
                            @Override
                            public void call(ActivityBackWrapper activityBackWrapper) {
                                logger.debug("ResultCode=" + activityBackWrapper.getResultCode());
                                if(activityBackWrapper.getResultCode() == Activity.RESULT_OK) {
                                    Intent intent = activityBackWrapper.getIntent();
                                    Bundle bundle = intent.getExtras();
                                    String userName = bundle.getString("userName");
                                    String userNumber = bundle.getString("userNumber");

                                    String script = "javascript: try{ getContactInfo_callback(\"" + userName + "\",\"" + userNumber + "\"); }catch(e){ }";
                                    heasyContext.getJsInterface().loadUrl(script);
                                }
                            }
                        });
                }
            });
        }

        return Constants.SUCCESS;
    }

}
