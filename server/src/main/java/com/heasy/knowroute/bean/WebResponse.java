package com.heasy.knowroute.bean;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description="接口返回结果JavaBean类")
public class WebResponse implements Serializable{
	private static final long serialVersionUID = 8075402133320819186L;

	@ApiModelProperty(value="结果状态码")
	private Integer code;
	
	@ApiModelProperty(value="结果消息")
	private String message;
	
	@ApiModelProperty(value="结果数据")
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
	
	public static WebResponse failure() {
		return failure(ResponseCode.FAILURE);
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
	
	public static WebResponse failure(Integer code, String message) {
		WebResponse result = new WebResponse();
		result.setCode(code);
		result.setMessage(message);
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
