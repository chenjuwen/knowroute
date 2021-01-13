package com.heasy.knowroute.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.heasy.Main;
import com.heasy.knowroute.bean.MessageBean;
import com.heasy.knowroute.common.EnumConstants;
import com.heasy.knowroute.service.MessageService;
import com.heasy.knowroute.utils.JsonUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Main.class)
public class MessageServiceTest {
	@Autowired
    private MessageService messageService;
	
	@Test
	public void getMessage() {
		MessageBean bean = messageService.getMessage(String.valueOf(5), "18218324742", EnumConstants.MessageCategory.INVITE_FRIEND.name());
		if(bean != null) {
			System.out.println(JsonUtil.object2String(bean));
		}
	}

	@Test
	public void insert() {
		MessageBean bean = new MessageBean();
		bean.setContent("请求添加您为好友");
		bean.setCategory(EnumConstants.MessageCategory.INVITE_FRIEND.name());
		bean.setResult("");
		bean.setSender(String.valueOf(5));
		bean.setReceiver("18218324742");
		bean.setStatus(0);
		
		int id = messageService.insert(bean);
		System.out.println(id);
	}
	
	@Test
	public void confirmMessage() {
		messageService.confirmMessage(16, "已忽略");
	}
}
