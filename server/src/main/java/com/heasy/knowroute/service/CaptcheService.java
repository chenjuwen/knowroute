package com.heasy.knowroute.service;

public interface CaptcheService {
	public void set(String key, String value);
	public String get(String key);
	public void delete(String key);
	public void clean();
}
