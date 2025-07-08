package com.webapp.ms1.tinyurl.service;

import com.webapp.ms1.redis.RedisService;
import com.webapp.ms1.tinyurl.Base62Encoder;
import com.webapp.ms1.tinyurl.SnowflakeIdGenerator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TinyUrlService {

    private final SnowflakeIdGenerator idGenerator = new SnowflakeIdGenerator(1, 1); // workerId=1, datacenterId=1

    private final RedisService redisService;

    public String generateShortCode() {
        long id = idGenerator.nextId();
        return Base62Encoder.encode(id);
    }

    public String generateTinyUrl(String longUrl) {
        String shortCode = generateShortCode();
        redisService.save(shortCode, longUrl);
        return shortCode;
    }

    public String getLongUrl(String shortCode) {
        return redisService.getString(shortCode);
    }
}
