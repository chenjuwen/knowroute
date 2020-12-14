package com.heasy.knowroute;

import com.heasy.knowroute.action.ActionScanner;
import com.heasy.knowroute.core.HeasyContext;
import com.heasy.knowroute.core.configuration.AbstractComponentScanner;
import com.heasy.knowroute.core.webview.DefaultActionDispatcher;
import com.heasy.knowroute.core.webview.DefaultDownloadListener;
import com.heasy.knowroute.core.webview.DefaultWebChromeClient;
import com.heasy.knowroute.core.webview.DefaultWebViewClient;
import com.heasy.knowroute.core.webview.JSInterfaceImpl;
import com.heasy.knowroute.core.webview.WebViewWrapper;

/**
 * Created by Administrator on 2018/1/23.
 */
public class WebViewWrapperFactory {
    private static DefaultActionDispatcher actionDispatcher = null;
    private static WebViewWrapper webViewWrapper = null;

    /**
     * 初始化DefaultJSActionRouter
     * @param heasyContext
     * @param actionBasePackages 多个包路径用 分号 分隔
     */
    public static void initActionDispatcher(HeasyContext heasyContext, String actionBasePackages){
        AbstractComponentScanner actionScanner = new ActionScanner();
        actionScanner.setContext(heasyContext.getServiceEngine().getAndroidContext());
        actionScanner.setBasePackages(actionBasePackages);

        //DefaultActionDispatcher
        actionDispatcher = new DefaultActionDispatcher();
        actionDispatcher.setActionScanner(actionScanner);
        actionDispatcher.init();
    }

    /**
     * @param heasyContext
     */
    public static void build(HeasyContext heasyContext){
        //JSInterfaceImpl
        JSInterfaceImpl jsInterface = new JSInterfaceImpl();
        jsInterface.setActionDispatcher(actionDispatcher);
        jsInterface.setHeasyContext(heasyContext);

        //DefaultWebViewClient
        DefaultWebViewClient webViewClient = new DefaultWebViewClient(heasyContext);

        String htmlLoadBasePath = heasyContext.getServiceEngine().getConfigurationService().getConfigBean().getWebviewLoadBasePath();

        //保证select控件能弹出下拉框
        HeasyApplication app = (HeasyApplication)heasyContext.getServiceEngine().getAndroidContext();
        webViewWrapper = new WebViewWrapper.Builder(app.getMainActivity())
                .setWebViewClient(webViewClient)
                .setWebChromeClient(new DefaultWebChromeClient(app.getMainActivity()))
                .setDownloadListener(new DefaultDownloadListener(app.getMainActivity()))
                .setJSInterface(jsInterface)
                .setHtmlBasePath(htmlLoadBasePath)
                .build();

        heasyContext.setJsInterface(jsInterface);
    }

    public static WebViewWrapper getWebViewWrapper() {
        return webViewWrapper;
    }

}
