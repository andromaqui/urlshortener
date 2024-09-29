package com.andrearozaki.urlshortener.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDateTime;
import jakarta.validation.constraints.NotNull;

@Data
public class UrlRequestDTO {

    @NotNull(message = "long url is required")
    @NotBlank(message = "long url is required")
    private String longUrl;

    private LocalDateTime creationDate;
}
