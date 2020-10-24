package com.heasy.knowroute.action;

public enum ResponseCode {
	SUCCESS(1, "成功"),
	SERVICE_CALL_ERROR(1003, "服务调用出错，请稍后再试！");
	
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
