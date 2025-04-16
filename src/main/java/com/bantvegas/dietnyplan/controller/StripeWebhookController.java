package com.bantvegas.dietnyplan.controller;

import com.bantvegas.dietnyplan.service.DietService;
import com.bantvegas.dietnyplan.service.MailService;
import com.bantvegas.dietnyplan.service.PdfService;
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
@RequestMapping("/api/stripe") // 👉 toto zabezpečí správny prefix
public class StripeWebhookController {

    private final DietService dietService;
    private final PdfService pdfService;
    private final MailService mailService;

    @Value("${stripe.webhook-secret}")
    private String endpointSecret;

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String payload,
                                                @RequestHeader("Stripe-Signature") String sigHeader) {
        log.info("📥 Stripe webhook prijatý...");

        try {
            Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);

            if ("checkout.session.completed".equals(event.getType())) {
                Session session = (Session) event.getDataObjectDeserializer()
                        .getObject()
                        .orElseThrow();

                String email = session.getCustomerEmail();
                log.info("✅ Platba potvrdená pre: {}", email);

                // 1. Vygeneruj plán
                String plan = dietService.generatePlanForEmail(email);

                // 2. Ulož plán + token (ak používaš)
                String token = dietService.storePlan(plan, email);

                // 3. Vygeneruj PDF
                byte[] pdf = pdfService.generatePdf(plan);

                // 4. Pošli email
                mailService.sendPdf(email, pdf);

                log.info("📤 PDF plán odoslaný e-mailom pre: {}", email);
            }

            return ResponseEntity.ok("Webhook processed");

        } catch (SignatureVerificationException e) {
            log.error("❌ Neplatný podpis webhooku", e);
            return ResponseEntity.status(400).body("Invalid signature");
        } catch (Exception e) {
            log.error("❌ Chyba pri spracovaní Stripe webhooku", e);
            return ResponseEntity.status(500).body("Webhook error");
        }
    }
}
