package com.inspire17.ythelper.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class TokenService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    public String generateVerificationToken(String email) {
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set("token:" + token, email, 30, TimeUnit.MINUTES); // Store for 30 mins
        return token;
    }

    public String generateOTP(String email) {
        String otp = String.valueOf(1000 + new SecureRandom().nextInt(9000));
        redisTemplate.opsForValue().set("otp:" + email, otp, 5, TimeUnit.MINUTES); // Store for 5 mins
        return otp;
    }

    public String getOTPFromEmail(String email) {
        return redisTemplate.opsForValue().get("otp:" + email);
    }

    public String getEmailFromToken(String token) {
        return redisTemplate.opsForValue().get("token:" + token);
    }

    public void removeToken(String token) {
        redisTemplate.delete("token:" + token);
    }

    public void removeOTP(String emailId) {
        redisTemplate.delete("otp:" + emailId);
    }
}
