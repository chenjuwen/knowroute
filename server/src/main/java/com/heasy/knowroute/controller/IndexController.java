package com.heasy.knowroute.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.heasy.knowroute.api.WebResponse;

@Controller
public class IndexController extends BaseController{
	@RequestMapping("/index")
	@ResponseBody
	public WebResponse index(ModelMap modelMap){
		return WebResponse.success();
	}
	
	@RequestMapping("/map")
	public String map() {
		return "map";
	}
}
