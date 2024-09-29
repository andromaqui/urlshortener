package com.andrearozaki.urlshortener.unit.util;

import com.andrearozaki.urlshortener.util.UrlEncoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UrlEncoderTest {

    private UrlEncoder urlEncoder;

    @BeforeEach
    void setUp() {
        urlEncoder = new UrlEncoder();
    }

    @Test
    void testEncodeUrl_GeneratesConsistentHashForSameInput() {
        String url = "https://www.example.com";
        LocalDateTime time = LocalDateTime.of(2023, 9, 28, 12, 30);

        String hash1 = urlEncoder.encodeUrl(url, time);
        String hash2 = urlEncoder.encodeUrl(url, time);

        assertNotNull(hash1);
        assertEquals(hash1, hash2); // Ensure that the same input generates the same output
    }

    @Test
    void testEncodeUrl_GeneratesDifferentHashesForDifferentTimes() {
        String url = "https://www.example.com";
        LocalDateTime time1 = LocalDateTime.of(2023, 9, 28, 12, 30);
        LocalDateTime time2 = LocalDateTime.of(2023, 9, 28, 12, 31); // One minute later

        String hash1 = urlEncoder.encodeUrl(url, time1);
        String hash2 = urlEncoder.encodeUrl(url, time2);

        assertNotNull(hash1);
        assertNotNull(hash2);
        assertNotEquals(hash1, hash2); // Ensure different times generate different hashes
    }

    @Test
    void testEncodeUrl_GeneratesDifferentHashesForDifferentUrls() {
        String url1 = "https://www.example1.com";
        String url2 = "https://www.example2.com";
        LocalDateTime time = LocalDateTime.of(2023, 9, 28, 12, 30);

        String hash1 = urlEncoder.encodeUrl(url1, time);
        String hash2 = urlEncoder.encodeUrl(url2, time);

        assertNotNull(hash1);
        assertNotNull(hash2);
        assertNotEquals(hash1, hash2); // Ensure different URLs generate different hashes
    }
}
