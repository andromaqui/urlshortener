package com.andrearozaki.urlshortener.intergration;

import com.andrearozaki.urlshortener.dto.request.UrlRequestDTO;
import com.andrearozaki.urlshortener.entity.UrlEntity;
import com.andrearozaki.urlshortener.service.UrlService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@Testcontainers
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

    @AfterEach
    public void cleanup() {
        mongoTemplate.dropCollection(UrlEntity.class);
    }

    @Test
    void testCreateMultipleUrls() {
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
        Assertions.assertEquals(2, urls.size());
        Assertions.assertEquals("https://longurl.com", urls.get(0).getLongUrl());
        Assertions.assertNotNull(urls.get(0).getShortUrl());
        Assertions.assertEquals("https://google.com", urls.get(1).getLongUrl());
        Assertions.assertNotNull(urls.get(1).getShortUrl());
    }

}
