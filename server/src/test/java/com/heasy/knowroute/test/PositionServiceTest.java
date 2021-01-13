package com.heasy.knowroute.test;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.heasy.Main;
import com.heasy.knowroute.bean.PointBean;
import com.heasy.knowroute.bean.PositionBean;
import com.heasy.knowroute.service.PositionService;
import com.heasy.knowroute.utils.DatetimeUtil;
import com.heasy.knowroute.utils.StringUtil;

import net.sf.json.JSONArray;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Main.class)
public class PositionServiceTest {
	@Autowired
    private PositionService positionService;

	@Test
	public void insert() {
		PositionBean bean = new PositionBean();
		bean.setId(StringUtil.getUUIDString());
		bean.setUserId(5);
		bean.setLongitude(111.563081);
		bean.setLatitude(22.617776);
		bean.setTimes(DatetimeUtil.nowDate());
		
		positionService.insert(bean);
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
}
