package com.bantvegas.dietnyplan.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // Povoliť FE domény pre všetky API endpointy
                registry.addMapping("/api/**")
                        .allowedOrigins(
                                "https://www.dietnyplan.sk",
                                "https://dietnyplan.sk",
                                "https://dietnyplan-frontend.vercel.app")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(false);

                // Povoliť Stripe webhook endpoint zo všetkých originov (Stripe webhooky môžu byť odkiaľkoľvek)
                registry.addMapping("/api/stripe/webhook")
                        .allowedOrigins("*")
                        .allowedMethods("POST", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(false);
            }
        };
    }
}
