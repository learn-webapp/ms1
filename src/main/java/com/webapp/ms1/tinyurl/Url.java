package com.webapp.ms1.tinyurl;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Url {

    private String shortUrl;

    private String longUrl;
}

