package com.andrearozaki.urlshortener.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class UrlEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String shortUrl;

    @Column(nullable = false)
    private String longUrl;

    private LocalDateTime creationDate;
}

