package com.webapp.ms1.tinyurl.service;

import org.springframework.stereotype.Service;

@Service
public class TinyUrlService {

    public String generateTinyUrl(String longUrl) {
        String tinyUrl = "http://tinyurl.com/" + longUrl.hashCode();
//        System.out.println("Generated Tiny URL: " + tinyUrl);
        return tinyUrl;
    }
}
