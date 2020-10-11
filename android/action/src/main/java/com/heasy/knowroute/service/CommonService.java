package com.heasy.knowroute.service;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.telephony.SmsManager;

import com.example.library.ActivityBackWrapper;
import com.example.library.RxActivity;
import com.heasy.knowroute.core.HeasyApplication;
import com.heasy.knowroute.core.HeasyContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import rx.functions.Action1;

/**
 * Created by Administrator on 2020/10/7.
 */
public class CommonService {
    private static final Logger logger = LoggerFactory.getLogger(CommonService.class);

    /**
     * 发送短信
     * @param phoneNumber 手机号码
     * @param message 短信信息
     */
    public static void sendSMS( String phoneNumber, String message) {
        SmsManager smsManager = SmsManager.getDefault();

        // 拆分短信内容（手机短信长度限制）
        List<String> divideContents = smsManager.divideMessage(message);
        for (String text : divideContents) {
            smsManager.sendTextMessage(phoneNumber, null, text, null, null);
        }
    }

    /**
     * 从通讯录获取联系人号码和称呼
     * @param heasyContext
     */
    public static void getContactInfo(final HeasyContext heasyContext) {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        HeasyApplication heasyApplication = (HeasyApplication) heasyContext.getServiceEngine().getAndroidContext();
        final FragmentActivity activity = (FragmentActivity) heasyApplication.getMainActivity();
        final Intent tmpIntent = intent;

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RxActivity
                    .startActivityForResult(activity, tmpIntent, 1)
                    .subscribe(new Action1<ActivityBackWrapper>() {
                        @Override
                        public void call(ActivityBackWrapper activityBackWrapper) {
                            if(activityBackWrapper.getResultCode() == Activity.RESULT_OK) {
                                ContentResolver reContentResolverol = activity.getContentResolver();
                                Uri contactData = activityBackWrapper.getIntent().getData();
                                Cursor cursor = activity.managedQuery(contactData, null, null, null, null);
                                cursor.moveToFirst();

                                //称呼
                                String username = cursor.getString(cursor
                                        .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                                String contactId = cursor.getString(cursor
                                        .getColumnIndex(ContactsContract.Contacts._ID));

                                Cursor phone = reContentResolverol.query(
                                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = "
                                                + contactId, null, null);

                                while (phone.moveToNext()) {
                                    //手机号
                                    String usernumber = phone
                                            .getString(phone
                                                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                    logger.info(username + ", " + usernumber);

                                    contactCallback(heasyContext, username, usernumber);
                                    break;
                                }
                            }
                        }
                    });
            }
        });
    }

    private static void contactCallback(HeasyContext heasyContext, String userName, String userNumber){
        try {
            String script = "javascript: try{ contactCallback(\"" + userName + "\",\"" + userNumber + "\"); }catch(e){ }";
            heasyContext.getJsInterface().loadUrl(script);
        } catch (Exception ex) {
            logger.error("", ex);
        }
    }

}
