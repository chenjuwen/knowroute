package com.heasy.knowroute.utils;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.JWTVerifier;

public class JWTUtil {
	private static Logger logger = LoggerFactory.getLogger(JWTUtil.class);

	public static final int TOKEN_EXPIRE_TIME = 3 * 24 * 60 * 60 * 1000; //token过期时间
	public static final int REFRESH_TOKEN_REMAINING_MINUTES = 24 * 60; //刷新token的最大剩余分钟数
	
	private static final String SECRET_KEY = "knowroute@admin"; //秘钥
	private static final String ISSUER = "knowroute"; //签发人

	public static final String CLAIM_USERID = "userid";
	public static final String CLAIM_PHONE = "phone";
	
	private static Algorithm getAlgorithm() {
		return Algorithm.HMAC256(SECRET_KEY); //算法
	}

    /**
     * 生成签名
     */
	public static String generateToken(String userid, String phone){
		String token = JWT.create()
			.withIssuer(ISSUER) //签发人
			.withIssuedAt(DatetimeUtil.nowDate()) //签发时间
			.withExpiresAt(DatetimeUtil.add(DatetimeUtil.nowDate(), Calendar.MILLISECOND, TOKEN_EXPIRE_TIME)) //过期时间
			.withClaim(CLAIM_USERID, userid)
			.withClaim(CLAIM_PHONE, phone)
			.sign(getAlgorithm());
		return token;
	}
	
	/**
	 * 验证token是否正确
	 */
	public static boolean verify(String token, String claimName, String claimValue){
	    try {
	    	if(StringUtil.isEmpty(token)) {
	    		return false;
	    	}
	    	
			JWTVerifier verifier = JWT.require(getAlgorithm())
					.withIssuer(ISSUER)
					.withClaim(claimName, claimValue)
					.build();
			verifier.verify(token);
			return true;
			
	    } catch (SignatureVerificationException ex) {
	    	logger.error(ex.toString());
	    } catch (TokenExpiredException ex) {
	    	logger.error(ex.toString());
	    } catch (InvalidClaimException ex) {
	    	logger.error(ex.toString());
	    } catch (Exception ex){
	    	logger.error("", ex);
	    }
	    return false;
	}
	
	public static boolean verify(String token){
	    try {
	    	if(StringUtil.isEmpty(token)) {
	    		return false;
	    	}
	    	
			JWTVerifier verifier = JWT.require(getAlgorithm()).withIssuer(ISSUER).build();
			verifier.verify(token); //验证不通过时会抛出各种异常
			return true;
			
	    } catch (SignatureVerificationException ex) {
	    	logger.error(ex.toString());
	    } catch (TokenExpiredException ex) {
	    	logger.error(ex.toString());
	    } catch (InvalidClaimException ex) {
	    	logger.error(ex.toString());
	    } catch (Exception ex){
	    	logger.error("", ex);
	    }
	    return false;
	}
	
	/**
	 * 获取指定claim
	 */
	public static String getClaimFromToken(String token, String claimName){
		try{
	    	if(StringUtil.isEmpty(token)) {
	    		return "";
	    	}
	    	
			return JWT.decode(token).getClaim(claimName).asString();
		}catch(Exception ex){
	    	logger.error("", ex);
		}
		return "";
	}
	
	/**
	 * 获取头部信息：token类型、加密算法
	 */
	public static String getOriginalHeader(String token){
		String encodedHeader = JWT.decode(token).getHeader();
		String originalHeader = new String(Base64.decodeBase64(encodedHeader));
		return originalHeader;
	}

	/**
	 * 获取负载信息
	 */
	public static String getOriginalPayload(String token){
		String encodedPayload = JWT.decode(token).getPayload();
		String originalPayload = new String(Base64.decodeBase64(encodedPayload));
		return originalPayload;
	}
	
	/**
	 * 获取过期时间
	 */
	public static Date getExpiresDate(String token) {
		Date date = JWT.decode(token).getClaims().get("exp").asDate();
		return date;
	}
	
	/**
	 * 验证token是否有效：没有被篡改等
	 */
	public static boolean checkToken(String token){
		byte[] headers = JWT.decode(token).getHeader().getBytes();
		byte[] payloads = JWT.decode(token).getPayload().getBytes();
		String signature = JWT.decode(token).getSignature();

		byte[] bytes = getAlgorithm().sign(headers, payloads);
		String newSignature = Base64.encodeBase64URLSafeString(bytes);
		return newSignature.endsWith(signature);
	}
    
//	public static void main(String[] args) throws Exception {
//		String token = JWTUtil.generateToken("5", "13798189352");
//		System.out.println(token);
//		System.out.println(JWTUtil.verify(token));
//		TimeUnit.SECONDS.sleep(3);
//		System.out.println(JWTUtil.verify(token));
//		Date date = JWTUtil.getExpiresDate(token);
//		System.out.println(DatetimeUtil.formatDate(DatetimeUtil.nowDate()));
//		System.out.println(DatetimeUtil.formatDate(date));
//		System.out.println(DatetimeUtil.nowDate().after(date));
//	}

}
