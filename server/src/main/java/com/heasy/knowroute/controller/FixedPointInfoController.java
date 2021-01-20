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

import com.heasy.knowroute.bean.FixedPointInfoBean;
import com.heasy.knowroute.bean.WebResponse;
import com.heasy.knowroute.service.FixedPointInfoService;
import com.heasy.knowroute.utils.JsonUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api(tags="定点属性信息管理")
@RestController
@RequestMapping("/fixedPointInfo")
public class FixedPointInfoController extends BaseController{
    private static final Logger logger = LoggerFactory.getLogger(FixedPointInfoController.class);
    
	@Autowired
	private FixedPointInfoService fixedPointInfoService;

	@ApiOperation(value="list", notes="获取某个用户某个类别下的所有定点信息")
	@ApiImplicitParams({
		@ApiImplicitParam(name="userId", paramType="path", required=true, dataType="Integer"),
		@ApiImplicitParam(name="categoryId", paramType="path", required=true, dataType="Integer")
	})
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

	@ApiOperation(value="saveOrUpdate", notes="添加或修改一条定点信息")
	@ApiImplicitParams({
		@ApiImplicitParam(name="bean", paramType="body", required=true, dataType="FixedPointInfoBean")
	})
	@RequestMapping(value="/saveOrUpdate", method=RequestMethod.POST, consumes="application/json")
	public WebResponse insert(@RequestBody FixedPointInfoBean bean) {
		if(bean.getId() > 0) {
			fixedPointInfoService.update(bean);
			return WebResponse.success(JsonUtil.toJSONString("id", String.valueOf(bean.getId())));
		}else {
			int id = fixedPointInfoService.insert(bean);
			if(id > 0) {
				return WebResponse.success(JsonUtil.toJSONString("id", String.valueOf(id)));
			}
		}
		return WebResponse.failure();
	}

	@ApiOperation(value="deleteById", notes="根据id删除定点信息")
	@ApiImplicitParams({
		@ApiImplicitParam(name="userId", paramType="path", required=true, dataType="Integer"),
		@ApiImplicitParam(name="id", paramType="path", required=true, dataType="Integer")
	})
	@RequestMapping(value="/deleteById/{userId}/{id}", method=RequestMethod.POST, consumes="application/json")
	public WebResponse deleteById(@PathVariable Integer userId, @PathVariable Integer id) {
		fixedPointInfoService.deleteById(userId, id);
		return WebResponse.success();
	}

	@ApiOperation(value="deleteByCategory", notes="根据类别删除定点信息")
	@ApiImplicitParams({
		@ApiImplicitParam(name="userId", paramType="path", required=true, dataType="Integer"),
		@ApiImplicitParam(name="categoryId", paramType="path", required=true, dataType="Integer")
	})
	@RequestMapping(value="/deleteByCategory/{userId}/{categoryId}", method=RequestMethod.POST, consumes="application/json")
	public WebResponse deleteByCategory(@PathVariable Integer userId, @PathVariable Integer categoryId) {
		fixedPointInfoService.deleteByCategory(userId, categoryId);
		return WebResponse.success();
	}

	@ApiOperation(value="deleteByCategory", notes="根据用户id删除定点信息")
	@ApiImplicitParams({
		@ApiImplicitParam(name="userId", paramType="path", required=true, dataType="Integer")
	})
	@RequestMapping(value="/deleteByUser/{userId}", method=RequestMethod.POST, consumes="application/json")
	public WebResponse deleteByUser(@PathVariable Integer userId) {
		fixedPointInfoService.deleteByUser(userId);
		return WebResponse.success();
	}
	
}
