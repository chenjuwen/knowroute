package com.heasy.knowroute.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.heasy.knowroute.api.WebResponse;
import com.heasy.knowroute.utils.DatetimeUtil;

import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@Controller
public class IndexController extends BaseController{
	@RequestMapping("/index")
	@ResponseBody
	public WebResponse index(ModelMap modelMap){
		return WebResponse.success(DatetimeUtil.getToday());
	}
}
