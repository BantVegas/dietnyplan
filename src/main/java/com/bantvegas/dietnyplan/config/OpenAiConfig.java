package com.bantvegas.dietnyplan.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class OpenAiConfig {

    @Value("${openai.api-key:}")
    private String rawApiKey; // Premenované pre prehľadnosť
    private String apiKey;    // Očistená verzia API kľúča

    @PostConstruct
    public void checkApiKey() {
        if (rawApiKey == null || rawApiKey.isBlank()) {
            System.err.println("❌ OPENAI_API_KEY nie je nastavený!");
            throw new IllegalStateException("Chýbajúci API kľúč");
        }

        // Odstránenie medzier a kontrolá formátu
        apiKey = rawApiKey.trim();
        if (!apiKey.startsWith("sk-")) {
            System.err.println("❌ Neplatný formát API kľúča (začína sa na 'sk-')?");
        }
        System.out.println("✅ OPENAI_API_KEY načítaný.");
    }

    @Bean
    public WebClient openAiWebClient() {
        return WebClient.builder()
                .baseUrl("https://api.openai.com") // Opravené: základná URL bez endpointu
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}