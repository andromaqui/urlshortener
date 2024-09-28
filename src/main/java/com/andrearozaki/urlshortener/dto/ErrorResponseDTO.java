package com.andrearozaki.urlshortener.dto;

import lombok.Getter;

@Getter
public class ErrorResponseDTO {
    private final int status;
    private final String message;

    public ErrorResponseDTO(int status, String message) {
        this.status = status;
        this.message = message;
    }

}

