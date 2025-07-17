package com.webapp.ms1.tinyurl;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomAliasRequest {

    private String alias;

    private String longUrl;
}
