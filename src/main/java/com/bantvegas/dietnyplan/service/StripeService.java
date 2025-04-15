package com.bantvegas.dietnyplan.service;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StripeService {

    @Value("${stripe.secret-key}")
    private String stripeSecretKey;

    @Value("${stripe.success-url}")
    private String successUrl;

    @Value("${stripe.cancel-url}")
    private String cancelUrl;

    public String createCheckoutSession(String email) {
        try {
            Stripe.apiKey = stripeSecretKey;

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(successUrl)
                    .setCancelUrl(cancelUrl)
                    .setCustomerEmail(email)
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setPrice("price_1RDsuS11bNoUWC3y3yBih3s2")
                                    .setQuantity(1L)
                                    .build()
                    )
                    .build();

            Session session = Session.create(params);
            log.info("✅ Stripe Checkout session created: {}", session.getId());
            return session.getUrl();

        } catch (Exception e) {
            log.error("❌ Chyba pri vytváraní Stripe session", e);
            return null;
        }
    }
}
