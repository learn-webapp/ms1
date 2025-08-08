package com.webapp.ms1.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void save(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void save(String key, Object value, long expirySeconds) {
        redisTemplate.opsForValue().set(key, value, expirySeconds, java.util.concurrent.TimeUnit.SECONDS);
    }

    public String getString(String key) {
        Object obj = redisTemplate.opsForValue().get(key);
        return obj instanceof String ? (String) obj : null;
    }

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * this method sets a value for the given key only if the key does not already exist.
     * it can be used to take lock on a resource or to ensure that a value is set only once.
     * */
    public boolean setIfAbsent(String key, Object value, long expirySeconds) {
        Boolean result = redisTemplate.opsForValue().setIfAbsent(key, value, expirySeconds, java.util.concurrent.TimeUnit.SECONDS);
        return result != null && result;
    }

    // write method to check redis connection
    public boolean isRedisConnected() {
        try {
            return redisTemplate.getConnectionFactory().getConnection().ping() != null;
        } catch (Exception e) {
            log.error("Redis connection error: {}", e.getMessage());
            return false;
        }
    }
}
