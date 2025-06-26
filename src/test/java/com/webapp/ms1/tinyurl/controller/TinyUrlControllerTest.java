package com.webapp.ms1.tinyurl.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.webapp.ms1.tinyurl.service.TinyUrlService;

class TinyUrlControllerTest {

    @Test
    void testMethod() {
        TinyUrlService mockService = mock(TinyUrlService.class);
        TinyUrlController controller = new TinyUrlController(mockService);
        String randomUrl = "http://example.com/abc";
        when(mockService.generateTinyUrl(anyString())).thenReturn("http://tinyurl.com/123");
        String result = controller.testMethod();
        assertEquals("http://tinyurl.com/123", result);
        verify(mockService).generateTinyUrl(anyString());
    }

    @Test
    void generateRandomUrl() {
        TinyUrlService mockService = mock(TinyUrlService.class);
        TinyUrlController controller = new TinyUrlController(mockService);
        String randomUrl = controller.generateRandomUrl();
        assertTrue(randomUrl.startsWith("http://example.com/"));
        assertTrue(randomUrl.length() > "http://example.com/".length());
    }
}