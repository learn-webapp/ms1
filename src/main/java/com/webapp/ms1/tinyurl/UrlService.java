package com.webapp.ms1.tinyurl;

import com.webapp.ms1.redis.RedisService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class UrlService {

    // workerId=1, datacenterId=1
    private final SnowflakeIdGenerator idGenerator = new SnowflakeIdGenerator(1, 1);

//    private final RedisService redisService;

    private final UrlRepository urlRepository;

    public String generateShortCode() {
        long id = idGenerator.nextId();
        return Base62Encoder.encode(id);
    }

    public String generateTinyUrl(String longUrl) {
        String shortCode = generateShortCode();
        urlRepository.save(Url.builder()
                .shortUrl(shortCode)
                .longUrl(longUrl)
                .build());
        return shortCode;
    }

    public String getLongUrl(String shortCode) {
        // save it in redis cache
//        redisService.save(shortCode, longUrl);
        Url url = urlRepository.getUrl(shortCode);
        log.info("Retrieved URL: {}", url);
        return url.getLongUrl();
    }
}

