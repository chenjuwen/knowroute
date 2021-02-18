package com.heasy.knowroute.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.heasy.Main;
import com.heasy.knowroute.service.DataCacheService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Main.class)
public class DataCacheServiceTest {
	@Autowired
	private DataCacheService dataCacheService;
	
	@Test
	public void test1() {
		long count = dataCacheService.incr("count");
		System.out.println(count);
		System.out.println(dataCacheService.get("count"));
		count = dataCacheService.decr("count");
		System.out.println(count);
	}
}
