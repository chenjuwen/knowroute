package com.heasy.knowroute.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.heasy.knowroute.bean.ContactBean;
import com.heasy.knowroute.utils.DatetimeUtil;

@Service
public class ContactServiceImpl extends BaseService implements ContactService {
    private static final Logger logger = LoggerFactory.getLogger(ContactServiceImpl.class);

	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
	@Override
	public boolean save(ContactBean bean) {
		if(existsContact(bean)) {
			return false;
		}

		String date = DatetimeUtil.getToday(DatetimeUtil.DEFAULT_PATTERN_DT);
    	String sql = "insert into contacts(user_id,contact_name,contact_phone,update_date) values (?,?,?,?)";
    	jdbcTemplate.update(sql, bean.getUserId(), bean.getContactName(), bean.getContactPhone(), date);
    	
        return true;
	}

	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
	@Override
	public boolean update(ContactBean bean) {
		if(existsContact(bean)) {
			return false;
		}

		String date = DatetimeUtil.getToday(DatetimeUtil.DEFAULT_PATTERN_DT);
    	String sql = "update contacts set contact_name=?,contact_phone=?,update_date=? where id=?";
    	jdbcTemplate.update(sql, bean.getContactName(), bean.getContactPhone(), date, bean.getId());
        return true;
	}

	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
	@Override
	public void delete(int userId, int id) {
    	String sql = "delete from contacts where user_id=? and id=?";
    	jdbcTemplate.update(sql, userId, id);
	}

	@Override
	public ContactBean get(int id) {
		try{
        	String sql = "select * from contacts where id=?";
        	
        	List<ContactBean> list = jdbcTemplate.query(sql, new RowMapper<ContactBean>() {
        		@Override
        		public ContactBean mapRow(ResultSet rs, int rowNum) throws SQLException {
        			int id = rs.getInt("id");
        			int userId = rs.getInt("user_id");
        			String contactName = rs.getString("contact_name");
        			String contactPhone = rs.getString("contact_phone");
        			
        			ContactBean bean = new ContactBean();
        			bean.setId(id);
        			bean.setUserId(userId);
        			bean.setContactName(contactName);
        			bean.setContactPhone(contactPhone);
        			
        			return bean;
        		}
        	}, id);
        	
        	if(!CollectionUtils.isEmpty(list)) {
        		return list.get(0);
        	}
        }catch (Exception ex){
            logger.error("", ex);
        }
        return null;
	}

	@Override
	public List<ContactBean> list(int userId) {
		try{
        	String sql = "select * from contacts where user_id=? order by id desc limit 3";
        	
        	List<ContactBean> list = jdbcTemplate.query(sql, new RowMapper<ContactBean>() {
        		@Override
        		public ContactBean mapRow(ResultSet rs, int rowNum) throws SQLException {
        			int id = rs.getInt("id");
        			int userId = rs.getInt("user_id");
        			String contactName = rs.getString("contact_name");
        			String contactPhone = rs.getString("contact_phone");
        			
        			ContactBean bean = new ContactBean();
        			bean.setId(id);
        			bean.setUserId(userId);
        			bean.setContactName(contactName);
        			bean.setContactPhone(contactPhone);
        			
        			return bean;
        		}
        	}, userId);
        	
        	return list;
        }catch (Exception ex){
            logger.error("", ex);
        }
        return null;
	}

	@Override
	public boolean existsContact(ContactBean bean) {
		try{
			Map<String, Object> map = null;
			
        	String sql = "select count(id) as count from contacts where user_id=? and contact_phone=?";
        	if(bean.getId() <= 0) {
        		map = jdbcTemplate.queryForMap(sql, bean.getUserId(), bean.getContactPhone());
        	}else {
        		sql += " and id!=?";
        		map = jdbcTemplate.queryForMap(sql, bean.getUserId(), bean.getContactPhone(), bean.getId());
        	}
        	
        	if(map != null) {
        		Integer count = (Integer)map.get("count");
        		if(count > 0) {
        			return true;
        		}
        	}
        	return false;
        }catch (Exception ex){
            logger.error("", ex);
        }
        return false;
	}
}
