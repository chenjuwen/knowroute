package com.heasy.knowroute.okhttp;

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
        okHttpClientHelper.build();

        String content = okHttpClientHelper.synGet("http://180.215.209.100/knowroute/index");
        System.out.println(content);
    }

    @Test
    public void asynGet() throws Exception {
        OkHttpClientHelper okHttpClientHelper = new OkHttpClientHelper();
        okHttpClientHelper.build();

        okHttpClientHelper.asynGet("http://www.knowroute.cn/knowroute/index", new HttpClientListener() {
            @Override
            public void onReponse(Response response) {
                try {
                    System.out.println(response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
            public void onReponse(Response response) {
                try {
                    System.out.println(response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
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