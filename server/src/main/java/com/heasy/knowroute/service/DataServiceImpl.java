package com.heasy.knowroute.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.heasy.knowroute.bean.DataBean;
import com.heasy.knowroute.utils.DatetimeUtil;

@Service
public class DataServiceImpl implements DataService {
    private static final Logger logger = LoggerFactory.getLogger(DataServiceImpl.class);

	@Autowired
    private JdbcTemplate jdbcTemplate;
	
    @Override
    public List<DataBean> getAllData() {
        try{
        	String sql = "select * from lottery_data order by year asc,period asc";
        	
        	List<DataBean> list = jdbcTemplate.query(sql, new RowMapper<DataBean>() {
        		@Override
        		public DataBean mapRow(ResultSet rs, int rowNum) throws SQLException {
        			int id = rs.getInt("id");
        			String year = rs.getString("year");
        			String period = rs.getString("period");
        			String data = rs.getString("data");
        			String times = rs.getString("times");
        			List<String> dataList = Arrays.asList(data.split(","));
        			
        			DataBean bean = new DataBean(id, year, period, data, times, dataList);
        			return bean;
        		}
        	});
        	
        	return list;
        }catch (Exception ex){
            logger.error("", ex);
        }
        return null;
    }

    @Override
    public DataBean getByPeriod(String period) {
        try{
        	String sql = "select * from lottery_data where period=?";
        	
        	List<DataBean> list = jdbcTemplate.query(sql, new RowMapper<DataBean>() {
        		@Override
        		public DataBean mapRow(ResultSet rs, int rowNum) throws SQLException {
        			int id = rs.getInt("id");
        			String year = rs.getString("year");
        			String period = rs.getString("period");
        			String data = rs.getString("data");
        			String times = rs.getString("times");
        			List<String> dataList = Arrays.asList(data.split(","));
        			
        			DataBean bean = new DataBean(id, year, period, data, times, dataList);
        			return bean;
        		}
        	}, period);
        	
        	if(!CollectionUtils.isEmpty(list)) {
        		return list.get(0);
        	}
        }catch (Exception ex){
            logger.error("", ex);
        }
        return null;
    }

    @Override
    public boolean deleteByPeriod(String period) {
        try{
        	String sql = "delete from lottery_data where period=?";
        	jdbcTemplate.update(sql, period);
            return true;

        }catch (Exception ex){
            logger.error("", ex);
            return false;
        }
    }

    @Override
    public boolean add(String period, String data) {
        try{
        	String year = String.valueOf(DatetimeUtil.getCalendar().get(Calendar.YEAR));
        	String times = DatetimeUtil.getToday(DatetimeUtil.DEFAULT_PATTERN_DT);
        	
        	String sql = "insert into lottery_data(year,period,data,times) values (?,?,?,?)";
        	jdbcTemplate.update(sql, year, period, data, times);
            return true;

        }catch (Exception ex){
            logger.error("", ex);
            return false;
        }
    }

    @Override
    public boolean update(String period, String data) {
        try{
        	String sql = "update lottery_data set data=? where period=?";
        	jdbcTemplate.update(sql, data, period);
            return true;

        }catch (Exception ex){
            logger.error("", ex);
            return false;
        }
    }
}