package com.andrearozaki.urlshortener.unit.controller;
import com.andrearozaki.urlshortener.controller.UrlController;
import com.andrearozaki.urlshortener.dto.request.UrlRequestDTO;
import com.andrearozaki.urlshortener.dto.response.UrlResponseDTO;
import com.andrearozaki.urlshortener.service.UrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
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

    private MockMvc mockMvc;

    @Mock
    private UrlService urlService;

    @InjectMocks
    private UrlController urlController;

    private UrlRequestDTO urlRequestDTO;
    private UrlResponseDTO urlResponseDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(urlController).build();

        urlRequestDTO = new UrlRequestDTO();
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
        when(urlService.getLongUrl(eq("encodedShortUrl"))).thenReturn(urlResponseDTO);

        mockMvc.perform(get("/api/v1/shortUrl/encodedShortUrl"))
                .andExpect(status().isMovedPermanently())
                .andExpect(header().string(HttpHeaders.LOCATION, "https://www.example.com"));

        verify(urlService, times(1)).getLongUrl(eq("encodedShortUrl"));
    }

    @Test
    void testRedirect_shortUrlNotFound() throws Exception {
        when(urlService.getLongUrl(eq("nonexistentShortUrl"))).thenReturn(null);

        mockMvc.perform(get("/api/v1/shortUrl/nonexistentShortUrl"))
                .andExpect(status().isNotFound());

        verify(urlService, times(1)).getLongUrl(eq("nonexistentShortUrl"));
    }

    @Test
    void testShortenUrl_requestBodyValidationError() throws Exception {
        String invalidRequestBody = "{}";

        mockMvc.perform(post("/api/v1/shortUrl")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestBody))
                .andExpect(status().isBadRequest());

        verify(urlService, times(0)).createUrlMapping(any(UrlRequestDTO.class));
    }
}
