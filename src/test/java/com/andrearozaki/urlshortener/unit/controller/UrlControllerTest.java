package com.andrearozaki.urlshortener.unit.controller;
import com.andrearozaki.urlshortener.controller.UrlController;
import com.andrearozaki.urlshortener.dto.request.UrlRequestDTO;
import com.andrearozaki.urlshortener.dto.response.UrlResponseDTO;
import com.andrearozaki.urlshortener.exception.UrlNotFoundException;
import com.andrearozaki.urlshortener.exceptionhandler.GlobalExceptionHandler;
import com.andrearozaki.urlshortener.service.UrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UrlControllerTest {

    private static final String LONG_URL = "https://www.example.com";
    private static final String SHORT_URL = "encodedShortUrl";
    private static final String NON_EXISTENT_SHORT_URL = "nonexistentShortUrl";
    private static final String INVALID_REQUEST_BODY = "{}";

    private MockMvc mockMvc;

    @Mock
    private UrlService urlService;

    @InjectMocks
    private UrlController urlController;

    private UrlResponseDTO urlResponseDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(urlController)
                .setControllerAdvice(new GlobalExceptionHandler())  // Registering your GlobalExceptionHandler
                .build();

        UrlRequestDTO urlRequestDTO = new UrlRequestDTO();
        urlRequestDTO.setLongUrl("https://www.example.com");

        urlResponseDTO = new UrlResponseDTO("encodedShortUrl", "https://www.example.com", LocalDateTime.now());
    }

    @Test
    void testShortenUrl_Success() throws Exception {
        when(urlService.createUrlMapping(any(UrlRequestDTO.class))).thenReturn(urlResponseDTO);

        String requestBody = "{\"longUrl\": \"https://www.example.com\"}";

        mockMvc.perform(post("/api/v1/shortUrl")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortUrl").value("encodedShortUrl"))
                .andExpect(jsonPath("$.longUrl").value("https://www.example.com"));

        verify(urlService, times(1)).createUrlMapping(any(UrlRequestDTO.class));
    }

    @Test
    void testGetLongUrl_Success() throws Exception {
        // Mock the service to return the long URL (String) instead of UrlResponseDTO
        when(urlService.getLongUrlOrThrow(eq(SHORT_URL))).thenReturn(LONG_URL);

        mockMvc.perform(get("/api/v1/shortUrl/" + SHORT_URL))
                .andExpect(status().isMovedPermanently())  // Expect a 301 redirect
                .andExpect(header().string(HttpHeaders.LOCATION, LONG_URL));  // Expect the location header to be the long URL

        verify(urlService, times(1)).getLongUrlOrThrow(eq(SHORT_URL));
    }

    @Test
    void testRedirect_shortUrlNotFound() throws Exception {
        when(urlService.getLongUrlOrThrow(eq(NON_EXISTENT_SHORT_URL)))
                .thenThrow(new UrlNotFoundException(NON_EXISTENT_SHORT_URL));

        mockMvc.perform(get("/api/v1/shortUrl/" + NON_EXISTENT_SHORT_URL))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("URL not found: " + NON_EXISTENT_SHORT_URL));

        verify(urlService, times(1)).getLongUrlOrThrow(eq(NON_EXISTENT_SHORT_URL));
    }

    @Test
    void testShortenUrl_requestBodyValidationError() throws Exception {
        mockMvc.perform(post("/api/v1/shortUrl")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(INVALID_REQUEST_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));

        verify(urlService, times(0)).createUrlMapping(any(UrlRequestDTO.class));
    }
}
