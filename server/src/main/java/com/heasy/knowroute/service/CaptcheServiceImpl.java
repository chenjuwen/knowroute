package com.heasy.knowroute.service;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * 验证码服务
 */
@Service
public class CaptcheServiceImpl implements CaptcheService {
    private static final Logger logger = LoggerFactory.getLogger(CaptcheServiceImpl.class);
    
	private Cache<String, String> cache;
	
	@PostConstruct
	public void init() {
		cache = CacheBuilder.newBuilder()
				.maximumSize(1000) //最大存储
				.expireAfterWrite(10, TimeUnit.MINUTES) //写入后10分钟过期
				.build();
		logger.info("cache init!");
	}
	
	@PreDestroy
	public void destroy() {
		cache.invalidateAll();
		cache = null;
	}
	
	@Override
	public void set(String key, String value) {
		cache.put(key, value);
	}

	@Override
	public String get(String key) {
		return cache.getIfPresent(key);
	}

	@Override
	public void delete(String key) {
		cache.invalidate(key);
	}

	@Override
	public void clean() {
		cache.invalidateAll();
	}

}
