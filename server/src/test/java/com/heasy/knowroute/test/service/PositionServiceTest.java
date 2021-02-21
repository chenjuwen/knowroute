package com.heasy.knowroute.test.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.heasy.knowroute.bean.PointBean;
import com.heasy.knowroute.bean.PositionBean;
import com.heasy.knowroute.service.PositionService;
import com.heasy.knowroute.utils.DatetimeUtil;
import com.heasy.knowroute.utils.StringUtil;

import net.sf.json.JSONArray;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PositionServiceTest {
	@Autowired
    private PositionService positionService;

	@Test
	public void insert() {
		List<PositionBean> dataList = new ArrayList<>();
		
		PositionBean bean = new PositionBean();
		bean.setId(StringUtil.getUUIDString());
		bean.setUserId(5);
		bean.setLongitude(111.563081);
		bean.setLatitude(22.617776);
		bean.setTimes(DatetimeUtil.nowDate());
		
		dataList.add(bean);
		positionService.insert(dataList);
	}
	
	@Test
	public void getPoints(){
		Date fromDate = DatetimeUtil.toDate(DatetimeUtil.add(Calendar.DAY_OF_MONTH, -30)); //30日前
		Date toDate = DatetimeUtil.nowDate();
		List<PointBean> list = positionService.getPoints(5, fromDate, toDate);
		if(!CollectionUtils.isEmpty(list)) {
			System.out.println(JSONArray.fromObject(list).toString(2));
		}
	}
	
	@Test
	public void cleanup() {
		positionService.cleanup(5, 0);
	}
}
