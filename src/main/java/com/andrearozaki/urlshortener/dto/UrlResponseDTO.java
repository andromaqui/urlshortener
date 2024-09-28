package com.andrearozaki.urlshortener.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UrlResponseDTO {
    private final String longUrl;
    private final String shortUrl;
    private final LocalDateTime creationDate;

    public UrlResponseDTO(String longUrl, String shortUrl, LocalDateTime creationDate) {
        this.longUrl = longUrl;
        this.shortUrl = shortUrl;
        this.creationDate = creationDate;
    }

}
