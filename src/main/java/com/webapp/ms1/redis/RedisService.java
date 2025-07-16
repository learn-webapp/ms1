package com.webapp.ms1.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

    private static final long expirySeconds = 15 * 60;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void save(String key, Object value) {
        redisTemplate.opsForValue().set(key, value, expirySeconds, java.util.concurrent.TimeUnit.SECONDS);
    }

    public String getString(String key) {
        Object obj = redisTemplate.opsForValue().get(key);
        return obj instanceof String ? (String) obj : null;
    }

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}
