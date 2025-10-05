package com.yolifay.eventservice.client;

import com.yolifay.eventservice.exception.ConflictException;
import com.yolifay.eventservice.exception.DataNotFoundException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
    @Bean
    public feign.codec.ErrorDecoder errorDecoder() {
        return (methodKey, response) -> switch (response.status()) {
            case 404 -> new DataNotFoundException("Upstream 404");
            case 409 -> new ConflictException("Upstream conflict");
            case 502, 503, 504 -> new IllegalStateException("Upstream unavailable: " + response.status());
            default -> new IllegalStateException("Upstream error: " + response.status());
        };
    }
}


