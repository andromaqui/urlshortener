package com.andrearozaki.urlshortener;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UrlMappingRepository extends JpaRepository<UrlModel, Long> {
        Optional<UrlModel> findByShortUrl(String shortUrl);
}
