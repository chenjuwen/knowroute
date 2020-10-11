package com.heasy.knowroute.api;

public enum ResponseCode {
	SUCCESS(1, "成功"),
	FAILURE(999, "失败"),
	PARAM_INVALID(1001, "参数无效"),
	NO_DATA(1002, "查无数据"),
	NO_ACCESS(2001, "无权访问");
	
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
