package com.andrearozaki.urlshortener.exceptionhandler;

import com.andrearozaki.urlshortener.dto.response.ErrorResponseDTO;
import com.andrearozaki.urlshortener.exception.InvalidUrlException;
import com.andrearozaki.urlshortener.exception.UrlNotFoundException;
import com.andrearozaki.urlshortener.exception.UrlShortenerRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UrlNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleUrlNotFound(UrlNotFoundException ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return new ResponseEntity<ErrorResponseDTO>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidUrlException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidUrl(InvalidUrlException ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return new ResponseEntity<ErrorResponseDTO>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UrlShortenerRuntimeException.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(UrlShortenerRuntimeException ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
        return new ResponseEntity<ErrorResponseDTO>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(MethodArgumentNotValidException ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return new ResponseEntity<ErrorResponseDTO>(errorResponse, HttpStatus.BAD_REQUEST);
    }

}
