package com.heasy.knowroute.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class QuickHelpController extends BaseController{
	public static final String MAP_AK = "dznCIamw2fFeqGOxQGHKYAxACOYw143G";
	
	@RequestMapping("/helpme")
	public String map(ModelMap modelMap) {
		modelMap.put("longitude", new Float(111.556564)); //经度
		modelMap.put("latitude", new Float(22.611926)); //纬度
		modelMap.put("phone", "13798189352"); //手机号
		modelMap.put("address", "广东省云浮市罗定市897乡道靠近大路口"); //地址
		modelMap.put("ak", MAP_AK);
		
		return "helpme_map";
	}
}
