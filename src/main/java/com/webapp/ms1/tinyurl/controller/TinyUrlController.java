package com.webapp.ms1.tinyurl.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tiny-url")
public class TinyUrlController {

    @GetMapping("/test")
    public String testMethod() {

        // create some random string here and return it
        return "Random string : " + java.util.UUID.randomUUID().toString();
    }
}
