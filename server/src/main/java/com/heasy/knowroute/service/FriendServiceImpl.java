package com.heasy.knowroute.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
		try {
			//无效的手机号码
			if(!StringUtil.isMobile(phone)) {
				return EnumConstants.FriendStatusCode.INVALID_PHONE.name();
			}
			
			UserBean userBean = userService.getUserByPhone(phone);
			
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
		}catch(Exception ex) {
			logger.error("", ex);
			return EnumConstants.FriendStatusCode.UNKNOW.name();
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
			sb.append(" ,b.longitude,b.latitude,b.address,b.position_times,a.forbid_look_trace ");
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
	
	@Override
	public FriendBean getFriend(int id) {
		try{
			StringBuffer sb = new StringBuffer();
			sb.append(" select a.id,a.user_id,a.related_user_id,b.phone,a.nickname ");
			sb.append(" ,b.longitude,b.latitude,b.address,b.position_times,a.forbid_look_trace ");
			sb.append(" from friends a left join users b on a.related_user_id=b.id ");
			sb.append(" where a.id=? ");
        	
        	List<FriendBean> list = jdbcTemplate.query(sb.toString(), new FriendRowMapper(), id);
        	
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
			sb.append(" 	select 1 as type,0 as id,id as user_id,id as related_user_id,phone,'我自己' as nickname,longitude,latitude,address,position_times,0 as forbid_look_trace ");
			sb.append(" 	from users where id=? ");
			sb.append(" 	union ");
			sb.append(" 	select 2 as type,a.id,a.user_id,a.related_user_id,b.phone,a.nickname ");
			sb.append(" 	,b.longitude,b.latitude,b.address,b.position_times,a.forbid_look_trace ");
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

	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
	@Override
	public boolean confirmAdd(int messageId, String pass) {
		MessageBean messageBean = messageService.getMessage(messageId);
		if(messageBean != null) {
			if(pass.equalsIgnoreCase("yes")) {
				messageService.confirmMessage(messageId, "已同意");
				
				//添加好友关系
				UserBean receiverUser = userService.getUserByPhone(messageBean.getReceiver());
				addFriendRelationship(Integer.parseInt(messageBean.getSender()), messageBean.getReceiver(), 
						receiverUser.getId(), messageBean.getSenderPhone());
				
				//添加站内消息
				MessageBean bean = new MessageBean();
				bean.setTitle("好友提醒");
				bean.setContent(messageBean.getReceiver() + "同意了您的好友邀请");
				bean.setCategory(EnumConstants.MessageCategory.GENERAL.name());
				bean.setCreateDate(DatetimeUtil.nowDate());
				bean.setOwner(new Integer(messageBean.getSender()));
				bean.setStatus(1);
				messageService.insert(bean);
				
			} else {
				messageService.confirmMessage(messageId, "已忽略");
				
				//添加站内消息
				MessageBean bean = new MessageBean();
				bean.setTitle("好友提醒");
				bean.setContent(messageBean.getReceiver() + "拒绝了您的好友邀请");
				bean.setCategory(EnumConstants.MessageCategory.GENERAL.name());
				bean.setCreateDate(DatetimeUtil.nowDate());
				bean.setOwner(new Integer(messageBean.getSender()));
				bean.setStatus(1);
				messageService.insert(bean);
			}
			return true;
		}else {
			return false;
		}
	}

	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
	@Override
	public void addFriendRelationship(int aUserId, String bPhone, int bUserId, String aPhone) {
		insert(aUserId, bPhone);
		insert(bUserId, aPhone);
	}

	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
	/**
	 * 添加好友
	 * @param userId 用户id
	 * @param phone 好友的手机号
	 */
	@Override
	public void insert(int userId, String phone) {
		FriendBean friendBean = getFriend(userId, phone);
		if(friendBean == null) {
			UserBean friendUserBean = userService.getUserByPhone(phone); //好友
			if(friendUserBean != null) {
				String sql = "insert into friends(user_id,related_user_id,nickname) values (?,?,?)";
	        	jdbcTemplate.update(sql, userId, friendUserBean.getId(), friendUserBean.getNickname());
	        	logger.info("添加好友关系： " + userId + " >> " + phone);
			}else {
				logger.warn("好友 " + phone + " 不在系统中");
			}
		}
	}

	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
	@Override
	public void updateNickname(int id, String newNickname) {
    	String sql = "update friends set nickname=? where id=?";
    	jdbcTemplate.update(sql, newNickname, id);
	}

	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
	@Override
	public void forbidLookTrace(int id, int traceFlag) {
		String sql = "update friends set forbid_look_trace=? where id=?";
    	jdbcTemplate.update(sql, traceFlag, id);
	}

	/**
	 * 是否禁止好友查看轨迹
	 * viewTrackUserId想看userId的轨迹
	 */
	@Override
	public boolean checkForbid(int userId, int viewTrackUserId) {
		try{
			StringBuffer sb = new StringBuffer();
			sb.append("select forbid_look_trace from friends where user_id=? and related_user_id=?");
			Map<String, Object> map = jdbcTemplate.queryForMap(sb.toString(), userId, viewTrackUserId);
			if(map != null) {
				int forbid_look_trace = (Integer)map.get("forbid_look_trace");
				if(forbid_look_trace == 1) {
					return true;
				}
			}
	    }catch (Exception ex){
	        logger.error("", ex);
	    }
		return false;
	}

	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
	/**
	 * 删除好友关系
	 */
	@Override
	public boolean delete(int id) {
		FriendBean bean = getFriend(id);
		if(bean == null) {
			return false;
		}
		
		int userId = bean.getUserId();
		int relatedUserId = bean.getRelatedUserId();
		
    	String sql = "delete from friends where user_id=? and related_user_id=?";
    	jdbcTemplate.update(sql, userId, relatedUserId);
    	
    	sql = "delete from friends where user_id=? and related_user_id=?";
    	jdbcTemplate.update(sql, relatedUserId, userId);
    	
        return true;
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
			int forbid_look_trace = rs.getInt("forbid_look_trace");
			
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
			bean.setForbidLookTrace(forbid_look_trace);
			
			return bean;
		}
	}
	
}
