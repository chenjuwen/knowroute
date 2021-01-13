package com.heasy.knowroute.service;

import java.util.List;

import com.heasy.knowroute.bean.FixedPointCategoryBean;

public interface FixedPointCategoryService {
	public List<FixedPointCategoryBean> list(int userId);
	public void insert(int userId, String name);
	public void update(int id, String name);
	public void delete(int id);
	public void topping(int id);
	public void cancelTopping(int id);
}
