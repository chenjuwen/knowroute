package com.heasy.knowroute.service;

import java.util.List;

import com.heasy.knowroute.bean.DataBean;

/**
 * Created by Administrator on 2020/9/26.
 */
public interface DataService{
    List<DataBean> getAllData();
    DataBean getByPeriod(String period);
    boolean deleteByPeriod(String period);
    boolean add(String period, String data);
    boolean update(String period, String data);
}
