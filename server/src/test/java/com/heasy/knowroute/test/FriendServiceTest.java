package com.heasy.knowroute.test;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.heasy.Main;
import com.heasy.knowroute.bean.FriendBean;
import com.heasy.knowroute.service.FriendService;
import com.heasy.knowroute.utils.JsonUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Main.class)
public class FriendServiceTest {
	@Autowired
    private FriendService friendService;

	@Test
	public void checkFriend() {
		String result = friendService.checkFriend(5, "12798189352");
		System.out.println(result);
		result = friendService.checkFriend(5, "13798189359");
		System.out.println(result);
		result = friendService.checkFriend(5, "13798189352");
		System.out.println(result);
		result = friendService.checkFriend(5, "13798189355");
		System.out.println(result);
		result = friendService.checkFriend(5, "13798189353");
		System.out.println(result);
	}
	
	@Test
	public void getFriend() {
		FriendBean bean = friendService.getFriend(5, "13798189359");
		if(bean != null) {
			System.out.println(JsonUtil.object2String(bean));
		}else {
			System.out.println("bean is null");
		}
		
		bean = friendService.getFriend(5, "13798189353");
		if(bean != null) {
			System.out.println(JsonUtil.object2String(bean));
		}else {
			System.out.println("bean is null");
		}
		
		List<FriendBean> list = friendService.getFriendList(5);
		if(list != null) {
			System.out.println(JsonUtil.object2ArrayString(list));
		}
	}

	@Test
	public void insertFriend() {
		friendService.insert(5, "13798189356");
	}

	@Test
	public void update() {
		friendService.updateNickname(3, "张三2");
	}

	@Test
	public void delete() {
		boolean b = friendService.delete(3);
		System.out.println(b);
	}
	
}
