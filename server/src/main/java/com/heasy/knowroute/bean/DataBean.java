package com.heasy.knowroute.bean;

import java.util.List;

/**
 * Created by Administrator on 2020/9/27.
 */
public class DataBean{
	private int id;
    private String year;
    private String period;
    private String data;
    private List<String> dataList;
    private String times;

    public DataBean(){

    }

    public DataBean(String year, String period, String data, String times, List<String> dataList){
        this.year = year;
        this.period = period;
        this.data = data;
        this.times = times;
        this.dataList = dataList;
    }

    public DataBean(int id, String year, String period, String data, String times, List<String> dataList){
    	this.id = id;
        this.year = year;
        this.period = period;
        this.data = data;
        this.times = times;
        this.dataList = dataList;
    }

    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public List<String> getDataList() {
        return dataList;
    }

    public void setDataList(List<String> dataList) {
        this.dataList = dataList;
    }
}
