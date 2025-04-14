package com.bantvegas.dietnyplan.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class OpenAiConfig {

    @Value("${openai.api-key:}")
    private String apiKey;

    @PostConstruct
    public void checkApiKey() {
        if (apiKey == null || apiKey.isBlank()) {
            System.out.println("❌ OPENAI_API_KEY NIE JE nastavený!");
        } else {
            System.out.println("✅ OPENAI_API_KEY načítaný.");
        }
    }

    @Bean
    public WebClient openAiWebClient() {
        return WebClient.builder()
                .baseUrl("https://api.openai.com/v1/chat/completions")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
