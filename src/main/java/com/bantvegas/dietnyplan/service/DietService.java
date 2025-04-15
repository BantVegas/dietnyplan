package com.bantvegas.dietnyplan.service;

import com.bantvegas.dietnyplan.model.DietRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DietService {

    private final WebClient openAiWebClient;

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
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            res -> res.bodyToMono(String.class).map(body -> {
                                System.err.println("❌ Chyba OpenAI: " + body);
                                return new RuntimeException("OpenAI error: " + body);
                            })
                    )
                    .bodyToMono(Map.class)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(2)))
                    .block();

            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");

            return (String) message.get("content");

        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Nepodarilo sa získať odpoveď od AI.";
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

                Výsledok v štruktúrovanej forme.
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

    // 🔍 TEST ENDPOINT – diagnostika použitého modelu
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

            String modelUsed = (String) response.get("model");
            return "✅ Použitý model: " + modelUsed;

        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Chyba pri overení modelu: " + e.getMessage();
        }
    }
}
