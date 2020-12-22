package com.heasy.knowroute.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.heasy.knowroute.bean.FixedPointCategoryBean;
import com.heasy.knowroute.utils.DatetimeUtil;

@Service
public class FixedPointCategoryServiceImpl extends BaseService implements FixedPointCategoryService {
    private static final Logger logger = LoggerFactory.getLogger(FixedPointCategoryServiceImpl.class);
    
	@Override
	public List<FixedPointCategoryBean> list(int userId) {
		try{
        	String sql = "select * from fixed_point_categorys where user_id=? order by topping desc,create_date desc";
        	List<FixedPointCategoryBean> list = jdbcTemplate.query(sql, new RowMapperImpl(), userId);
        	return list;
        }catch (Exception ex){
            logger.error("", ex);
        }
        return null;
	}

	@Override
	public boolean insert(int userId, String name) {
		try{
    		String date = DatetimeUtil.getToday(DatetimeUtil.DEFAULT_PATTERN_DT);
        	String sql = "insert into fixed_point_categorys(user_id,name,create_date) values (?,?,?)";
        	jdbcTemplate.update(sql, userId, name, date);
            return true;
        }catch (Exception ex){
            logger.error("", ex);
            return false;
        }
	}

	@Override
	public boolean update(int id, String name) {
		try{
        	String sql = "update fixed_point_categorys set name=? where id=?";
        	jdbcTemplate.update(sql, name, id);
            return true;
        }catch (Exception ex){
            logger.error("", ex);
            return false;
        }
	}

	@Override
	public boolean delete(int id) {
		try{
        	String sql = "delete from fixed_point_categorys where id=?";
        	jdbcTemplate.update(sql, id);
            return true;
        }catch (Exception ex){
            logger.error("", ex);
            return false;
        }
	}

	@Override
	public boolean topping(int id) {
		try{
    		String date = DatetimeUtil.getToday(DatetimeUtil.DEFAULT_PATTERN_DT);
        	String sql = "update fixed_point_categorys set topping=1,create_date=? where id=?";
        	jdbcTemplate.update(sql, date, id);
            return true;
        }catch (Exception ex){
            logger.error("", ex);
            return false;
        }
	}

	@Override
	public boolean cancelTopping(int id) {
		try{
        	String sql = "update fixed_point_categorys set topping=0 where id=?";
        	jdbcTemplate.update(sql, id);
            return true;
        }catch (Exception ex){
            logger.error("", ex);
            return false;
        }
	}

	class RowMapperImpl implements RowMapper<FixedPointCategoryBean>{
		@Override
		public FixedPointCategoryBean mapRow(ResultSet rs, int rowNum) throws SQLException {
			int id = rs.getInt("id");
			int user_id = rs.getInt("user_id");
			String name = rs.getString("name");
			int topping = rs.getInt("topping");
			Date create_date = DatetimeUtil.toDate(rs.getString("create_date"));
			
			FixedPointCategoryBean bean = new FixedPointCategoryBean();
			bean.setId(id);
			bean.setUserId(user_id);
			bean.setName(name);
			bean.setTopping(topping);
			bean.setCreateDate(create_date);
			
			return bean;
		}
	}
	
}
