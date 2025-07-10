package com.webapp.ms1.tinyurl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tiny-url")
public class TinyUrlController {

    private final UrlService urlService;


    /**
     * curl -X POST http://localhost:8080/tiny-url -H "Content-Type: text/plain" -d "https://www.example.com"
     * */
    @PostMapping()
    public String createTinyUrl(@RequestBody String longUrl) {
        return urlService.generateTinyUrl(longUrl);
    }

    /**
     * curl -v -L http://localhost:8080/tiny-url/abc123
     * */
    // this also shows example how 302 redirect works
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirectToLongUrl(@PathVariable String shortCode) {
        String longUrl = urlService.getLongUrl(shortCode);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(longUrl))
                .build();
    }
}