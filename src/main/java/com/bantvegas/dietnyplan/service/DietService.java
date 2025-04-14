package com.bantvegas.dietnyplan.service;

import com.bantvegas.dietnyplan.model.DietRequest;
import com.bantvegas.dietnyplan.util.HtmlPlanFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class DietService {

    @Autowired
    private WebClient openAiWebClient;

    public String generatePlan(DietRequest req) {
        String prompt = buildPrompt(req);

        Mono<Map> response = openAiWebClient.post()
                .bodyValue(Map.of(
                        "model", "gpt-3.5-turbo",
                        "messages", List.of(Map.of(
                                "role", "user",
                                "content", prompt
                        ))
                ))
                .retrieve()
                .bodyToMono(Map.class);

        Map result = response.block();

        try {
            Map choice = (Map) ((List) result.get("choices")).get(0);
            Map message = (Map) choice.get("message");
            return (String) message.get("content");
        } catch (Exception e) {
            return "❌ Nepodarilo sa získať odpoveď od AI.";
        }
    }

    private String buildPrompt(DietRequest req) {
        return String.format("""
            Vygeneruj 7-dňový diétny plán pre %s (%d rokov), %.1f kg, %.1f cm, cieľ: %s.
            Preferencie: %s. Alergie/intolerancie: %s.

            Pre každý deň uveď 5 jedál: Raňajky, Desiata, Obed, Olovrant, Večera.

            Ku každému jedlu:
            - Napíš čo sa má zjesť
            - Uveď orientačné gramáže (napr. 80g kuracie prsia, 150g ryža)
            - A aj celkové kcal (napr. 450 kcal)

            Príklad:
            Raňajky: Ovsená kaša s banánom – 80g ovsených vločiek, 150ml mlieka (350 kcal)
            Desiata: Tvaroh s medom – 100g tvarohu, 10g medu (200 kcal)
            Obed: Kuracie prsia s ryžou – 150g kuracie mäso, 100g ryža, 50g zelenina (600 kcal)

            Zobrazenie nech je vo formáte:
            Deň 1:
            Raňajky: ...
            Desiata: ...
            Obed: ...
            Olovrant: ...
            Večera: ...
            """,
                req.getGender(), req.getAge(), req.getWeight(), req.getHeight(),
                req.getGoal(), req.getPreferences(), req.getAllergies()
        );
    }
}