package com.sjxm.utils;

import io.jsonwebtoken.*;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

public class JwtUtil {
    /**
     * 生成Token（JWT加密）
     * @param secretKey jwt密钥
     * @param ttlMillis jwt存在时间
     * @param claims 设置的信息
     * @return
     */

    public static String createJWT(String secretKey, long ttlMillis, Map<String,Object> claims){
        //设置jwt的head
        //指定签名使用的签名算法，此处使用的是HS256
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        //生成JWT的时间
        long expMillis = System.currentTimeMillis()+ttlMillis;
        Date exp = new Date(expMillis);

        //设置jwt的body
        JwtBuilder jwtBuilder = Jwts.builder()
                .setClaims(claims)
                //设置签名算法与密钥
                .signWith(signatureAlgorithm,secretKey.getBytes(StandardCharsets.UTF_8))
                //设置过期时间
                .setExpiration(exp);

        return jwtBuilder.compact();
    }

    /**
     * token解密
     * @param secretKey jwt密钥，需要服务端保密
     * @param token 加密后的token
     * @return
     */

    public static Claims parseJWT(String secretKey,String token){
        // 得到DefaultJwtParser
        Claims claims = Jwts.parser()
                //设置签名的密钥
                .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                //设置需要解析的jwt
                .parseClaimsJws(token).getBody();
        return claims;
    }
}
