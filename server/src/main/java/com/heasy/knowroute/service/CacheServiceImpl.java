package com.heasy.knowroute.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
public class CacheServiceImpl implements CacheService{
    @Autowired
    private CacheManager cacheManager;
    
	@Override
	public Cache getCache(String name) {
		return cacheManager.getCache(name);
	}

	@Override
	public String get(Cache cache, Object key) {
		return cache.get(key, String.class);
	}
	
	@Override
	public <T> T get(Cache cache, Object key, Class<T> type) {
		return cache.get(key, type);
	}

	@Override
	public void put(Cache cache, Object key, Object value) {
		cache.put(key, value);
	}

	@Override
	public void evict(Cache cache, Object key) {
		cache.evict(key);
	}

	@Override
	public void clear(Cache cache) {
		cache.clear();
	}
	
}
