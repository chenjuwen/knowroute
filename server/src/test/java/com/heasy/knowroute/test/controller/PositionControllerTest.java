package com.heasy.knowroute.test.controller;

import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

public class PositionControllerTest extends BaseTest{
    @Transactional
    @Test
    public void cleanup() throws Exception{
    	post("/position/cleanup/5/0", "");
    }
}
