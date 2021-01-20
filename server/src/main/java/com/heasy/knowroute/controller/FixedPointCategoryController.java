package com.heasy.knowroute.controller;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.heasy.knowroute.bean.FixedPointCategoryBean;
import com.heasy.knowroute.bean.ResponseCode;
import com.heasy.knowroute.bean.WebResponse;
import com.heasy.knowroute.service.FixedPointCategoryService;
import com.heasy.knowroute.utils.JsonUtil;
import com.heasy.knowroute.utils.StringUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JsonConfig;

@Api(tags="定点类别管理")
@RestController
@RequestMapping("/fixedPointCategory")
public class FixedPointCategoryController extends BaseController{
	@Autowired
	private FixedPointCategoryService fixedPointCategoryService;

	@ApiOperation(value="list", notes="获取某个用户的所有定点类别信息")
	@ApiImplicitParams({
		@ApiImplicitParam(name="userId", paramType="path", required=true, dataType="Integer")
	})
	@RequestMapping(value="/list/{userId}", method=RequestMethod.GET)
    public WebResponse list(@PathVariable Integer userId) {
		List<FixedPointCategoryBean> list = fixedPointCategoryService.list(userId);
		if(CollectionUtils.isNotEmpty(list)) {
			JsonConfig jsonConfig = JsonUtil.getJsonConfigOfDate();
			jsonConfig.setExcludes(new String[] {"userId", "createDate"}); //忽略字段，不在json字符串中出现
			String data = JsonUtil.object2ArrayString(list, jsonConfig);
			return WebResponse.success(data);
		}else {
			return WebResponse.success("[]");
		}
    }

	@ApiOperation(value="insert", notes="添加一条定点类别信息")
	@ApiImplicitParams({
		@ApiImplicitParam(name="map", paramType="body", required=true, dataType="Map<String,String>")
	})
	@RequestMapping(value="/insert", method=RequestMethod.POST, consumes="application/json")
	public WebResponse insert(@RequestBody Map<String,String> map) {
		String userId = map.get("userId");
		String name = map.get("name");
		
		if(StringUtil.isEmpty(userId) || StringUtil.isEmpty(name)) {
			return WebResponse.failure(ResponseCode.PARAM_INVALID);
		}
		
		fixedPointCategoryService.insert(Integer.parseInt(userId), name);
		return WebResponse.success();
	}

	@ApiOperation(value="update", notes="更新一条定点类别信息")
	@ApiImplicitParams({
		@ApiImplicitParam(name="map", paramType="body", required=true, dataType="Map<String,String>")
	})
	@RequestMapping(value="/update", method=RequestMethod.POST, consumes="application/json")
	public WebResponse update(@RequestBody Map<String,String> map) {
		String id = map.get("id");
		String name = map.get("name");
		
		if(StringUtil.isEmpty(id) || StringUtil.isEmpty(name)) {
			return WebResponse.failure(ResponseCode.PARAM_INVALID);
		}
		
		fixedPointCategoryService.update(Integer.parseInt(id), name);
		return WebResponse.success();
	}

	@ApiOperation(value="delete", notes="删除一条定点类别信息")
	@ApiImplicitParams({
		@ApiImplicitParam(name="id", paramType="path", required=true, dataType="Integer")
	})
	@RequestMapping(value="/delete/{id}", method=RequestMethod.POST, consumes="application/json")
	public WebResponse delete(@PathVariable Integer id) {
		fixedPointCategoryService.delete(id);
		return WebResponse.success();
	}

	@ApiOperation(value="topping", notes="定点类别信息置顶")
	@ApiImplicitParams({
		@ApiImplicitParam(name="id", paramType="path", required=true, dataType="Integer")
	})
	@RequestMapping(value="/topping/{id}", method=RequestMethod.POST, consumes="application/json")
	public WebResponse topping(@PathVariable Integer id) {
		fixedPointCategoryService.topping(id);
		return WebResponse.success();
	}

	@ApiOperation(value="cancelTopping", notes="定点类别信息取消置顶")
	@ApiImplicitParams({
		@ApiImplicitParam(name="id", paramType="path", required=true, dataType="Integer")
	})
	@RequestMapping(value="/cancelTopping/{id}", method=RequestMethod.POST, consumes="application/json")
	public WebResponse cancelTopping(@PathVariable Integer id) {
		fixedPointCategoryService.cancelTopping(id);
		return WebResponse.success();
	}
	
}
