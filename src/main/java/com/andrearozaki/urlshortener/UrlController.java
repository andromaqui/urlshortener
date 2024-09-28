package com.andrearozaki.urlshortener;

import com.andrearozaki.urlshortener.dto.UrlResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/shortUrl")
public class UrlController {
    @Autowired
    private UrlService urlService;

    @PostMapping
    public ResponseEntity<UrlResponseDTO> shortenUrl(@Valid @RequestBody UrlModel urlModel) {
        UrlResponseDTO savedUrlResponse = urlService.createUrlMapping(urlModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUrlResponse);
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<UrlResponseDTO> getLongUrl(@PathVariable String shortUrl) {
        UrlResponseDTO urlResponse = urlService.getLongUrl(shortUrl);
        return ResponseEntity.ok(urlResponse); // Return DTO with 200 OK
    }
}

