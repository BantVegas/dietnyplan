package com.bantvegas.dietnyplan.service;

import com.bantvegas.dietnyplan.model.DietRequest;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct; // <--- DÔLEŽITÉ!

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StripeService {

    @Value("${stripe.secret-key}")
    private String stripeSecretKey;

    @Value("${stripe.success-url}")
    private String successUrl;

    @Value("${stripe.cancel-url}")
    private String cancelUrl;

    @Value("${stripe.price-id}")
    private String priceId;

    // Debug výpis, čo načítal Spring z properties/ENV
    @PostConstruct
    public void debugStripeUrls() {
        System.out.println("=== STRIPE SECRET KEY: " + (stripeSecretKey != null && stripeSecretKey.length() > 6 ? stripeSecretKey.substring(0,6) + "..." : "NOT SET"));
        System.out.println("=== STRIPE SUCCESS URL: " + successUrl);
        System.out.println("=== STRIPE CANCEL URL:  " + cancelUrl);
        System.out.println("=== STRIPE PRICE ID:    " + priceId);
    }

    public String createCheckoutSession(DietRequest req) {
        Stripe.apiKey = stripeSecretKey;

        try {
            // Priprav metadáta
            Map<String, String> metadata = new HashMap<>();
            metadata.put("name", req.getName());
            metadata.put("age", String.valueOf(req.getAge()));
            metadata.put("gender", req.getGender());
            metadata.put("weight", String.valueOf(req.getWeight()));
            metadata.put("height", String.valueOf(req.getHeight()));
            metadata.put("goal", req.getGoal());
            if (req.getPreferences() != null) {
                metadata.put("preferences", req.getPreferences());
            }
            if (req.getAllergies() != null) {
                metadata.put("allergies", req.getAllergies());
            }

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(successUrl)
                    .setCancelUrl(cancelUrl)
                    .setCustomerEmail(req.getEmail())
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setPrice(priceId)
                                    .setQuantity(1L)
                                    .build()
                    )
                    .putAllMetadata(metadata)
                    .build();

            Session session = Session.create(params);
            return session.getUrl();

        } catch (Exception e) {
            throw new RuntimeException("Stripe checkout session failed", e);
        }
    }

    // Info o session (voliteľné, použiteľné v Reacte po návrate zo Stripe)
    public Map<String, Object> getSessionInfo(String sessionId) {
        Stripe.apiKey = stripeSecretKey;
        Map<String, Object> out = new HashMap<>();
        try {
            Session session = Session.retrieve(sessionId);
            out.put("email", session.getCustomerEmail());
            out.put("payment_status", session.getPaymentStatus());
            out.put("metadata", session.getMetadata());
        } catch (Exception e) {
            out.put("error", e.getMessage());
        }
        return out;
    }
}
