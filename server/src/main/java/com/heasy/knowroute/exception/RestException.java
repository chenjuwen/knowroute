package com.heasy.knowroute.exception;

public class RestException extends RuntimeException {
	private static final long serialVersionUID = 1393919605589372914L;
	private Integer code;
	private String message;
	
	public RestException(Integer code, String message){
		super(message);
		this.code = code;
		this.message = message;
	}
	
	public Integer getCode() {
		return code;
	}
	
	public void setCode(Integer code) {
		this.code = code;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
}
