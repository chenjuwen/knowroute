package com.heasy.knowroute.utils;
import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES加解密
 */
public class AESEncrpt {
    private static final String ALGO = "AES";
    private static final byte[] PWD = "knowroute@163.com".getBytes(); // 密钥
    private static final String CHARTSET = "UTF-8";

    private static Key generateKey() throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance(ALGO);  
        kgen.init(128, new SecureRandom(PWD));
        
        SecretKey secretKey = kgen.generateKey();  
        byte[] encodeFormat = secretKey.getEncoded();  
        
        SecretKeySpec key = new SecretKeySpec(encodeFormat, ALGO);
        
        return key;
    }
    
    /**
     * 加密字符串
     */
    public static String encrypt(String data){
    	try {
	        Key key = generateKey();
	        Cipher cipher = Cipher.getInstance(ALGO);
	        cipher.init(Cipher.ENCRYPT_MODE, key);
	
	        byte[] encVal = cipher.doFinal(data.getBytes(CHARTSET));
	        String encryptedValue = byte2HexStr(encVal);
	        return encryptedValue;
    	} catch(Exception ex) {
    		ex.printStackTrace();
    		return data;
    	}
    }

    /**
     * 解密字符串
     */
    public static String decrypt(String encryptedData){
    	try {
	        Key key = generateKey();
	        Cipher cipher = Cipher.getInstance(ALGO);
	        cipher.init(Cipher.DECRYPT_MODE, key);
	
	        byte[] b2 = cipher.doFinal(hexStr2Byte(encryptedData));
	        String decordedValue = new String(b2, CHARTSET);
	        return decordedValue;
    	} catch(Exception ex) {
    		ex.printStackTrace();
    		return encryptedData;
    	}
    }
    
    /**
	 * 将字节数组转换成16进制字符串
	 * @param buf 字节数组
	 * @return 16进制字符串
	 */
    private static String byte2HexStr(byte buf[]) {  
	    StringBuffer sb = new StringBuffer();  
	    for (int i = 0; i < buf.length; i++) {  
	            String hex = Integer.toHexString(buf[i] & 0xFF);  
	            if (hex.length() == 1) {  
	                    hex = '0' + hex;  
	            }  
	            sb.append(hex.toUpperCase());  
	    }  
	    return sb.toString();  
	}

	/**
	 * 将16进制字符串转换为字节数组 
	 * @param hexStr 16进制字符串
	 * @return 字节数组
	 */
	private static byte[] hexStr2Byte(String hexStr) {  
	    if (hexStr.length() < 1)  
	            return null;  
	    byte[] result = new byte[hexStr.length()/2];  
	    for (int i = 0;i< hexStr.length()/2; i++) {  
	            int high = Integer.parseInt(hexStr.substring(i*2, i*2+1), 16);  
	            int low = Integer.parseInt(hexStr.substring(i*2+1, i*2+2), 16);  
	            result[i] = (byte) (high * 16 + low);  
	    }  
	    return result;
	}
    
}
