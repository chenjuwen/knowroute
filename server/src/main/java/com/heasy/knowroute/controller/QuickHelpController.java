package com.heasy.knowroute.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.heasy.knowroute.bean.UserBean;
import com.heasy.knowroute.service.UserService;

@Controller
public class QuickHelpController extends BaseController{
	@Autowired
	private UserService userService;
	
	@RequestMapping(value="/helpme", method=RequestMethod.GET)
	public String map(ModelMap modelMap, @RequestParam(value="u") String userId, 
			@RequestParam(value="p") String phone) {
		Double longitude = null;
		Double latitude = null;
		String address = "";
		
		UserBean userBean = userService.getUser(Integer.parseInt(userId));
		if(userBean != null) {
			if(userBean.getPhone().equals(phone)) {
				longitude = userBean.getLongitude();
				latitude = userBean.getLatitude();
				address = userBean.getAddress();
			}
		}
		
		modelMap.put("longitude", longitude); //经度
		modelMap.put("latitude", latitude); //纬度
		modelMap.put("phone", phone); //手机号
		modelMap.put("address", address); //地址
		
		return "helpme_map";
	}
}
