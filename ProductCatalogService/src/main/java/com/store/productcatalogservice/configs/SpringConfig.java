package com.store.productcatalogservice.configs;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootConfiguration
public class SpringConfig {
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplateBuilder().build();
    }
}
