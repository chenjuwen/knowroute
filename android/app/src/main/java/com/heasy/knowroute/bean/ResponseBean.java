package com.heasy.knowroute.bean;

import java.io.Serializable;

public class ResponseBean implements Serializable{
	private Integer code;
	private String message;
	private Object data;

	public static ResponseBean failure(ResponseCode responseCode) {
		ResponseBean result = new ResponseBean();
		result.setCode(responseCode.code());
		result.setMessage(responseCode.message());
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

}
