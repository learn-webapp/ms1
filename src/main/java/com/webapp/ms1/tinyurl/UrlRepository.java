package com.webapp.ms1.tinyurl;

import com.webapp.ms1.config.AppConfig;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Repository
@AllArgsConstructor
public class UrlRepository {

    private static final String URL_TABLE = "url-mapping";

    public void save(Url url) {
        Map<String, AttributeValue> urlDataMap = new HashMap<>();
        urlDataMap.put("shortUrl", AttributeValue.fromS(url.getShortUrl()));
        urlDataMap.put("longUrl", AttributeValue.fromS(url.getLongUrl()));
        urlDataMap.put("createdAt", AttributeValue.fromS(url.getCreatedAt().toString()));
        PutItemRequest request = PutItemRequest.builder()
                .tableName(URL_TABLE)
                .item(urlDataMap)
                .build();
        AppConfig.getDynamoDbClient().putItem(request);
    }

    public Url getUrl(String shortUrl) {
        GetItemRequest request = GetItemRequest.builder()
                .tableName(URL_TABLE)
                .key(Map.of("shortUrl", AttributeValue.fromS(shortUrl)))
                .build();

        Map<String, AttributeValue> urlDataMap = AppConfig.getDynamoDbClient().getItem(request).item();
        if (urlDataMap == null || urlDataMap.isEmpty()) return null;

        return Url.builder()
                .shortUrl(urlDataMap.get("shortUrl").s())
                .longUrl(urlDataMap.get("longUrl").s())
                .createdAt(urlDataMap.get("createdAt") != null ? LocalDateTime.parse(urlDataMap.get("createdAt").s()) : null)
                .build();
    }
}

