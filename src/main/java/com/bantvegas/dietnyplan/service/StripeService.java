package com.bantvegas.dietnyplan.service;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
                    .setMode(SessionCreateParams.Mode.PAYMENT) // ✅ jednorazová platba
                    .setSuccessUrl(successUrl)
                    .setCancelUrl(cancelUrl)
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setPrice("price_1RDsuS11bNoUWC3y3yBih3s2") // ✅ nový one-time price ID
                                    .setQuantity(1L)
                                    .build()
                    )
                    .setCustomerEmail(email)
                    .build();

            Session session = Session.create(params);
            System.out.println("✅ Stripe Checkout URL: " + session.getUrl());
            return session.getUrl();

        } catch (Exception e) {
            System.out.println("❌ Chyba pri vytváraní Stripe session: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
