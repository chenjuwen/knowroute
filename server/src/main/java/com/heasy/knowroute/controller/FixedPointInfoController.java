package com.heasy.knowroute.controller;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.heasy.knowroute.api.WebResponse;
import com.heasy.knowroute.bean.FixedPointInfoBean;
import com.heasy.knowroute.service.FixedPointInfoService;
import com.heasy.knowroute.utils.JsonUtil;

@RestController
@RequestMapping("/fixedPointInfo")
public class FixedPointInfoController extends BaseController{
    private static final Logger logger = LoggerFactory.getLogger(FixedPointInfoController.class);
    
	@Autowired
	private FixedPointInfoService fixedPointInfoService;

	@RequestMapping(value="/list/{userId}/{categoryId}", method=RequestMethod.GET)
    public WebResponse list(@PathVariable Integer userId, @PathVariable Integer categoryId) {
		List<FixedPointInfoBean> list = fixedPointInfoService.list(userId, categoryId);
		if(CollectionUtils.isNotEmpty(list)) {
			String data = JsonUtil.object2ArrayString(list);
			logger.debug(data);
			return WebResponse.success(data);
		}else {
			return WebResponse.success("[]");
		}
    }
	
	@RequestMapping(value="/saveOrUpdate", method=RequestMethod.POST, consumes="application/json")
	public WebResponse insert(@RequestBody FixedPointInfoBean bean) {
		if(bean.getId() > 0) {
			boolean b = fixedPointInfoService.update(bean);
			if(b) {
				return WebResponse.success(JsonUtil.toJSONString("id", String.valueOf(bean.getId())));
			}
		}else {
			int id = fixedPointInfoService.insert(bean);
			if(id > 0) {
				return WebResponse.success(JsonUtil.toJSONString("id", String.valueOf(id)));
			}
		}
		return WebResponse.failure();
	}
	
	@RequestMapping(value="/deleteById/{userId}/{id}", method=RequestMethod.POST, consumes="application/json")
	public WebResponse deleteById(@PathVariable Integer userId, @PathVariable Integer id) {
		boolean b = fixedPointInfoService.deleteById(userId, id);
		if(b) {
			return WebResponse.success();
		}
		return WebResponse.failure();
	}
	
	@RequestMapping(value="/deleteByCategory/{userId}/{categoryId}", method=RequestMethod.POST, consumes="application/json")
	public WebResponse deleteByCategory(@PathVariable Integer userId, @PathVariable Integer categoryId) {
		boolean b = fixedPointInfoService.deleteByCategory(userId, categoryId);
		if(b) {
			return WebResponse.success();
		}
		return WebResponse.failure();
	}
	
	@RequestMapping(value="/deleteByUser/{userId}", method=RequestMethod.POST, consumes="application/json")
	public WebResponse deleteByUser(@PathVariable Integer userId) {
		boolean b = fixedPointInfoService.deleteByUser(userId);
		if(b) {
			return WebResponse.success();
		}
		return WebResponse.failure();
	}
	
}
