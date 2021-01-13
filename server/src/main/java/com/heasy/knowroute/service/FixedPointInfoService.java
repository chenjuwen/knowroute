package com.heasy.knowroute.service;

import java.util.List;

import com.heasy.knowroute.bean.FixedPointInfoBean;

public interface FixedPointInfoService{
	List<FixedPointInfoBean> list(int userId, int categoryId);
	int insert(FixedPointInfoBean bean);
	void update(FixedPointInfoBean bean);
	void deleteById(int userId, int id);
	void deleteByCategory(int userId, int categoryId);
	void deleteByUser(int userId);
}
