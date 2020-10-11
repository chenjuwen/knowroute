package com.heasy.knowroute.core.webview;

import com.heasy.knowroute.core.HeasyContext;

/**
 * Created by Administrator on 2017/12/21.
 */
public interface Action {
    String execute(HeasyContext heasyContext, String jsonData, String extend);
}
