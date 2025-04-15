package com.bantvegas.dietnyplan.controller;

import com.bantvegas.dietnyplan.service.DietService;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.model.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StripeWebhookController {

    private final DietService dietService;

    @Value("${stripe.webhook-secret}")
    private String endpointSecret;

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload,
                                                      @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);

            if ("checkout.session.completed".equals(event.getType())) {
                Session session = (Session) event.getDataObjectDeserializer()
                        .getObject()
                        .orElseThrow();

                String email = session.getCustomerEmail();
                log.info("✅ Stripe platba potvrdená pre: {}", email);

                // Vygeneruj plán a ulož token
                String plan = dietService.generatePlanForEmail(email);
                String token = dietService.storePlan(plan);

                log.info("📄 Plán vygenerovaný a uložený pre: {}", email);
            }

            return ResponseEntity.ok("OK");

        } catch (Exception e) {
            log.error("❌ Chyba pri spracovaní Stripe webhooku", e);
            return ResponseEntity.badRequest().body("Webhook error");
        }
    }
}
