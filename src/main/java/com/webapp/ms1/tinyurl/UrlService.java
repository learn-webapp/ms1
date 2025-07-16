package com.webapp.ms1.tinyurl;

import com.webapp.ms1.redis.RedisService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@AllArgsConstructor
public class UrlService {

    // workerId=1, datacenterId=1
    private final SnowflakeIdGenerator idGenerator = new SnowflakeIdGenerator(1, 1);

    private final RedisService redisService;

    private final UrlRepository urlRepository;

    public String generateShortCode() {
        long id = idGenerator.nextId();
        return Base62Encoder.encode(id);
    }

    public String generateTinyUrl(String longUrl) {
        String shortCode = generateShortCode();
        Url url = Url.builder()
                .shortUrl(shortCode)
                .longUrl(longUrl).createdAt(LocalDateTime.now())
                .build();
        urlRepository.save(url);
        return shortCode;
    }

    public String getLongUrl(String shortCode) {
        // first find it in redis cache
        String longUrl = redisService.getString(shortCode);
        if (longUrl != null) {
            log.info("Found long URL in cache: {}", longUrl);
            return longUrl;
        }
        // fetch from database if not found in cache
        Url url = urlRepository.getUrl(shortCode);
        if (url == null) {
            log.warn("No URL found for short code: {}", shortCode);
            return null; // or throw an exception
        }
        // save into cache
        redisService.save(shortCode, url.getLongUrl());
        log.info("Retrieved URL: {}, {}", url.getShortUrl(), url.getLongUrl());
        return url.getLongUrl();
    }
}

