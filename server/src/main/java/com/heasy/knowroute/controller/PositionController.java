package com.heasy.knowroute.controller;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
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
import com.heasy.knowroute.service.UserService;
import com.heasy.knowroute.utils.DatetimeUtil;

import net.sf.json.JSONArray;

@RestController
@RequestMapping("/position")
public class PositionController extends BaseController{
    private static final Logger logger = LoggerFactory.getLogger(PositionController.class);
    
	@Autowired
	private PositionService positionService;

    @Autowired
    private UserService userService;
    
    /**
     * 处理以form-data格式提交的日期字段
     */
//    @InitBinder
//    public void initDateFormate(WebDataBinder dataBinder) {
//        dataBinder.addCustomFormatter(new DateFormatter(DatetimeUtil.DEFAULT_PATTERN_DT));
//    }
    
	@RequestMapping(value="/insert", method=RequestMethod.POST, consumes="application/json")
	public WebResponse insert(@RequestBody PositionBean positionBean){
		try {
			//更新用户的位置信息
			userService.updatePositionInfo(positionBean.getUserId(), 
					positionBean.getLongitude(), positionBean.getLatitude(), 
					positionBean.getAddress());
			
			boolean b = positionService.insert(positionBean);
			if(b) {
				return WebResponse.success();
			}
		}catch(Exception ex) {
			logger.error("", ex);
		}
		
		return WebResponse.failure(ResponseCode.FAILURE);
	}
	
	@RequestMapping(value="/getPoints", method=RequestMethod.GET)
    public WebResponse getPoints(@RequestParam(value="userId") Integer userId,
    		@RequestParam(value="fromDate") Date fromDate,
    		@RequestParam(value="toDate") Date toDate) {
		try {
			List<PointBean> list = positionService.getPoints(userId, fromDate, toDate);
			if(!CollectionUtils.isEmpty(list)) {
				return WebResponse.success(JSONArray.fromObject(list).toString(2));
			}
		}catch(Exception ex) {
			logger.error("", ex);
		}
		
		return WebResponse.success("[]");
    }
	
}
