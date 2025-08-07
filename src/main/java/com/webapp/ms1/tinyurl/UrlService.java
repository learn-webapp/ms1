package com.webapp.ms1.tinyurl;

import com.webapp.ms1.redis.RedisService;
import com.webapp.ms1.sqs.QueueService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.model.Message;

import java.time.LocalDateTime;

import static com.webapp.ms1.json.JsonConfig.GSON;

@Slf4j
@Service
@AllArgsConstructor
public class UrlService {

    // workerId=1, datacenterId=1
    private final SnowflakeIdGenerator idGenerator = new SnowflakeIdGenerator(1, 1);

    private final RedisService redisService;

    private final UrlRepository urlRepository;

    private final QueueService queueService;

    private static final long SHORT_URL_EXPIRY_SEC = 15 * 60; // 15 minutes
    private static final long ALIAS_LOCK_EXPIRY_SEC = 15 * 60; // 15 minutes

    public String generateShortCode() {
        long id = idGenerator.nextId();
        return Base62Encoder.encode(id);
    }

    public String generateTinyUrl(String longUrl) {
        String shortCode = generateShortCode();
        Url url = Url.builder()
                .shortUrl(shortCode)
                .longUrl(longUrl)
                .createdAt(LocalDateTime.now())
                .build();
        // save into redis cache
        saveShortUrlInRedis(shortCode, longUrl, SHORT_URL_EXPIRY_SEC);
        // push message to SQS queue
        queueService.sendMessage(GSON.toJson(url));
        return shortCode;
    }

    public String getLongUrl(String shortCode) {
        // first find it in redis cache
        String longUrl = getLongUrlFromRedis(shortCode);
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
        saveShortUrlInRedis(shortCode, url.getLongUrl(), SHORT_URL_EXPIRY_SEC);
        log.info("Retrieved URL: {}, {}", url.getShortUrl(), url.getLongUrl());
        return url.getLongUrl();
    }


    public String createShortUrlRedisKey(String shortCode) {
        return "shortUrl:" + shortCode;
    }

    public String getLongUrlFromRedis(String shortCode) {
        String shortUrlKey = createShortUrlRedisKey(shortCode);
        return redisService.getString(shortUrlKey);
    }

    public void saveShortUrlInRedis(String shortUrl, String longUrl, long expirySeconds) {
        String redisShortUrlKey = createShortUrlRedisKey(shortUrl);
        redisService.save(redisShortUrlKey, longUrl, expirySeconds);
        log.info("Saved short URL in Redis: {}, {}", shortUrl, longUrl);
    }

    public boolean saveCustomAlias(String alias, String longUrl) {
        // first try to take lock in redis
        String redisKey = "alias:" + alias;
        boolean lockAcquired = redisService.setIfAbsent(redisKey, longUrl, ALIAS_LOCK_EXPIRY_SEC);
        if (!lockAcquired) {
            log.warn("Could not acquire lock for alias: {}", alias);
            return false; // could not acquire lock
        }
        log.info("Acquired lock successfully for alias: {}", alias);
        // check if key exists in redis
        if (checkIfUrlExistsInRedis(alias)) {
            log.info("Alias already exists in Redis: {}", alias);
            return false;
        }
        // check if key exists in dynamodb
        if (checkIfUrlExistsInDynamoDb(alias)) {
            log.warn("Alias already exists in DynamoDB: {}", alias);
            return false;
        }
        Url url = Url.builder()
                .shortUrl(alias)
                .longUrl(longUrl)
                .createdAt(LocalDateTime.now())
                .build();
        // save into redis cache
        saveShortUrlInRedis(alias, longUrl, SHORT_URL_EXPIRY_SEC);
        // push message to SQS queue
        queueService.sendMessage(GSON.toJson(url));
        log.info("Saved custom alias: {}, long URL: {}", alias, longUrl);
        return true;
    }

    public boolean checkIfUrlExistsInRedis(String shortUrl) {
        return redisService.getString(shortUrl) != null;
    }

    public boolean checkIfUrlExistsInDynamoDb(String shortUrl) {
        return urlRepository.getUrl(shortUrl) != null;
    }

    public void processQueueMessage(Message message) {
        Url url = GSON.fromJson(message.body(), Url.class);
        // save in dynamodb
        urlRepository.save(url);
        log.info("Saved short URL: {}, long URL: {}", url.getShortUrl(), url.getLongUrl());
    }
}

