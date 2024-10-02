package com.andrearozaki.urlshortener.intergration;

import com.andrearozaki.urlshortener.dto.request.UrlRequestDTO;
import com.andrearozaki.urlshortener.dto.response.UrlResponseDTO;
import com.andrearozaki.urlshortener.entity.UrlEntity;
import com.andrearozaki.urlshortener.repository.UrlMappingRepository;
import com.andrearozaki.urlshortener.service.UrlService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@EnableCaching
public class UrlMappingIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0").withExposedPorts(27017);

    @DynamicPropertySource
    static void containersProperties(DynamicPropertyRegistry registry) {
        mongoDBContainer.start();
        registry.add("spring.data.mongodb.host", mongoDBContainer::getHost);
        registry.add("spring.data.mongodb.port", mongoDBContainer::getFirstMappedPort);
    }

    @Autowired
    private UrlService urlService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private CacheManager cacheManager;

    @SpyBean
    private UrlMappingRepository repository;

    @AfterEach
    public void cleanup() {
        mongoTemplate.dropCollection(UrlEntity.class);
    }

    @Test
    public void testCreateMultipleUrls() {
        UrlRequestDTO firstUrlRequestDTO = new UrlRequestDTO();
        firstUrlRequestDTO.setLongUrl("https://longurl.com");
        firstUrlRequestDTO.setCreationDate(LocalDateTime.now());

        UrlRequestDTO secondUrlRequestDTO = new UrlRequestDTO();
        secondUrlRequestDTO.setLongUrl("https://google.com");
        secondUrlRequestDTO.setCreationDate(LocalDateTime.now());

        // Save the URL using the service
        urlService.createUrlMapping(firstUrlRequestDTO);
        urlService.createUrlMapping(secondUrlRequestDTO);
        List<UrlEntity> urls = mongoTemplate.findAll(UrlEntity.class);

        // Verify the results
        assertEquals(2, urls.size());
        assertEquals("https://longurl.com", urls.get(0).getLongUrl());
        assertNotNull(urls.get(0).getShortUrl());
        assertEquals("https://google.com", urls.get(1).getLongUrl());
        assertNotNull(urls.get(1).getShortUrl());
    }

    @Test
    void testGetLongUrlWithCaching() {
        // Step 1: Create a URL mapping
        UrlRequestDTO urlRequestDTO = new UrlRequestDTO();
        urlRequestDTO.setLongUrl("https://example.com");
        urlRequestDTO.setCreationDate(LocalDateTime.now());

        UrlResponseDTO createdUrl = urlService.createUrlMapping(urlRequestDTO);

        // Step 2: Retrieve it using getLongUrl (first call - should hit the database)
        String firstCallResponse = urlService.getLongUrlOrThrow(createdUrl.getShortUrl());
        assertEquals("https://example.com", firstCallResponse);

        // Verify that repository was called exactly once for the first DB hit
        verify(repository, times(1)).findByShortUrl(createdUrl.getShortUrl());

        // Step 3: Make a second call to getLongUrl (should hit the cache)
        String secondCallResponse = urlService.getLongUrlOrThrow(createdUrl.getShortUrl());
        assertEquals("https://example.com", secondCallResponse);

        // Verify that repository was not called again (cache should handle it)
        verify(repository, times(1)).findByShortUrl(createdUrl.getShortUrl()); // Still 1 call, no additional hit
    }

}
