package org.dingdang.common.utils;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.dingdang.config.JwtProperties;
import org.dingdang.domain.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    @Autowired
    private JwtProperties jwtProperties;

    public String generateToken(Long userId, String openId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("openId", openId);
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getSecretKey()));

        Date expirationDate = null;
//        if (!jwtProperties.isInfiniteExpiration()) {
//            long expirationTimeLong = jwtProperties.getExpiration() * 1000; // convert seconds to milliseconds
//            expirationDate = new Date(System.currentTimeMillis() + expirationTimeLong);
//        }


        return Jwts.builder()
                .claims(claims)
                .expiration(expirationDate)
                .signWith(key)
                .compact();
    }

    public SysUser parseToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getSecretKey()));
        Claims payload = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        SysUser sysUser = new SysUser();
        sysUser.setUserId(Long.parseLong(payload.get("userId").toString()));
        sysUser.setOpenId(payload.get("openId").toString());
        return sysUser;
    }
}