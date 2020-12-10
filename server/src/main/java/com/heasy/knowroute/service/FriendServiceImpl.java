package com.heasy.knowroute.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.heasy.knowroute.bean.FriendBean;
import com.heasy.knowroute.bean.MessageBean;
import com.heasy.knowroute.bean.UserBean;
import com.heasy.knowroute.common.EnumConstants;
import com.heasy.knowroute.utils.DatetimeUtil;
import com.heasy.knowroute.utils.StringUtil;

@Service
public class FriendServiceImpl extends BaseService implements FriendService {
    private static final Logger logger = LoggerFactory.getLogger(FriendServiceImpl.class);
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private MessageService messageService;
    
    /**
     * 检查好友的关联关系
     * @param userId 邀请者
     * @param phone 待邀请好友的手机号码
     */
	@Override
	public String checkFriend(int userId, String phone) {
		//无效的手机号码
		if(!StringUtil.isMobile(phone)) {
			return EnumConstants.FriendStatusCode.INVALID_PHONE.name();
		}
		
		UserBean userBean = userService.getUser(phone);
		
		//不是系统用户
		if(userBean == null) {
			MessageBean messageBean = messageService.getMessage(String.valueOf(userId), 
					phone, EnumConstants.MessageCategory.INVITE_FRIEND.name());
			if(messageBean != null) {
				return EnumConstants.FriendStatusCode.INVITED.name();
			}else {
				return EnumConstants.FriendStatusCode.NOT_INVITED.name();
			}
		}else {
			if(userBean.getId() == userId) {
				return EnumConstants.FriendStatusCode.SELF.name();
			}else {
				FriendBean friendBean = getFriend(userId, phone);
				if(friendBean != null) {
					return EnumConstants.FriendStatusCode.ALREADY_FRIEND.name();
				}else {
					MessageBean messageBean = messageService.getMessage(String.valueOf(userId), 
							phone, EnumConstants.MessageCategory.ADD_FRIEND.name());
					if(messageBean != null) {
						return EnumConstants.FriendStatusCode.NOT_FRIEND__ADDED.name();
					}else {
						return EnumConstants.FriendStatusCode.NOT_FRIEND__NOTADD.name();
					}
				}
			}
		}
	}
	
	/**
	 * 获取好友信息
	 * @param userId 邀请者
	 * @param phone 待邀请好友的手机号码
	 */
	@Override
	public FriendBean getFriend(int userId, String phone) {
		try{
			StringBuffer sb = new StringBuffer();
			sb.append(" select a.id,a.user_id,a.related_user_id,b.phone,a.nickname ");
			sb.append(" ,b.longitude,b.latitude,b.address,b.position_times ");
			sb.append(" from friends a left join users b on a.related_user_id=b.id ");
			sb.append(" where a.user_id=? and b.phone=? ");
        	
        	List<FriendBean> list = jdbcTemplate.query(sb.toString(), new FriendRowMapper(), userId, phone);
        	
        	if(!CollectionUtils.isEmpty(list)) {
        		return list.get(0);
        	}
        }catch (Exception ex){
            logger.error("", ex);
        }
        return null;
	}
	
	/**
	 * 好友列表
	 */
	@Override
	public List<FriendBean> getFriendList(int userId) {
		try{
			StringBuffer sb = new StringBuffer();
			sb.append(" select * from ");
			sb.append(" ( ");
			sb.append(" 	select 1 as type,0 as id,id as user_id,id as related_user_id,phone,'我自己' as nickname,longitude,latitude,address,position_times ");
			sb.append(" 	from users where id=? ");
			sb.append(" 	union ");
			sb.append(" 	select 2 as type,a.id,a.user_id,a.related_user_id,b.phone,a.nickname ");
			sb.append(" 	,b.longitude,b.latitude,b.address,b.position_times ");
			sb.append(" 	from friends a left join users b on a.related_user_id=b.id ");
			sb.append(" 	where a.user_id=? ");
			sb.append(" ) order by type asc ");
        	
        	List<FriendBean> list = jdbcTemplate.query(sb.toString(), new FriendRowMapper(), userId, userId);
        	
        	return list;
        	
        }catch (Exception ex){
            logger.error("", ex);
        }
        return null;
	}

	/**
	 * 添加好友
	 * @param userId 用户id
	 * @param phone 好友的手机号
	 */
	@Override
	public boolean insert(int userId, String phone) {
		try{
			FriendBean friendBean = getFriend(userId, phone);
			if(friendBean == null) {
				UserBean userBean = userService.getUser(phone); //好友
				if(userBean != null) {
					String sql = "insert into friends(user_id,related_user_id,nickname) values (?,?,?)";
		        	jdbcTemplate.update(sql, userId, userBean.getId(), userBean.getNickname());
		        	logger.info("添加好友关系： " + userId + " >> " + phone);
		            return true;
				}else {
					logger.warn("好友 " + phone + " 不在系统中");
				}
			}
        }catch (Exception ex){
            logger.error("", ex);
        }
        return false;
	}

	@Override
	public boolean updateNickname(int id, String newNickname) {
		try{
        	String sql = "update friends set nickname=? where id=?";
        	jdbcTemplate.update(sql, newNickname, id);
            return true;
        }catch (Exception ex){
            logger.error("", ex);
            return false;
        }
	}

	@Override
	public boolean delete(int id) {
		try{
        	String sql = "delete from friends where id=?";
        	jdbcTemplate.update(sql, id);
            return true;
        }catch (Exception ex){
            logger.error("", ex);
            return false;
        }
	}

	class FriendRowMapper implements RowMapper<FriendBean>{
		@Override
		public FriendBean mapRow(ResultSet rs, int rowNum) throws SQLException {
			int id = rs.getInt("id");
			int user_id = rs.getInt("user_id");
			int related_user_id = rs.getInt("related_user_id");
			String phone = rs.getString("phone");
			String nickname = rs.getString("nickname");
			double longitude = rs.getDouble("longitude");
			double latitude = rs.getDouble("latitude");
			String address = rs.getString("address");
			String position_times = DatetimeUtil.formatDate(DatetimeUtil.toDate(rs.getString("position_times")));
			
			FriendBean bean = new FriendBean();
			bean.setId(id);
			bean.setUserId(user_id);
			bean.setRelatedUserId(related_user_id);
			bean.setPhone(phone);
			bean.setNickname(nickname);
			bean.setLongitude(longitude);
			bean.setLatitude(latitude);
			bean.setAddress(address);
			bean.setPositionTimes(position_times);
			
			return bean;
		}
	}
	
}
