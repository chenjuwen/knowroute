package com.heasy.knowroute.service;

import java.util.Date;
import java.util.List;

import com.heasy.knowroute.bean.PointBean;
import com.heasy.knowroute.bean.PositionBean;

public interface PositionService {
	public boolean insert(PositionBean bean);
	public List<PointBean> getPoints(int userId, Date fromDate, Date toDate);
}
