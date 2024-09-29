package com.andrearozaki.urlshortener.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UrlResponseDTO {
    private final String shortUrl;
    private final String longUrl;
    private final LocalDateTime creationDate;

}
