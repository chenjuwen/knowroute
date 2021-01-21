package com.heasy.knowroute.service;

import org.springframework.cache.Cache;

public interface CacheService {
	public static final String CACHE_NAME_CAPTCHA = "CaptchaCache";
	public static final String CACHE_NAME_TOKEN = "TokenCache";
	
	Cache getCache(String name);
	String get(Cache cache, Object key);
	<T> T get(Cache cache, Object key, Class<T> type);
	void put(Cache cache, Object key, Object value);
	void evict(Cache cache, Object key);
	void clear(Cache cache);
}
