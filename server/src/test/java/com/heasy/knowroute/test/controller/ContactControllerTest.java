package com.heasy.knowroute.test.controller;

import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import com.heasy.knowroute.utils.JsonUtil;

public class ContactControllerTest extends BaseTest{
    @Transactional
    @Test
    public void notifyContact() throws Exception{
    	String json = JsonUtil.toJSONString("userId", "5", "helpPhone", "13798189352", "friendPhone", "18218324742");
    	post("/contact/notify", json);
    }
}
