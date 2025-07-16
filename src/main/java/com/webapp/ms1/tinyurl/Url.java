package com.webapp.ms1.tinyurl;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Url {

    private String shortUrl;

    private String longUrl;

    private LocalDateTime createdAt;
}

