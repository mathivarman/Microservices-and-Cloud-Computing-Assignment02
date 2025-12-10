package com.travel.payment.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * WebClient configuration for inter-service communication
 */
@Configuration
public class WebClientConfig {

    @Value("${services.booking-service.url:http://localhost:8086}")
    private String bookingServiceUrl;

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient bookingServiceWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(bookingServiceUrl)
                .build();
    }
}
