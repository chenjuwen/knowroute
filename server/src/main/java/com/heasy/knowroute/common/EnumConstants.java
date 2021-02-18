package com.heasy.knowroute.common;

public final class EnumConstants {
	/**
	 * 消息类别
	 */
	public enum MessageCategory{
		/**
		 * 普通
		 */
		GENERAL,
		
		/**
		 * 邀请好友
		 */
		INVITE_FRIEND,
		
		/**
		 * 添加好友
		 */
		ADD_FRIEND,
		
		/**
		 * 紧急求助
		 */
		SEEK_HELP
	}
	
	/**
	 * 好友邀请状态码
	 */
	public enum FriendStatusCode{
		/**
		 * 无效的手机号码
		 */
		INVALID_PHONE,
		
		/**
		 * 未短信邀请
		 */
		NOT_INVITED,
		
		/**
		 * 已短信邀请
		 */
		INVITED,
		
		/**
		 * 本人
		 */
		SELF,
		
		/**
		 * 不是好友，已发送消息
		 */
		NOT_FRIEND__ADDED,
		
		/**
		 * 不是好友，未发送消息
		 */
		NOT_FRIEND__NOTADD,
		
		/**
		 * 已是好友
		 */
		ALREADY_FRIEND,
		
		/**
		 * 未知
		 */
		UNKNOW
	}

	/**
	 * 数据角色
	 */
	public static final String DATA_ROLE_SELF = "self"; //只允许本人查看数据
	public static final String DATA_ROLE_FRIEND = "friend"; //允许好友查看数据
	
	/**
	 * 参数放在哪个地方
	 */
	public static final String PARAM_TYPE_QUERY = "query"; //@RequestParam定义的参数
	public static final String PARAM_TYPE_PATH = "path"; //@PathVariable定义的参数
	public static final String PARAM_TYPE_BODY = "body"; //@RequestBody定义的参数
	
}
