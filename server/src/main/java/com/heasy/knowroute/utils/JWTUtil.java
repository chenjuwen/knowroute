package com.heasy.knowroute.utils;

import java.util.Calendar;

import org.apache.commons.codec.binary.Base64;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.JWTVerifier;

public class JWTUtil {
	private static final String SECRET_KEY = "knowroute@admin"; //秘钥
	private static final int TOKEN_EXPIRE_TIME = 24 * 60 * 60 * 1000; //token过期时间，24小时
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
	    } catch (Exception ex){
	    	ex.printStackTrace();
	    }
	    return false;
	}
	
	public static boolean verify(String token){
	    try {
	    	if(StringUtil.isEmpty(token)) {
	    		return false;
	    	}
	    	
			JWTVerifier verifier = JWT.require(getAlgorithm())
					.withIssuer(ISSUER)
					.build();
			verifier.verify(token);
			return true;
	    } catch (Exception ex){
	    	ex.printStackTrace();
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
			ex.printStackTrace();
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
    
//	public static void main(String[] args) {
//		String token = JWTUtil.generateToken("admin");
//		System.out.println(token);
//		System.out.println(getOriginalHeader(token));
//		System.out.println(getOriginalPayload(token));
//		System.out.println(checkToken(token));
//		
//		String username = JWTUtil.getClaimFromToken(token, "username");
//		System.out.println(username);
//		
//		System.out.println(JWTUtil.verify(token, username));
//		System.out.println(JWTUtil.verify(null));
//	}

}
