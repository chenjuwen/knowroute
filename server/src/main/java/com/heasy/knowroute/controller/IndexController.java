package com.heasy.knowroute.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.heasy.knowroute.bean.VersionBean;
import com.heasy.knowroute.bean.WebResponse;
import com.heasy.knowroute.common.RequestLimitAnnotation;
import com.heasy.knowroute.service.VersionService;
import com.heasy.knowroute.utils.DatetimeUtil;

import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@Controller
@RequestLimitAnnotation
public class IndexController extends BaseController{
    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);
    
	@Autowired
	private VersionService versionService;
	
	@RequestMapping("/index")
	@ResponseBody
	public WebResponse index(ModelMap modelMap){
		return WebResponse.success(DatetimeUtil.getToday());
	}
	
	@RequestMapping(value="/aboutme", method=RequestMethod.GET)
	public String aboutme(ModelMap modelMap){
		//最新版本号
		VersionBean versionBean = versionService.getLatestVersion();
		if(versionBean != null) {
			logger.debug("latest version: " + versionBean.getVname());
			modelMap.put("version", versionBean.getVname());
		}else {
			modelMap.put("version", "");
		}
				
		return "aboutme";
	}
}
