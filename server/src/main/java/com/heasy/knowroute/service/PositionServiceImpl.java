package com.heasy.knowroute.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.heasy.knowroute.bean.PointBean;
import com.heasy.knowroute.bean.PositionBean;
import com.heasy.knowroute.utils.DatetimeUtil;

@Service
public class PositionServiceImpl extends BaseService implements PositionService {
    private static final Logger logger = LoggerFactory.getLogger(PositionServiceImpl.class);

	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
	@Override
	public void insert(PositionBean bean) {
    	String date = DatetimeUtil.getToday(DatetimeUtil.DEFAULT_PATTERN_DT);
    	final String sql = "insert into positions(id,user_id,longitude,latitude,times) values(?,?,?,?,?)";
    	jdbcTemplate.update(sql, bean.getId(), bean.getUserId(), bean.getLongitude(), bean.getLatitude(), date);
	}

	@Override
	public List<PointBean> getPoints(int userId, Date startDate, Date endDate) {
		try{
			String startDateStr = DatetimeUtil.formatDate(startDate);
			String endDateStr = DatetimeUtil.formatDate(DatetimeUtil.add(endDate, Calendar.MINUTE, 1));
			
        	String sql = "select * from positions where user_id=? and times>=? and times<=? order by times asc";
        	
        	List<PointBean> list = jdbcTemplate.query(sql, new RowMapper<PointBean>() {
        		@Override
        		public PointBean mapRow(ResultSet rs, int rowNum) throws SQLException {
        			double longitude = rs.getDouble("longitude");
        			double latitude = rs.getDouble("latitude");
        			String times = rs.getString("times");
        			
        			PointBean bean = new PointBean(longitude, latitude, times);
        			return bean;
        		}
        	}, userId, startDateStr, endDateStr);
        	
        	return list;
        }catch (Exception ex){
            logger.error("", ex);
            return null;
        }
	}

}
