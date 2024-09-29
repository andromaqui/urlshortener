package com.andrearozaki.urlshortener.repository;

import com.andrearozaki.urlshortener.entity.UrlEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlMappingRepository extends MongoRepository<UrlEntity, Long> {
        Optional<UrlEntity> findByShortUrl(String shortUrl);
}
