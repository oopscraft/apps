package org.oopscraft.apps.core.support;

import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.UUID;

@Slf4j
public class IdGenerator {

    private static final Charset CHARSET = Charset.forName("UTF-8");

    /**
     * generates UUID
     */
    public static String uuid() {
        String uuid = UUID.randomUUID().toString();
        log.debug("uuid:{}", uuid);
        return uuid;
    }

    /**
     * hash MD5
     */
    public static String md5(String value) {
        log.debug("value:{}", value);
        if(value == null) {
            return null;
        }
        try {
            StringBuffer encodedValueBuffer = new StringBuffer();
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(value.getBytes("UTF-8"));
            byte byteData[] = md.digest();
            for (int i = 0; i < byteData.length; i++) {
                encodedValueBuffer.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            String encodedValue = encodedValueBuffer.toString();
            log.debug("encodedValue:{}", encodedValue);
            return encodedValue;
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    /**
     * encode base64
     */
    public static String encodeBase64(String plainValue) {
        log.debug("plainValue:{}", plainValue);
        if(plainValue == null) {
            return null;
        }
        byte[] bytes = Base64.getUrlEncoder().encode(plainValue.getBytes(CHARSET));
        String encodedValue = new String(bytes, CHARSET);
        log.debug("encodedValue:{}", encodedValue);
        return encodedValue;
    }

    /**
     * decode base64
     */
    public static String decodeBase64(String encodedValue) {
        log.debug("encodedValue:{}", encodedValue);
        if(encodedValue == null) {
            return null;
        }
        byte[] bytes = Base64.getUrlDecoder().decode(encodedValue.getBytes(CHARSET));
        String decodedValue = new String(bytes, CHARSET);
        log.debug("decodedValue:{}", decodedValue);
        return decodedValue;
    }

}