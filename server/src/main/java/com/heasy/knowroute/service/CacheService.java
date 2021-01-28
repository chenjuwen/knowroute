package com.heasy.knowroute.service;

import org.springframework.cache.Cache;

public interface CacheService {
	Cache getCache(String name);
	String get(Cache cache, Object key);
	<T> T get(Cache cache, Object key, Class<T> type);
	void put(Cache cache, Object key, Object value);
	void evict(Cache cache, Object key);
	void clear(Cache cache);
}
