package com.travel.booking.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * WebClient configuration for User and Notification services
 */
@Configuration
public class WebClientConfig {

    @Value("${services.user-service.url:http://localhost:8081}")
    private String userServiceUrl;

    @Value("${services.notification-service.url:http://localhost:8084}")
    private String notificationServiceUrl;

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient userServiceWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(userServiceUrl)
                .build();
    }

    @Bean
    public WebClient notificationServiceWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(notificationServiceUrl)
                .build();
    }
}
