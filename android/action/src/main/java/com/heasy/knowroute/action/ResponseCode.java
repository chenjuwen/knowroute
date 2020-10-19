package com.heasy.knowroute.action;

public enum ResponseCode {
	SUCCESS(1, "成功"),
	//FAILURE(999, "失败"),

	//PARAM_INVALID(1001, "参数无效"),
	//NO_DATA(1002, "查无数据"),
	SERVICE_CALL_ERROR(1003, "服务调用出错，请稍后再试！");
	//NO_ACCESS(2001, "无权访问");
	
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
