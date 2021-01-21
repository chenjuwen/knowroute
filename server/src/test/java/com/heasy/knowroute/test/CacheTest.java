package com.heasy.knowroute.test;

import javax.annotation.Resource;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.heasy.Main;
import com.heasy.knowroute.service.CacheService;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = Main.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class CacheTest {
	@Resource
	private CacheService cacheService;
	
	@org.junit.Test
	public void cacheTest() {
	    Cache cache = cacheService.getCache(CacheService.CACHE_NAME_TOKEN);
//	    cacheService.put(cache, "key", "123");
//	    System.out.println("缓存成功");
	    String res = cacheService.get(cache, "key");
	    System.out.println(res);
	}
}
