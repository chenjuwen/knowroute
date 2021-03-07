package com.heasy.knowroute.bean;

public enum ResponseCode {
	SUCCESS(1, "成功"),
	FAILURE(999, "操作失败"),
	
	PARAM_INVALID(1001, "参数无效"),
	NO_DATA(1002, "查无数据"),
	PHONE_INVALID(1003, "手机号码错误"),
	CAPTCHA_INVALID(1004, "验证码错误"),
	GET_CAPTCHA_ERROR(1005, "获取验证码失败"),
	
	NO_ACCESS(2001, "无权访问"),
	LOGIN_ERROR(2002, "登录失败"),
	TOKEN_ERROR(2003, "token有误"),
	REFRESH_TOKEN_ERROR(2004, "刷新token失败"),
	REQUEST_LIMIT(2005, "访问过于频繁"),
	NO_DATA_ACCESS(2006, "无数据访问权限");
	
	private Integer code;
	private String message;

	ResponseCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
	
	public Integer code() {
		return this.code;
	}
	
	public String message() {
		return this.message;
	}
}
