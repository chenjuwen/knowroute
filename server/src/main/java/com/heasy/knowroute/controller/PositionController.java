package com.heasy.knowroute.controller;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.heasy.knowroute.api.ResponseCode;
import com.heasy.knowroute.api.WebResponse;
import com.heasy.knowroute.bean.PointBean;
import com.heasy.knowroute.bean.PositionBean;
import com.heasy.knowroute.service.PositionService;

import net.sf.json.JSONArray;

@RestController
@RequestMapping("/position")
public class PositionController extends BaseController{
	@Autowired
	private PositionService positionService;
	
	@RequestMapping(value="/insert", method=RequestMethod.POST, consumes="application/json")
	public WebResponse insert(@RequestBody PositionBean positionBean){
		boolean b = positionService.insert(positionBean);
		if(b) {
			return WebResponse.success();
		}else {
			return WebResponse.failure(ResponseCode.FAILURE);
		}
	}
	
	@RequestMapping(value="/getPoints", method=RequestMethod.GET)
    public WebResponse getPoints(@RequestParam(value="userId") Integer userId,
    		@RequestParam(value="fromDate") Date fromDate,
    		@RequestParam(value="toDate") Date toDate) {
		List<PointBean> list = positionService.getPoints(userId, fromDate, toDate);
		if(!CollectionUtils.isEmpty(list)) {
			return WebResponse.success(JSONArray.fromObject(list).toString(2));
		}else {
			return WebResponse.success("[]");
		}
    }
	
}
