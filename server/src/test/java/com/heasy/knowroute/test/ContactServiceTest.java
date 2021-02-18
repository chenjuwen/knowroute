package com.heasy.knowroute.test;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.heasy.Main;
import com.heasy.knowroute.bean.ContactBean;
import com.heasy.knowroute.service.ContactService;

import net.sf.json.JSONArray;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Main.class)
public class ContactServiceTest {
	@Autowired
    private ContactService contactService;

	@Test
	public void save() {
		ContactBean bean = new ContactBean();
		bean.setUserId(5);
		bean.setContactName("张议月");
		bean.setContactPhone("18218324742");
		
		boolean b = contactService.save(bean);
		System.out.println(b);
	}
	
	@Test
	public void update() {
		ContactBean bean = new ContactBean();
		bean.setId(1);
		bean.setUserId(5);
		bean.setContactName("张议月2");
		bean.setContactPhone("18218324742");

		boolean b = contactService.update(bean);
		System.out.println(b);
	}
	
	@Test
	public void getAll(){
		List<ContactBean> list = contactService.list(5);
		if(!CollectionUtils.isEmpty(list)) {
			System.out.println(JSONArray.fromObject(list).toString());
		}
	}
}
