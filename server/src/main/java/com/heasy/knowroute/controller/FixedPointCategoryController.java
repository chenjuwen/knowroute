package com.heasy.knowroute.controller;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.heasy.knowroute.api.ResponseCode;
import com.heasy.knowroute.api.WebResponse;
import com.heasy.knowroute.bean.FixedPointCategoryBean;
import com.heasy.knowroute.service.FixedPointCategoryService;
import com.heasy.knowroute.utils.JsonUtil;
import com.heasy.knowroute.utils.StringUtil;

@RestController
@RequestMapping("/fixedPointCategory")
public class FixedPointCategoryController extends BaseController{
    private static final Logger logger = LoggerFactory.getLogger(FixedPointCategoryController.class);
    
	@Autowired
	private FixedPointCategoryService fixedPointCategoryService;

	@RequestMapping(value="/list/{userId}", method=RequestMethod.GET)
    public WebResponse list(@PathVariable Integer userId) {
		List<FixedPointCategoryBean> list = fixedPointCategoryService.list(userId);
		if(CollectionUtils.isNotEmpty(list)) {
			String data = JsonUtil.object2ArrayString(list, JsonUtil.getJsonConfigOfDate());
			logger.debug(data);
			return WebResponse.success(data);
		}else {
			return WebResponse.success("[]");
		}
    }
	
	@RequestMapping(value="/insert", method=RequestMethod.POST, consumes="application/json")
	public WebResponse insert(@RequestBody Map<String,String> map) {
		String userId = map.get("userId");
		String name = map.get("name");
		
		if(StringUtil.isEmpty(userId) || StringUtil.isEmpty(name)) {
			return WebResponse.failure(ResponseCode.PARAM_INVALID);
		}
		
		boolean b = fixedPointCategoryService.insert(Integer.parseInt(userId), name);
		if(b) {
			return WebResponse.success();
		}
		
		return WebResponse.failure();
	}
	
	@RequestMapping(value="/update", method=RequestMethod.POST, consumes="application/json")
	public WebResponse update(@RequestBody Map<String,String> map) {
		String id = map.get("id");
		String name = map.get("name");
		
		if(StringUtil.isEmpty(id) || StringUtil.isEmpty(name)) {
			return WebResponse.failure(ResponseCode.PARAM_INVALID);
		}
		
		boolean b = fixedPointCategoryService.update(Integer.parseInt(id), name);
		if(b) {
			return WebResponse.success();
		}
		
		return WebResponse.failure();
	}
	
	@RequestMapping(value="/delete/{id}", method=RequestMethod.POST, consumes="application/json")
	public WebResponse delete(@PathVariable Integer id) {
		boolean b = fixedPointCategoryService.delete(id);
		if(b) {
			return WebResponse.success();
		}
		
		return WebResponse.failure();
	}
	
	@RequestMapping(value="/topping/{id}", method=RequestMethod.POST, consumes="application/json")
	public WebResponse topping(@PathVariable Integer id) {
		boolean b = fixedPointCategoryService.topping(id);
		if(b) {
			return WebResponse.success();
		}
		
		return WebResponse.failure();
	}
	
	@RequestMapping(value="/cancelTopping/{id}", method=RequestMethod.POST, consumes="application/json")
	public WebResponse cancelTopping(@PathVariable Integer id) {
		boolean b = fixedPointCategoryService.cancelTopping(id);
		if(b) {
			return WebResponse.success();
		}
		
		return WebResponse.failure();
	}
	
}
