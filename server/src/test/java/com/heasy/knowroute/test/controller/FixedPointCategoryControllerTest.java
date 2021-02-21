package com.heasy.knowroute.test.controller;

import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import com.heasy.knowroute.utils.JsonUtil;

public class FixedPointCategoryControllerTest extends BaseTest{
    @Test
    public void list() throws Exception{
    	get("/fixedPointCategory/list/5");
    }
    
    @Transactional
    @Test
    public void insert() throws Exception{
    	String json = JsonUtil.toJSONString("userId", "5", "name", "222");
    	post("/fixedPointCategory/insert", json);
    }
    
    @Transactional //事务回滚，不改变数据库的数据
    @Test
    public void update() throws Exception{
    	String json = JsonUtil.toJSONString("id", "16", "userId", "5", "name", "22222");
    	post("/fixedPointCategory/update", json);
    }
    
    @Transactional
    @Test
    public void delete() throws Exception{
    	post("/fixedPointCategory/delete/5/16", "");
    }
}
