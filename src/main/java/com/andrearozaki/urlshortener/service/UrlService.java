package com.andrearozaki.urlshortener.service;

import com.andrearozaki.urlshortener.entity.UrlEntity;
import com.andrearozaki.urlshortener.repository.UrlMappingRepository;
import com.andrearozaki.urlshortener.dto.request.UrlRequestDTO;
import com.andrearozaki.urlshortener.dto.response.UrlResponseDTO;
import com.andrearozaki.urlshortener.exception.UrlNotFoundException;
import com.andrearozaki.urlshortener.exception.UrlShortenerRuntimeException;
import com.andrearozaki.urlshortener.util.UrlEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@Service
public class UrlService {
    @Autowired
    private UrlMappingRepository repository;

    @Autowired
    private UrlEncoder urlEncoder;

    private static final Logger logger = LoggerFactory.getLogger(UrlService.class);

    public UrlResponseDTO createUrlMapping(UrlRequestDTO urlRequestDTO) {
        logger.info("Creating URL mapping for: {}", urlRequestDTO.getLongUrl());
        try {
            LocalDateTime nowInUTC = LocalDateTime.now(ZoneOffset.UTC);
            logger.debug("Incoming request data: {}", urlRequestDTO);
            String encodedUrl = urlEncoder.encodeUrl(urlRequestDTO.getLongUrl(), nowInUTC);
            logger.debug("Encoded URL: {}", encodedUrl);

            UrlEntity persistantUrl = new UrlEntity();
            persistantUrl.setCreationDate(LocalDateTime.now(ZoneOffset.UTC));
            persistantUrl.setLongUrl(urlRequestDTO.getLongUrl());
            persistantUrl.setShortUrl(encodedUrl);
            repository.save(persistantUrl);

            logger.info("Successfully created URL mapping. Short URL: {}", persistantUrl.getShortUrl());
            return new UrlResponseDTO(persistantUrl.getShortUrl(), persistantUrl.getLongUrl(), persistantUrl.getCreationDate());
        } catch (Exception e) {
            logger.error("Error occurred while creating URL mapping: {}", e.getMessage(), e);
            throw new UrlShortenerRuntimeException("An error occurred while attempting to create a URL mapping for " + urlRequestDTO.getLongUrl());
        }
    }

    @Cacheable(value = "urlcache", key="{#shortUrl}")
    public UrlResponseDTO getLongUrl(String shortUrl) {
        logger.info("Retrieving long URL for short URL: {}", shortUrl);
        Optional<UrlEntity> optionalUrl = repository.findByShortUrl(shortUrl);
        if (optionalUrl.isPresent()) {
            UrlEntity existentURL = optionalUrl.get();
            logger.debug("Found long URL: {}", existentURL.getLongUrl());
            return new UrlResponseDTO(existentURL.getShortUrl(), existentURL.getLongUrl(), existentURL.getCreationDate());
        } else {
            logger.warn("Short URL not found: {}", shortUrl);
            throw new UrlNotFoundException(shortUrl);
        }
    }
}
