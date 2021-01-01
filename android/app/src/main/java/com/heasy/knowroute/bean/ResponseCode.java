package com.heasy.knowroute.bean;

public enum ResponseCode {
	SUCCESS(1, "成功"),
	FAILURE(999, "操作失败"),

	SERVICE_CALL_ERROR(101, "服务调用出错，请稍后再试！"),
	NETWORK_ERROR(102, "网络异常");
	
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
