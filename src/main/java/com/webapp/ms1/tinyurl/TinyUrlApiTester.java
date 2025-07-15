package com.webapp.ms1.tinyurl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.UUID;

@Slf4j
@Component
public class TinyUrlApiTester {

    private final RestTemplate restTemplate = new RestTemplate();

    // runs every 5 minutes
    @Scheduled(fixedRate = 300000)
    public void testTinyUrlApis() {
        try {
            // Step 1: Generate dummy long URL
            String dummyUrl = "https://www.example.com/" + UUID.randomUUID();

            // Step 2: Send POST request to create short URL
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            HttpEntity<String> request = new HttpEntity<>(dummyUrl, headers);

            ResponseEntity<String> postResponse = restTemplate.postForEntity(
                    "http://localhost:8080/tiny-url", request, String.class
            );

            String shortCode = postResponse.getBody();
            log.info("Generated short code: {}", shortCode);

            if (shortCode == null || shortCode.isBlank()) {
                log.error("Short code was not returned properly.");
                return;
            }

            // Step 3: Use short code to hit redirect API
            String shortUrl = "http://localhost:8080/tiny-url/" + shortCode;

            ResponseEntity<String> getResponse = restTemplate.getForEntity(shortUrl, String.class);
            log.info("GET {} => {} - {}", shortUrl, getResponse.getStatusCode(), getResponse.getBody());

        } catch (Exception e) {
            log.error("Exception occurred while testing Tiny URL APIs: {}", e.getMessage(), e);
        }
    }
}