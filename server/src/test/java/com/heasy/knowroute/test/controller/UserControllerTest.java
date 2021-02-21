package com.heasy.knowroute.test.controller;

import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import com.heasy.knowroute.utils.JsonUtil;

public class UserControllerTest extends BaseTest{
    @Transactional
    @Test
    public void updateNickname() throws Exception{
    	String json = JsonUtil.toJSONString("userId", "5", "newNickname", "test");
    	post("/user/updateNickname", json);
    }
}
