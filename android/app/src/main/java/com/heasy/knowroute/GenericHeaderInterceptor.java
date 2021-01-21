package com.heasy.knowroute;

import com.heasy.knowroute.core.service.ServiceEngineFactory;
import com.heasy.knowroute.service.LoginService;
import com.heasy.knowroute.service.LoginServiceImpl;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 网络拦截器：添加公共请求头
 */
public class GenericHeaderInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        LoginService loginService = ServiceEngineFactory.getServiceEngine().getService(LoginServiceImpl.class);

        Request originalRequest = chain.request();
        Request newRequest = originalRequest.newBuilder()
                .header("token", loginService.getToken())
                .header("front", "KR_APP")
                .build();
        return chain.proceed(newRequest);
    }

}
