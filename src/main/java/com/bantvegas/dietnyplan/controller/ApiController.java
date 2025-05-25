package com.bantvegas.dietnyplan.controller;

import com.bantvegas.dietnyplan.model.DietRequest;
import com.bantvegas.dietnyplan.service.DietService;
import com.bantvegas.dietnyplan.service.StripeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * API Controller - REST endpointy pre FE
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApiController {

    private final DietService dietService;
    private final StripeService stripeService;

    /**
     * 1. Vytvorenie Stripe Checkout session
     */
    @PostMapping("/create-checkout-session")
    public ResponseEntity<Map<String, String>> createCheckoutSession(@RequestBody DietRequest req) {
        String url = stripeService.createCheckoutSession(req);
        return ResponseEntity.ok(Map.of("url", url));
    }

    /**
     * 2. Po platbe na frontende získaj info podľa session_id
     */
    @GetMapping("/stripe/session-info")
    public ResponseEntity<Map<String, Object>> getSessionInfo(@RequestParam String session_id) {
        return ResponseEntity.ok(stripeService.getSessionInfo(session_id));
    }

    /**
     * 3. (Voliteľné) Stiahni PDF po zaplatenej platbe podľa tokenu
     */
    @GetMapping("/download-pdf")
    public ResponseEntity<byte[]> downloadPdf(@RequestParam String token) {
        String plan = dietService.getPlanByToken(token);
        if (plan == null) return ResponseEntity.notFound().build();

        String email = dietService.getEmailByToken(token);
        if (email == null) return ResponseEntity.notFound().build();

        DietRequest req = dietService.getRequestByEmail(email);
        if (req == null) return ResponseEntity.notFound().build();

        byte[] pdf = dietService.generatePdfFromPlan(plan, req);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=dietnyplan.pdf")
                .body(pdf);
    }
}
