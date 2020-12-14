package com.heasy.knowroute.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.heasy.knowroute.api.WebResponse;
import com.heasy.knowroute.bean.VersionBean;
import com.heasy.knowroute.service.VersionService;

@RestController
@RequestMapping("/version")
public class VersionController extends BaseController{
    private static final Logger logger = LoggerFactory.getLogger(VersionController.class);
    
	@Autowired
	private VersionService versionService;

	@RequestMapping(value="/lasted", method=RequestMethod.GET)
	public WebResponse lasted() {
		VersionBean bean = versionService.getLatestVersion();
		if(bean != null) {
			logger.debug("current lasted version is " + bean.getVname());
			return WebResponse.success(bean.getVname());
		}
		return WebResponse.failure();
	}
}
