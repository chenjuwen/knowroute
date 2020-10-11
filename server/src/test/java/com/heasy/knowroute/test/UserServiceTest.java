package com.heasy.knowroute.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.heasy.Main;
import com.heasy.knowroute.bean.UserBean;
import com.heasy.knowroute.service.UserService;

import net.sf.json.JSONObject;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Main.class)
public class UserServiceTest {	
	@Autowired
    private UserService userService;

	@Test
	public void getUser(){
		UserBean bean = userService.getUser("admin");
		System.out.println(bean);
		
		if(bean != null) {
			System.out.println(JSONObject.fromObject(bean).toString(2));
		}
	}

	@Test
	public void changePassword(){
		String result = userService.changePassword("admin1", "admin", "admin");
		System.out.println(result);
		
		result = userService.changePassword("admin", "admin", "admin");
		System.out.println(result);
		
		result = userService.changePassword("admin", "123456", "admin");
		System.out.println(result);
		
		UserBean bean = userService.getUser("admin");
		System.out.println(bean);
		
		if(bean != null) {
			System.out.println(JSONObject.fromObject(bean).toString(2));
		}
	}
}
