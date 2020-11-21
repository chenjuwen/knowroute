package com.heasy.knowroute.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.heasy.knowroute.bean.VersionBean;
import com.heasy.knowroute.utils.DatetimeUtil;

@Service
public class VersionServiceImpl extends BaseService implements VersionService {
    private static final Logger logger = LoggerFactory.getLogger(VersionServiceImpl.class);
    
	@Override
	public VersionBean getLatestVersion() {
		try{
        	String sql = "select id,vname,vremark,vtime from versions order by vtime desc limit 1";
        	
        	List<VersionBean> list = jdbcTemplate.query(sql, new RowMapper<VersionBean>() {
        		@Override
        		public VersionBean mapRow(ResultSet rs, int rowNum) throws SQLException {
        			int id = rs.getInt("id");
        			String vname = rs.getString("vname");
        			String vremark = rs.getString("vremark");
        			Date vtime = DatetimeUtil.toDate(rs.getString("vtime"));
        			
        			VersionBean bean = new VersionBean();
        			bean.setId(id);
        			bean.setVname(vname);
        			bean.setVremark(vremark);
        			bean.setVtime(vtime);
        			
        			return bean;
        		}
        	});
        	
        	if(!CollectionUtils.isEmpty(list)) {
        		return list.get(0);
        	}
        	
        }catch (Exception ex){
            logger.error("", ex);
        }
        return null;
	}

}
