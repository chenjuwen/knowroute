package com.heasy.knowroute.api;

import java.io.Serializable;

public class WebResponse implements Serializable{
	private static final long serialVersionUID = 8075402133320819186L;
	
	private Integer code;
	private String message;
	private Object data;
	
	public WebResponse() {
		
	}
	
	public WebResponse(ResponseCode resultCode, Object data) {
		this.code = resultCode.code();
		this.message = resultCode.message();
		this.data = data;
	}
	
	public static WebResponse success() {
		return success(null);
	}
	
	public static WebResponse success(Object data) {
		WebResponse result = new WebResponse();
		result.setResultCode(ResponseCode.SUCCESS);
		result.setData(data);
		return result;
	}
	
	public static WebResponse failure(ResponseCode resultCode) {
		WebResponse result = new WebResponse();
		result.setResultCode(resultCode);
		return result;
	}
	
	public static WebResponse failure(ResponseCode resultCode, Object data) {
		WebResponse result = new WebResponse();
		result.setResultCode(resultCode);
		result.setData(data);
		return result;
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

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public void setResultCode(ResponseCode resultCode) {
		this.code = resultCode.code();
		this.message = resultCode.message();
	}

}
