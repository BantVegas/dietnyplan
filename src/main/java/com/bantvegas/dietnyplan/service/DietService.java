package com.bantvegas.dietnyplan.service;

import com.bantvegas.dietnyplan.model.DietRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class DietService {

    public String generatePlan(DietRequest req) {
        try {
            // Tu voláš AI API a spracúvaš odpoveď (príkladový placeholder)
            RestTemplate restTemplate = new RestTemplate();
            String prompt = buildPrompt(req);

            Map result = restTemplate.postForObject("https://api.openai.com/v1/completions", Map.of(
                    "model", "gpt-3.5-turbo",
                    "prompt", prompt,
                    "max_tokens", 2000
            ), Map.class);

            Map choice = (Map) ((List) result.get("choices")).get(0);
            Map message = (Map) choice.get("message");
            return (String) message.get("content");
        } catch (Exception e) {
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
        demo.setPreferences(""); // môžeš prispôsobiť
        demo.setAllergies("");   // môžeš prispôsobiť
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
                - Email: %s

                Odpoveď v zrozumiteľnom a štruktúrovanom formáte.
                """,
                req.getName(),
                req.getAge(),
                req.getGender(),
                req.getHeight(),
                req.getWeight(),
                req.getGoal(),
                req.getPreferences(),
                req.getAllergies(),
                req.getEmail()
        );
    }
}
