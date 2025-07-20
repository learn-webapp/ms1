package com.webapp.ms1.tinyurl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.Objects;
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
                log.info("ApiTester shortUrl creation test failed");
                return;
            }
            log.info("ApiTester shortUrl creation test passed successfully");

            // Step 3: Use short code to hit redirect API
            String shortUrl = "http://localhost:8080/tiny-url/" + shortCode;
            ResponseEntity<String> getResponse = restTemplate.getForEntity(shortUrl, String.class);
            log.info("GET {} => {} - {}", shortUrl, getResponse.getStatusCode(), getResponse.getBody());
            // Check if the response is a redirect (302 Found)
            if (getResponse.getStatusCode() == HttpStatus.FOUND) {
                String location = Objects.requireNonNull(getResponse.getHeaders().getLocation()).toString();
                log.info("Redirected to long URL: {}", location);
                if (!location.equals(dummyUrl)) {
                    log.error("Redirected URL does not match the original long URL");
                    log.info("ApiTester redirect test failed");
                } else {
                    log.info("ApiTester redirect test passed successfully");
                }
            } else {
                log.error("Expected 302 Found but got {}", getResponse.getStatusCode());
                log.info("ApiTester redirect test failed");
            }
        } catch (Exception e) {
            log.error("Exception occurred while testing Tiny URL APIs: {}", e.getMessage(), e);
        }
    }
}