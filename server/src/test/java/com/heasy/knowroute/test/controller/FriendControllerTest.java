package com.heasy.knowroute.test.controller;

import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import com.heasy.knowroute.utils.JsonUtil;

public class FriendControllerTest extends BaseTest{
    @Transactional
    @Test
    public void invite() throws Exception{
    	String json = JsonUtil.toJSONString("userId", "5", "phone", "18218324742");
    	post("/friend/invite", json);
    }
    
    @Transactional
    @Test
    public void add() throws Exception{
    	String json = JsonUtil.toJSONString("userId", "5", "friendUserId", "15", "phone", "18218324742");
    	post("/friend/add", json);
    }
    
    @Transactional
    @Test
    public void confirmAdd() throws Exception{
    	String json = JsonUtil.toJSONString("id", "43", "pass", "yes");
    	post("/friend/confirmAdd", json);
    }
    
    @Transactional
    @Test
    public void updateNickname() throws Exception{
    	String json = JsonUtil.toJSONString("id", "43", "userId", "5", "newNickname", "test");
    	post("/friend/updateNickname", json);
    }
}
