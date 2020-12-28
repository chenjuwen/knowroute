package com.heasy.knowroute.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import com.heasy.knowroute.bean.FixedPointInfoBean;

@Service
public class FixedPointInfoServiceImpl extends BaseService implements FixedPointInfoService {
    private static final Logger logger = LoggerFactory.getLogger(FixedPointInfoServiceImpl.class);

	@Override
	public List<FixedPointInfoBean> list(int userId, int categoryId) {
		try{
        	String sql = "select * from fixed_point_infos where user_id=? and category_id=?";
        	List<FixedPointInfoBean> list = jdbcTemplate.query(sql, new DefaultRowMapper(), userId, categoryId);
        	return list;
        }catch (Exception ex){
            logger.error("", ex);
        }
        return null;
	}

	@Override
	public int insert(final FixedPointInfoBean bean) {
		try{
    		KeyHolder keyHolder = new GeneratedKeyHolder(); 
    		
    		final String sql = "insert into fixed_point_infos(user_id,category_id,longitude,latitude,address,comments,label) values (?,?,?,?,?,?,?)";
        	jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
					PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
					ps.setInt(1, bean.getUserId());
					ps.setInt(2, bean.getCategoryId());
					ps.setDouble(3, bean.getLongitude());
					ps.setDouble(4, bean.getLatitude());
					ps.setString(5, bean.getAddress());
					ps.setString(6, bean.getComments());
					ps.setString(7, bean.getLabel());
					return ps;
				}
			}, keyHolder);
        	
        	int id = keyHolder.getKey().intValue();
    		logger.info("新的id为 " + id);
        	return id;
        }catch (Exception ex){
            logger.error("", ex);
            return 0;
        }
	}

	@Override
	public boolean update(FixedPointInfoBean bean) {
		try{
        	String sql = "update fixed_point_infos set address=?,comments=?,label=? where id=? and user_id=?";
        	jdbcTemplate.update(sql, bean.getAddress(), bean.getComments(), bean.getLabel(), bean.getId(), bean.getUserId());
            return true;
        }catch (Exception ex){
            logger.error("", ex);
            return false;
        }
	}

	@Override
	public boolean deleteById(int userId, int id) {
		try{
        	String sql = "delete from fixed_point_infos where id=? and user_id=?";
        	jdbcTemplate.update(sql, id, userId);
            return true;

        }catch (Exception ex){
            logger.error("", ex);
            return false;
        }
	}

	@Override
	public boolean deleteByCategory(int userId, int categoryId) {
		try{
        	String sql = "delete from fixed_point_infos where category_id=? and user_id=?";
        	jdbcTemplate.update(sql, categoryId, userId);
            return true;

        }catch (Exception ex){
            logger.error("", ex);
            return false;
        }
	}

	@Override
	public boolean deleteByUser(int userId) {
		try{
        	String sql = "delete from fixed_point_infos where user_id=?";
        	jdbcTemplate.update(sql, userId);
            return true;

        }catch (Exception ex){
            logger.error("", ex);
            return false;
        }
	}
    
	class DefaultRowMapper implements RowMapper<FixedPointInfoBean>{
		@Override
		public FixedPointInfoBean mapRow(ResultSet rs, int rowNum) throws SQLException {
			FixedPointInfoBean bean = new FixedPointInfoBean();
			bean.setId(rs.getInt("id"));
			bean.setUserId(rs.getInt("user_id"));
			bean.setCategoryId(rs.getInt("category_id"));
			bean.setLongitude(rs.getDouble("longitude"));
			bean.setLatitude(rs.getDouble("latitude"));
			bean.setAddress(rs.getString("address"));
			bean.setComments(rs.getString("comments"));
			bean.setLabel(rs.getString("label"));
			return bean;
		}
	}
}
