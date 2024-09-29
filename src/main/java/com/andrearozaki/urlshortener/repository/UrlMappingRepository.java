package com.andrearozaki.urlshortener.repository;

import com.andrearozaki.urlshortener.entity.UrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlMappingRepository extends JpaRepository<UrlEntity, Long> {
        Optional<UrlEntity> findByShortUrl(String shortUrl);
}
