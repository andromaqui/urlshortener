package com.andrearozaki.urlshortener.unit.service;

import com.andrearozaki.urlshortener.entity.UrlEntity;
import com.andrearozaki.urlshortener.exception.UrlNotFoundException;
import com.andrearozaki.urlshortener.exception.UrlShortenerRuntimeException;
import com.andrearozaki.urlshortener.repository.UrlMappingRepository;
import com.andrearozaki.urlshortener.dto.request.UrlRequestDTO;
import com.andrearozaki.urlshortener.dto.response.UrlResponseDTO;
import com.andrearozaki.urlshortener.service.UrlService;
import com.andrearozaki.urlshortener.util.UrlEncoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.CacheManager;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UrlServiceTest {

    @InjectMocks
    private UrlService urlService;

    @Mock
    private UrlMappingRepository repository;

    @Mock
    private UrlEncoder urlEncoder;

    @Mock
    private CacheManager cacheManager;

    private UrlRequestDTO urlRequestDTO;
    private UrlEntity urlEntity;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        urlRequestDTO = new UrlRequestDTO();
        urlRequestDTO.setLongUrl("https://www.example.com");

        urlEntity = new UrlEntity();
        urlEntity.setLongUrl("https://www.example.com");
        urlEntity.setShortUrl("encodedShortUrl");
        urlEntity.setCreationDate(LocalDateTime.now(ZoneOffset.UTC));
    }

    @Test
    public void testCreateUrlMapping_Success() {
        when(urlEncoder.encodeUrl(anyString(), any(LocalDateTime.class))).thenReturn("encodedShortUrl");
        when(repository.save(any(UrlEntity.class))).thenReturn(urlEntity);

        UrlResponseDTO response = urlService.createUrlMapping(urlRequestDTO);

        assertNotNull(response);
        assertEquals("encodedShortUrl", response.getShortUrl());
        assertEquals("https://www.example.com", response.getLongUrl());
        assertNotNull(response.getCreationDate());

        verify(repository, times(1)).save(any(UrlEntity.class));
    }

    @Test
    public void testCreateUrlMapping_Exception() {
        when(repository.save(any(UrlEntity.class))).thenThrow(new RuntimeException("Database error"));

        UrlShortenerRuntimeException thrown = assertThrows(UrlShortenerRuntimeException.class, () -> {
            urlService.createUrlMapping(urlRequestDTO);
        });

        assertEquals("An error occurred while attempting to create a URL mapping for https://www.example.com", thrown.getMessage());
    }

    @Test
    public void testGetLongUrl_Success() {
        when(repository.findByShortUrl("encodedShortUrl")).thenReturn(Optional.of(urlEntity));

        UrlResponseDTO response = urlService.getLongUrl("encodedShortUrl");

        assertNotNull(response);
        assertEquals("encodedShortUrl", response.getShortUrl());
        assertEquals("https://www.example.com", response.getLongUrl());

        verify(repository, times(1)).findByShortUrl("encodedShortUrl");
    }

    @Test
    public void testGetLongUrl_NotFound() {
        when(repository.findByShortUrl("nonExistingShortUrl")).thenReturn(Optional.empty());

        UrlNotFoundException thrown = assertThrows(UrlNotFoundException.class, () -> {
            urlService.getLongUrl("nonExistingShortUrl");
        });

        assertEquals("URL not found: nonExistingShortUrl", thrown.getMessage());
    }

}
