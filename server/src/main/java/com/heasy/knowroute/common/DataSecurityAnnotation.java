package com.heasy.knowroute.common;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.heasy.knowroute.utils.JWTUtil;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface DataSecurityAnnotation {
	/**
	 * 定义能查看哪些角色的数据
	 * @return
	 */
	String dataRole() default EnumConstants.DATA_ROLE_SELF;
	String claimField() default JWTUtil.CLAIM_USERID;
	String paramType();
	String paramKey() default ""; //type为query时使用
	int paramIndex() default 0; //type为path时使用
}
