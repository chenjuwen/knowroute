package com.heasy.knowroute.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.heasy.knowroute.bean.UserBean;
import com.heasy.knowroute.common.Constants;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
    private JdbcTemplate jdbcTemplate;
	
    @Override
    public String changePassword(String account, String oldPassword, String newPassword) {
        try {
            UserBean bean = getUser(account);
            if(bean == null) {
            	return "账号不存在！";
            }
            
            String password = bean.getPassword();
            if(!password.equals(oldPassword)) {
            	return "旧密码错误！";
            }
            
            String sql = "update users set password=? where id=?";
            jdbcTemplate.update(sql, newPassword, bean.getId());
            
            return Constants.SUCCESS;

        }catch (Exception ex){
            logger.error("", ex);
            return "修改密码出错！";
        }
    }

    @Override
    public UserBean getUser(String account) {
        try {
        	String sql = "select * from users where account=?";
        	
        	List<UserBean> list = jdbcTemplate.query(sql, new RowMapper<UserBean>() {
        		@Override
        		public UserBean mapRow(ResultSet rs, int rowNum) throws SQLException {
        			int id = rs.getInt("id");
        			String account = rs.getString("account");
        			String password = rs.getString("password");
        			String role = rs.getString("role");
        			
        			UserBean bean = new UserBean(id, account, password, role);
        			return bean;
        		}
        	}, account);
        	
        	if(!CollectionUtils.isEmpty(list)) {
        		return list.get(0);
        	}
        }catch (Exception ex){
            logger.error("", ex);
        }
        return null;
    }
}
