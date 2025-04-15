package com.bantvegas.dietnyplan.controller;

import com.bantvegas.dietnyplan.service.DietService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
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
    public ResponseEntity<String> handleWebhook(@RequestBody String payload,
                                                @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);

            if ("checkout.session.completed".equals(event.getType())) {
                Session session = (Session) event.getDataObjectDeserializer()
                        .getObject()
                        .orElseThrow();

                String email = session.getCustomerEmail();
                log.info("✅ Platba potvrdená pre: {}", email);

                String plan = dietService.generatePlanForEmail(email);
                String token = dietService.storePlan(plan);

                log.info("📝 Plán vygenerovaný pre {} – token: {}", email, token);
            }

            return ResponseEntity.ok("Webhook processed");

        } catch (SignatureVerificationException e) {
            log.error("❌ Neplatný podpis webhooku", e);
            return ResponseEntity.status(400).body("Invalid signature");
        } catch (Exception e) {
            log.error("❌ Chyba vo webhook spracovaní", e);
            return ResponseEntity.status(500).body("Webhook error");
        }
    }
}
