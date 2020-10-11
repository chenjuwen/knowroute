package com.heasy.knowroute.test;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.heasy.Main;
import com.heasy.knowroute.bean.DataBean;
import com.heasy.knowroute.service.DataService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Main.class)
public class DataServiceTest {
	@Autowired
    private DataService dataService;

	@Test
	public void getAllData(){
		List<DataBean> list = dataService.getAllData();
		if(!CollectionUtils.isEmpty(list)) {
			System.out.println(JSONArray.fromObject(list).toString(2));
		}
	}
	
	@Test
	public void getByPeriod(){
		DataBean bean = dataService.getByPeriod("010");
		if(bean != null) {
			System.out.println(JSONObject.fromObject(bean).toString(2));
		}
	}
	
	@Test
	public void deleteByPeriod(){
		boolean b = dataService.deleteByPeriod("111");
		System.out.println(b);
	}
	
	@Test
	public void add(){
		boolean b = dataService.add("111", "111");
		System.out.println(b);
	}
	
	@Test
	public void update(){
		boolean b = dataService.update("111", "new data");
		System.out.println(b);
	}
	
}
