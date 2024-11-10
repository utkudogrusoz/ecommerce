package com.utkudogrusoz.ecommerce.Core.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisTokenService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void saveAccessToken(String username, String token) {
        redisTemplate.opsForValue().set("access:" + username, token, 5, TimeUnit.MINUTES);
    }

    public void saveRefreshToken(String username, String token) {
        redisTemplate.opsForValue().set("refresh:" + username, token, 30, TimeUnit.MINUTES);
    }

    public String getAccessToken(String username) {
        return redisTemplate.opsForValue().get("access:" + username);
    }

    public String getRefreshToken(String username) {
        return redisTemplate.opsForValue().get("refresh:" + username);
    }

    public void deleteTokens(String username) {
        redisTemplate.delete("access:" + username);
        redisTemplate.delete("refresh:" + username);
    }
}
