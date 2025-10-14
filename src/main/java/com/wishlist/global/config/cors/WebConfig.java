package com.wishlist.global.config.cors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
public class WebConfig {

    private final String[] allowedOrigins;

    public WebConfig() {
        String origins = System.getProperty("ALLOWED_ORIGINS");
        this.allowedOrigins = Arrays.stream(origins.split(","))
                                    .map(String::trim)
                                    .toArray(String[]::new);
    }

    @Bean
    public String[] allowedOrigins() {
        return allowedOrigins;
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {

            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins(allowedOrigins)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
            }
        };
    }
}