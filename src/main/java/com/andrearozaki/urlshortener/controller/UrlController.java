package com.andrearozaki.urlshortener.controller;

import com.andrearozaki.urlshortener.dto.request.UrlRequestDTO;
import com.andrearozaki.urlshortener.dto.response.UrlResponseDTO;
import com.andrearozaki.urlshortener.service.UrlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import jakarta.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/v1/shortUrl")
public class UrlController {
    @Autowired
    private UrlService urlService;

    private static final Logger logger = LoggerFactory.getLogger(UrlController.class);

    @PostMapping
    public ResponseEntity<UrlResponseDTO> shortenUrl(@Valid @RequestBody UrlRequestDTO urlRequestDTO) {
        logger.info("Shortening URL: {}", urlRequestDTO.getLongUrl());
        UrlResponseDTO savedUrlResponse = urlService.createUrlMapping(urlRequestDTO);
        logger.info("Shortened URL created: {}", savedUrlResponse.getShortUrl());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUrlResponse);
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<Void> redirect(@PathVariable String shortUrl) {
        logger.info("Retrieving long URL for short URL: {}", shortUrl);
        UrlResponseDTO urlResponseDTO = urlService.getLongUrl(shortUrl);

        if (urlResponseDTO == null || urlResponseDTO.getLongUrl() == null) {
            logger.warn("No long URL found for short URL: {}", shortUrl);
            return ResponseEntity.notFound().build();
        }

        String longUrl = urlResponseDTO.getLongUrl();
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(longUrl));
        logger.info("Redirecting to long URL: {}", longUrl);
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
                .headers(headers)
                .build();
    }
}
