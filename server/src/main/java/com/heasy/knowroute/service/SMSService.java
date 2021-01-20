package com.heasy.knowroute.service;

public interface SMSService {
	boolean sendVerificationCode(String phone, String captcha);
}
