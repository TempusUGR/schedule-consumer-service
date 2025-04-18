package com.calendarugr.schedule_consumer_service.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    
    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
            .filter(addApiKeyFilter());
    }
    
    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.build();
    }

    private ExchangeFilterFunction addApiKeyFilter() {
        return (request, next) -> {
            ClientRequest modifiedRequest = ClientRequest.from(request)
                    .header("X-Api-Key", System.getProperty("API_KEY")) 
                    .build();
            return next.exchange(modifiedRequest);
        };
    }
    
}
