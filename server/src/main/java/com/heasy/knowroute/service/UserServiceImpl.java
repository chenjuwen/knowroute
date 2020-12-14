package com.heasy.knowroute.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import com.heasy.knowroute.bean.MessageBean;
import com.heasy.knowroute.bean.UserBean;
import com.heasy.knowroute.utils.DatetimeUtil;
import com.heasy.knowroute.utils.StringUtil;

@Service
public class UserServiceImpl extends BaseService implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private MessageService messageService;
    @Autowired
    private FriendService friendService;
    
	/**
     * 登录处理：
     * 	手机号对应的用户记录不存在，则添加新用户
     * 	返回用户记录的id值，如果返回0则表示登录出错
     */
    @Override
    public int login(String phone) {
        try {
        	UserBean userBean = getUserByPhone(phone);
        	if(userBean == null) {
        		//新用户注册
        		String inviteCode = StringUtil.getUUIDString(); //邀请码
        		int id = insert(phone, inviteCode);
        		
        		if(id > 0) {
            		List<MessageBean> msgList = messageService.getInviteAgreeMessage(phone);
            		//此用户为同意邀请的新用户，且多人邀请了TA
            		if(CollectionUtils.isNotEmpty(msgList)){
            			msgList.stream().forEach(bean -> {
            				UserBean inviter = getUserById(Integer.parseInt(bean.getSender()));
                			if(inviter != null) {
                    			//添加好友关系，双向的
                    			friendService.insert(Integer.parseInt(bean.getSender()), phone);
                    			friendService.insert(id, inviter.getPhone());
                			}
            			});
            		}
        		}
        		
        		return id;
        	}else {
        		//老用户更新登录时间
        		updateLastLoginDate(userBean.getId());
        	}
            
        	return userBean.getId();
        	
        }catch(Exception ex){
            logger.error("", ex);
            return 0;
        }
    }
    
	/**
	 * 根据id获取用户信息
	 */
	@Override
	public UserBean getUserById(int id) {
		try {
        	String sql = "select * from users where id=?";
        	List<UserBean> list = jdbcTemplate.query(sql, new UserRowMapper(), id);
        	if(!CollectionUtils.isEmpty(list)) {
        		return list.get(0);
        	}
        }catch (Exception ex){
            logger.error("", ex);
        }
        return null;
	}
	
	/**
	 * 根据手机号获取用户信息
	 */
    @Override
    public UserBean getUserByPhone(String phone) {
        try {
        	String sql = "select * from users where phone=?";
        	List<UserBean> list = jdbcTemplate.query(sql, new UserRowMapper(), phone);
        	if(!CollectionUtils.isEmpty(list)) {
        		return list.get(0);
        	}
        }catch (Exception ex){
            logger.error("", ex);
        }
        return null;
    }
   
    /**
     * 添加用户记录，并返回记录的id值
     * 
     * @param phone 手机号
     * @param inviteCode 邀请码
     * 
     * @return 记录的id值，0表示错误，大于0表示真实的id值
     */
    @Override
    public int insert(String phone, String inviteCode) {
    	UserBean bean = getUserByPhone(phone);
    	if(bean != null) {
    		logger.warn("用户 " + phone + " 已存在");
    		return 0;
    	}
    	
    	try {
    		String date = DatetimeUtil.getToday(DatetimeUtil.DEFAULT_PATTERN_DT);
    		
    		final String sql = "insert into users(phone,nickname,invite_code,create_date,last_login_date) values(?,?,?,?,?)";
    		
    		KeyHolder keyHolder = new GeneratedKeyHolder(); 
    		
    		jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
					PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
					ps.setString(1, phone);
					ps.setString(2, phone.substring(0, 3) + "****" + phone.substring(7));
					ps.setString(3, inviteCode);
					ps.setString(4, date);
					ps.setString(5, date);
					return ps;
				}
			}, keyHolder);
    		
    		int id = keyHolder.getKey().intValue();
    		logger.info("新用户的id为 " + id);
        	return id;
    	}catch(Exception ex) {
    		logger.error("", ex);
    		return 0;
    	}
    }
    
    /**
     * 更新用户的昵称
     */
    @Override
    public boolean updateNickname(int id, String newNickname) {
    	try {
	    	String sql = "update users set nickname=? where id=?";
	    	int count = jdbcTemplate.update(sql, newNickname, id);
	    	return count > 0;
    	}catch(Exception ex) {
    		logger.error("", ex);
    		return false;
    	}
    }
    
    /**
     * 更新用户的最后登录时间
     */
    @Override
    public boolean updateLastLoginDate(int id) {
    	try {
    		String date = DatetimeUtil.getToday(DatetimeUtil.DEFAULT_PATTERN_DT);
	    	String sql = "update users set last_login_date=? where id=?";
	    	int count = jdbcTemplate.update(sql, date, id);
	    	return count > 0;
    	}catch(Exception ex) {
    		logger.error("", ex);
    		return false;
    	}
    }
    
    /**
     * 更新用户的位置信息
     */
    @Override
    public boolean updatePositionInfo(int id, double longitude, double latitude, String address, Date positionTimes) {
    	try {
    		if(StringUtil.isNotEmpty(address)) {
    			String times = DatetimeUtil.formatDate(positionTimes);
		    	String sql = "update users set longitude=?,latitude=?,address=?,position_times=? where id=?";
		    	int count = jdbcTemplate.update(sql, longitude, latitude, address, times, id);
		    	return count > 0;
    		}
		}catch(Exception ex) {
			logger.error("", ex);
		}
		return false;
    }
    
    class UserRowMapper implements RowMapper<UserBean>{
    	@Override
    	public UserBean mapRow(ResultSet rs, int rowNum) throws SQLException {
    		int id = rs.getInt("id");
			String phone = rs.getString("phone");
			String nickname = rs.getString("nickname");
			Double longitude = rs.getDouble("longitude");
			Double latitude = rs.getDouble("latitude");
			String address = rs.getString("address");
			Date position_times = DatetimeUtil.toDate(rs.getString("position_times"));
			String invite_code = rs.getString("invite_code");
			Date create_date = DatetimeUtil.toDate(rs.getString("create_date"));
			Date last_login_date = DatetimeUtil.toDate(rs.getString("last_login_date"));
			
			UserBean bean = new UserBean();
			bean.setId(id);
			bean.setPhone(phone);
			bean.setNickname(nickname);
			bean.setLongitude(longitude);
			bean.setLatitude(latitude);
			bean.setAddress(address);
			bean.setPositionTimes(position_times);
			bean.setInviteCode(invite_code);
			bean.setCreateDate(create_date);
			bean.setLastLoginDate(last_login_date);
			
			return bean;
    	}
    }
    
    @Override
    public boolean cancelAccount(int id, String phone) {
    	try {
    		String sql = "delete from contacts where user_id=?";
    		jdbcTemplate.update(sql, id);
    		
    		sql = "delete from friends where user_id=? or related_user_id=?";
    		jdbcTemplate.update(sql, id, id);
    		
    		sql = "delete from messages where sender=? or owner=? or receiver=?";
    		jdbcTemplate.update(sql, id, id, phone);
    		
    		sql = "delete from positions where user_id=?";
    		jdbcTemplate.update(sql, id);
    		
    		sql = "delete from users where id=?";
    		jdbcTemplate.update(sql, id);
    		
    		return true;
		}catch(Exception ex) {
			logger.error("", ex);
		}
		return false;
    }
}
