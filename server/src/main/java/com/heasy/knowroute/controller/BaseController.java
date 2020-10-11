package com.heasy.knowroute.controller;

import net.sf.json.JSONObject;

public class BaseController {
	protected String getSuccessResultJsonString(){
		return getResultJsonString("0", "成功");
	}
	
	protected String getResultJsonString(String code, String desc){
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("code", code);
		jsonObject.put("desc", desc);
		return jsonObject.toString(2);
	}
}
