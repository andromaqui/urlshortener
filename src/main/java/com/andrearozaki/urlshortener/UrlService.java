package com.andrearozaki.urlshortener;

import com.andrearozaki.urlshortener.dto.UrlResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@Service
public class UrlService {
    @Autowired
    private UrlMappingRepository repository;

    public UrlResponseDTO createUrlMapping(UrlModel urlModel) {
        urlModel.setCreationDate(LocalDateTime.now(ZoneOffset.UTC)); // Convert to UTC
        urlModel.setShortUrl("dummy2");
        UrlModel savedUrl = repository.save(urlModel);
        return new UrlResponseDTO(savedUrl.getShortUrl(), savedUrl.getLongUrl(), savedUrl.getCreationDate());
    }

    public UrlResponseDTO getLongUrl(String shortUrl) {
        Optional<UrlModel> optionalUrlModel = repository.findByShortUrl(shortUrl);
        if (optionalUrlModel.isPresent()) {
            UrlModel urlModel = optionalUrlModel.get();
            return new UrlResponseDTO(urlModel.getShortUrl(), urlModel.getLongUrl(), urlModel.getCreationDate());
        } else {
            throw new RuntimeException("URL not found for shortUrl: " + shortUrl);
        }
    }

}

