package com.heasy.knowroute.test.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.heasy.knowroute.service.DataCacheService;

@RunWith(SpringRunner.class)
@SpringBootTest
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
