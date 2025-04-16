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
@RequestMapping("/api/stripe")
public class StripeWebhookController {

    private final DietService dietService;
    private final PdfService pdfService;
    private final MailService mailService;

    @Value("${stripe.webhook-secret}")
    private String endpointSecret;

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String payload,
                                                @RequestHeader(value = "Stripe-Signature", required = false) String sigHeader) {
        log.info("📥 Stripe webhook prijatý...");

        try {
            Session session;
            String email;
            boolean isTest = (sigHeader == null || sigHeader.contains("test_signature"));

            if (isTest) {
                log.warn("⚠️ Testovací webhook – podpis ignorovaný.");
                session = new Session();
                session.setId("cs_test_1234567890");
                session.setCustomerEmail("test@dietnyplan.sk");
                session.setPaymentStatus("paid");
            } else {
                Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);

                if (!"checkout.session.completed".equals(event.getType())) {
                    return ResponseEntity.ok("Event ignored");
                }

                Object dataObject = event.getData().getObject();
                if (!(dataObject instanceof Session)) {
                    log.warn("⚠️ Webhook neobsahuje objekt typu Session.");
                    return ResponseEntity.badRequest().body("Invalid object type");
                }

                session = (Session) dataObject;
            }

            // 👉 Tu už máš platný `Session` (z reálneho eventu alebo z testu)
            email = session.getCustomerEmail();
            log.info("✅ Platba potvrdená pre: {}", email);

            String plan = dietService.generatePlanForEmail(email);
            String token = dietService.storePlan(plan, email);
            byte[] pdf = pdfService.generatePdf(plan);
            mailService.sendPdf(email, pdf);

            log.info("📤 PDF plán odoslaný e-mailom pre: {}", email);

            return ResponseEntity.ok("Webhook processed");

        } catch (SignatureVerificationException e) {
            log.error("❌ Neplatný podpis Stripe webhooku", e);
            return ResponseEntity.status(400).body("Invalid signature");

        } catch (Exception e) {
            log.error("❌ Chyba pri spracovaní Stripe webhooku", e);
            return ResponseEntity.status(500).body("Webhook error");
        }
    }
}
