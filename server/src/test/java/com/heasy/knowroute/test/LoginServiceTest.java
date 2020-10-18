package com.heasy.knowroute.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.heasy.Main;
import com.heasy.knowroute.service.LoginService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Main.class)
public class LoginServiceTest {	
	@Autowired
    private LoginService loginService;

	@Test
	public void login(){
		int id = loginService.login("13798189352");
		System.out.println(id);
	}
	
}
