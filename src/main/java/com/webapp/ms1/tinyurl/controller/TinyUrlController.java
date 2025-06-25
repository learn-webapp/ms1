package com.webapp.ms1.tinyurl.controller;

import com.webapp.ms1.tinyurl.service.TinyUrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tiny-url")
public class TinyUrlController {

    private final TinyUrlService tinyUrlService;

    @GetMapping("/test")
    public String testMethod() {
        // create some random url here
        String randomUrl = generateRandomUrl();
        return tinyUrlService.generateTinyUrl(randomUrl);
    }

    public String generateRandomUrl () {
        // This method generates a random URL for demonstration purposes
        String baseUrl = "http://example.com/";
        String randomPath = java.util.UUID.randomUUID().toString();
        return baseUrl + randomPath;
    }
}