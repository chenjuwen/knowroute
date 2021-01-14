package com.heasy.knowroute.core;

import com.heasy.knowroute.core.okhttp.HttpClientListener;
import com.heasy.knowroute.core.okhttp.OkHttpClientHelper;
import com.heasy.knowroute.core.okhttp.RequestBuilder;
import com.heasy.knowroute.core.okhttp.interceptor.LogInterceptor;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;

public class HttpTest {
    @Test
    public void synGet() throws Exception {
        OkHttpClientHelper okHttpClientHelper = new OkHttpClientHelper();
        okHttpClientHelper
                .addNetworkInterceptor(new LogInterceptor())
                .build();

        String content = okHttpClientHelper.synGet("https://www.knowroute.cn/knowroute/index");
        System.out.println(content);
        okHttpClientHelper.destroy();
    }

    @Test
    public void asynGet() throws Exception {
        OkHttpClientHelper okHttpClientHelper = new OkHttpClientHelper();
        okHttpClientHelper.build();

        okHttpClientHelper.asynGet("http://www.knowroute.cn/knowroute/index", new HttpClientListener() {
            @Override
            public void onResponse(Response response) {
                try {
                    System.out.println(response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    @Test
    public void postJSON() throws Exception {
        String jsonData = "{\"username\":\"cjm\", \"password\":\"123\"}";

        OkHttpClientHelper okHttpClientHelper = new OkHttpClientHelper();
        okHttpClientHelper.build();

        okHttpClientHelper.postJSON("http://10.1.43.6:8888/boot/saveUser2", jsonData, new HttpClientListener() {
            @Override
            public void onResponse(Response response) {
                try {
                    System.out.println(response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    @Test
    public void post(){
        try {
            Request request = new RequestBuilder()
                    .url("http://www.knowroute.cn/knowroute/login")
                    .addFormParam("phone", "13798189352")
                    .build();

            OkHttpClientHelper okHttpClientHelper = new OkHttpClientHelper();
            okHttpClientHelper.build();

            String result = okHttpClientHelper.post(request);
            System.out.println(result);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Test
    public void postWithFile() throws Exception {
        Request request = new RequestBuilder()
                .url("http://10.1.43.6:8888/boot/saveUser4")
                .addHeader("token", "123456")
                .addFormParam("username", "cjm")
                .addFormParam("password", "123中文")
                .addFileParam("image", "error.png", MediaType.parse("image/png"), new File("D:\\git_proj\\study\\okhttp\\error.png"))
                .build();

        OkHttpClientHelper okHttpClientHelper = new OkHttpClientHelper();
        okHttpClientHelper.build();

        String result = okHttpClientHelper.post(request);
        System.out.println(result);
    }

}