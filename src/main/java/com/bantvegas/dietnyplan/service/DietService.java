package com.bantvegas.dietnyplan.service;

import com.bantvegas.dietnyplan.model.DietRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class DietService {

    private final WebClient openAiWebClient;

    // token → plán
    private final Map<String, String> planStorage = new ConcurrentHashMap<>();

    // email → token
    private final Map<String, String> emailToTokenMap = new ConcurrentHashMap<>();

    public String generatePlan(DietRequest req) {
        try {
            String prompt = buildPrompt(req);

            Map<String, Object> requestBody = Map.of(
                    "model", "gpt-4o",
                    "messages", List.of(
                            Map.of("role", "system", "content", "Si výživový poradca."),
                            Map.of("role", "user", "content", prompt)
                    ),
                    "max_tokens", 2000
            );

            Map<String, Object> response = openAiWebClient.post()
                    .uri("/v1/chat/completions")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(2)))
                    .block();

            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            String plan = (String) message.get("content");

            if (plan == null || plan.isBlank()) {
                throw new IllegalStateException("❌ AI vrátilo prázdny plán.");
            }

            return plan;

        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Nepodarilo sa získať plán od AI.";
        }
    }

    public String generatePlanForEmail(String email) {
        DietRequest demo = new DietRequest();
        demo.setName("Auto_generované");
        demo.setAge(30);
        demo.setGender("muž");
        demo.setHeight(175);
        demo.setWeight(78);
        demo.setGoal("schudnúť");
        demo.setPreferences("");
        demo.setAllergies("");
        demo.setEmail(email);

        return generatePlan(demo);
    }

    // ⬇️ upravená verzia
    public String storePlan(String plan, String email) {
        String token = UUID.randomUUID().toString();
        planStorage.put(token, plan);
        emailToTokenMap.put(email, token);
        return token;
    }

    public String getTokenByEmail(String email) {
        return emailToTokenMap.get(email);
    }

    public String getPlanByToken(String token) {
        return planStorage.get(token);
    }

    private String buildPrompt(DietRequest req) {
        return String.format("""
                Vygeneruj prosím diétny plán na 7 dní pre osobu s týmito údajmi:
                - Meno: %s
                - Vek: %d
                - Pohlavie: %s
                - Výška: %.2f cm
                - Váha: %.2f kg
                - Cieľ: %s
                - Preferencie: %s
                - Alergie: %s

                Na konci každého dňa vypíš zoznam použitých surovín v štýle:
                100g ovsené vločky, 1ks vajce, 200ml mandľové mlieko...

                Výsledok v štruktúrovanej forme (napr. Markdown).
                """,
                req.getName(),
                req.getAge(),
                req.getGender(),
                req.getHeight(),
                req.getWeight(),
                req.getGoal(),
                req.getPreferences(),
                req.getAllergies()
        );
    }

    public String testModelName() {
        try {
            Map<String, Object> requestBody = Map.of(
                    "model", "gpt-4o",
                    "messages", List.of(Map.of("role", "user", "content", "Ahoj")),
                    "max_tokens", 5
            );

            Map<String, Object> response = openAiWebClient.post()
                    .uri("/v1/chat/completions")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            return "✅ Použitý model: " + response.get("model");

        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Chyba pri overení modelu: " + e.getMessage();
        }
    }
}
