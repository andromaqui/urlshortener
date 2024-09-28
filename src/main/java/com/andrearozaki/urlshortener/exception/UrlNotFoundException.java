package com.andrearozaki.urlshortener.exception;

public class UrlNotFoundException extends RuntimeException {
    public UrlNotFoundException(String shortUrl) {
        super("URL not found: " + shortUrl);
    }
}
