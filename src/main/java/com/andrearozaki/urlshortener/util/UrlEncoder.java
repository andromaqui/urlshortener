package com.andrearozaki.urlshortener.util;

import com.google.common.hash.Hashing;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Component
public class UrlEncoder {
    public String encodeUrl(String url, LocalDateTime time) {
        return Hashing.murmur3_32()
                .hashString(url.concat(time.toString()), StandardCharsets.UTF_8)
                .toString();
    }
}
