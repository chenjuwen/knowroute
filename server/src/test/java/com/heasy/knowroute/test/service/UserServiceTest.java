package com.heasy.knowroute.test.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.heasy.knowroute.bean.SimpleUserBean;
import com.heasy.knowroute.bean.UserBean;
import com.heasy.knowroute.service.SMSService;
import com.heasy.knowroute.service.UserService;
import com.heasy.knowroute.utils.DatetimeUtil;
import com.heasy.knowroute.utils.StringUtil;

import net.sf.json.JSONObject;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {	
	@Autowired
    private UserService userService;
	@Autowired
	private SMSService smsService;
	
	@Test
	public void sendSMS() {
		String captche = StringUtil.getRandomNumber(6);
		boolean b = smsService.sendVerificationCode("13798189352", captche);
		System.out.println(captche);
		System.out.println(b);
	}

	@Test
	public void login(){
		int id = userService.login("13798189352");
		System.out.println(id);
	}

	@Test
	public void getUser(){
		SimpleUserBean bean = userService.getUserByPhone("13798189352");
		if(bean != null) {
			System.out.println(JSONObject.fromObject(bean).toString(2));
		}else {
			System.out.println("user not found");
		}
		
		UserBean bean2 = userService.getUserById(8);
		if(bean2 != null) {
			System.out.println(DatetimeUtil.formatDate(bean2.getCreateDate()));
			System.out.println(DatetimeUtil.formatDate(bean2.getLastLoginDate()));
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
		SimpleUserBean bean = userService.getUserByPhone("13798189356");
		if(bean != null) {
			userService.updateNickname(bean.getId(), "张三");
			userService.updateLastLoginDate(bean.getId());
			userService.updatePositionInfo(bean.getId(), 1.123f, 2.456f, "address", DatetimeUtil.nowDate());
		}
	}
	
	@Test
	public void cancelAccount() {
		userService.cancelAccount(1, "13798188343");
	}
}
