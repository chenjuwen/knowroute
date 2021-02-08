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
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.heasy.knowroute.bean.MessageBean;
import com.heasy.knowroute.utils.DatetimeUtil;
import com.heasy.knowroute.utils.StringUtil;

@Service
public class MessageServiceImpl extends BaseService implements MessageService {
    private static final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

    @Override
    public List<MessageBean> getMessageList(int owner) {
    	try{
			String sql = "select * from messages where owner=? order by update_date desc";
        	List<MessageBean> list = jdbcTemplate.query(sql, new MessageRowMapper(), owner);
        	return list;
        }catch (Exception ex){
            logger.error("", ex);
        }
        return null;
    }
    
    @Override
    public MessageBean getMessage(int id) {
    	try{
			String sql = "select * from messages where id=?";
        	List<MessageBean> list = jdbcTemplate.query(sql, new MessageRowMapper(), id);
        	if(!CollectionUtils.isEmpty(list)) {
        		return list.get(0);
        	}
        }catch (Exception ex){
            logger.error("", ex);
        }
        return null;
    }
    
	@Override
	public MessageBean getMessage(String sender, String receiver, String category) {
		try{
			String sql = "select * from messages where status=0 and sender=? and receiver=? and category=? order by update_date desc";
        	List<MessageBean> list = jdbcTemplate.query(sql, new MessageRowMapper(), sender, receiver, category);
        	if(!CollectionUtils.isEmpty(list)) {
        		return list.get(0);
        	}
        }catch (Exception ex){
            logger.error("", ex);
        }
        return null;
	}
	
	/**
	 * 获取同意好友邀请的消息记录
	 */
	@Override
	public List<MessageBean> getInviteAgreeMessage(String phone) {
		try{
			String sql = "select * from messages where result='agree' and status=1 and category='INVITE_FRIEND' and receiver=?";
        	List<MessageBean> list = jdbcTemplate.query(sql, new MessageRowMapper(), phone);
        	return list;
        }catch (Exception ex){
            logger.error("", ex);
        }
        return null;
	}

	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
	/**
	 * @return 记录的id值，0表示错误，大于0表示真实的id值
	 */
	@Override
	public int insert(MessageBean bean) {
		String date = DatetimeUtil.getToday(DatetimeUtil.DEFAULT_PATTERN_DT);
		final String sql = "insert into messages(title,content,category,result,sender,sender_nickname,sender_phone,receiver,create_date,status,owner,update_date) values(?,?,?,?,?,?,?,?,?,?,?,?)";
		
		KeyHolder keyHolder = new GeneratedKeyHolder(); 
		
		jdbcTemplate.update(new PreparedStatementCreator(){
			@Override
			public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
				PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, bean.getTitle());
				ps.setString(2, bean.getContent());
				ps.setString(3, bean.getCategory());
				ps.setString(4, StringUtil.trimToEmpty(bean.getResult()));
				ps.setString(5, bean.getSender());
				ps.setString(6, bean.getSenderNickname());
				ps.setString(7, bean.getSenderPhone());
				ps.setString(8, bean.getReceiver());
				ps.setString(9, date);
				ps.setInt(10, bean.getStatus());
				ps.setInt(11, bean.getOwner()==null ? 0 : bean.getOwner());
				ps.setString(12, date);
				return ps;
			}
		}, keyHolder);
		
		int id = keyHolder.getKey().intValue();
		logger.info("id=" + id);
    	return id;
	}

	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
	@Override
	public void confirmMessage(int id, String result) {
		String date = DatetimeUtil.getToday(DatetimeUtil.DEFAULT_PATTERN_DT);
    	String sql = "update messages set status=1,result=?,update_date=? where status=0 and id=?";
    	jdbcTemplate.update(sql, result, date, id);
	}
	
	class MessageRowMapper implements RowMapper<MessageBean>{
		@Override
		public MessageBean mapRow(ResultSet rs, int rowNum) throws SQLException {
			int id = rs.getInt("id");
			String title = rs.getString("title");
			String content = rs.getString("content");
			String category = rs.getString("category");
			String result = rs.getString("result");
			String sender = rs.getString("sender");
			String senderNickname = rs.getString("sender_nickname");
			String senderPhone = rs.getString("sender_phone");
			String receiver = rs.getString("receiver");
			Date createDate = DatetimeUtil.toDate(rs.getString("create_date"));
			int status = rs.getInt("status");
			Integer owner = rs.getInt("owner");
			
			MessageBean bean = new MessageBean();
			bean.setId(id);
			bean.setTitle(title);
			bean.setContent(content);
			bean.setCategory(category);
			bean.setResult(result);
			bean.setSender(sender);
			bean.setSenderNickname(senderNickname);
			bean.setSenderPhone(senderPhone);
			bean.setReceiver(receiver);
			bean.setCreateDate(createDate);
			bean.setStatus(status);
			bean.setOwner(owner);
			
			return bean;
		}
	}
	
}
