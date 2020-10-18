package com.heasy.knowroute.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.heasy.Main;
import com.heasy.knowroute.bean.UserBean;
import com.heasy.knowroute.service.UserService;
import com.heasy.knowroute.utils.DatetimeUtil;
import com.heasy.knowroute.utils.StringUtil;

import net.sf.json.JSONObject;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Main.class)
public class UserServiceTest {	
	@Autowired
    private UserService userService;

	@Test
	public void getUser(){
		UserBean bean = userService.getUser("13798189352");
		if(bean != null) {
			System.out.println(JSONObject.fromObject(bean).toString(2));
		}else {
			System.out.println("user not found");
		}
		
		bean = userService.getUser(8);
		if(bean != null) {
			System.out.println(DatetimeUtil.formatDate(bean.getCreate_date()));
			System.out.println(DatetimeUtil.formatDate(bean.getLast_login_date()));
		}else {
			System.out.println("user not found");
		}
	}
	
	@Test
	public void insertUser() {
		int id = userService.insert("13798189356", StringUtil.getUUIDString());
		System.out.println(id);
	}
	
	@Test
	public void updateUser() {
		UserBean bean = userService.getUser("13798189356");
		if(bean != null) {
			userService.updateNickname(bean.getId(), "张三");
			userService.updateLastLoginDate(bean.getId());
			userService.updatePositionInfo(bean.getId(), 1.123f, 2.456f, "address");
		}
	}
}
