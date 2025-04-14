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

    public String createCheckoutSession(String email) throws Exception {
        // Nastavenie API kľúča
        Stripe.apiKey = stripeSecretKey;

        // Vytvorenie Stripe Checkout session
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setPrice("price_1RDpPP11bNoUWC3yE5EUcgl3") // ← tvoje LIVE Price ID
                                .setQuantity(1L)
                                .build()
                )
                .setCustomerEmail(email)
                .build();

        Session session = Session.create(params);
        return session.getUrl();
    }
}
