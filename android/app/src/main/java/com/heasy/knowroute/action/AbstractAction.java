package com.heasy.knowroute.action;

import android.app.Activity;
import android.content.Intent;

import com.heasy.knowroute.DatetimeActivity;
import com.heasy.knowroute.HeasyApplication;
import com.heasy.knowroute.core.HeasyContext;
import com.heasy.knowroute.core.webview.Action;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by Administrator on 2020/11/5.
 */
public abstract class AbstractAction implements Action{
    /**
     * 打开Activity
     * @param heasyContext
     * @param targetActivityClass 要打开的Activity类
     */
    protected void startActivity(HeasyContext heasyContext, Class targetActivityClass){
        startActivity(heasyContext, targetActivityClass, null);
    }

    /**
     * 打开Activity
     * @param heasyContext
     * @param targetActivityClass
     * @param params
     */
    protected void startActivity(HeasyContext heasyContext, Class targetActivityClass, Map<String, String> params){
        HeasyApplication application = (HeasyApplication)heasyContext.getServiceEngine().getAndroidContext();
        Activity mainActivity = application.getMainActivity();

        Intent intent = new Intent(mainActivity, targetActivityClass);

        if(params != null && params.size() > 0){
            for(Iterator<String> it=params.keySet().iterator(); it.hasNext();){
                String key = it.next();
                String value = params.get(key);
                intent.putExtra(key, value);
            }
        }

        mainActivity.startActivity(intent);
    }

}
