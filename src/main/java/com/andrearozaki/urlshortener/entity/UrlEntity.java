package com.andrearozaki.urlshortener.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "url_mappings")
@Data
public class UrlEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private String shortUrl;

    private String longUrl;

    private LocalDateTime creationDate;
}

