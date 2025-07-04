package com.bantvegas.dietnyplan.controller;

import com.bantvegas.dietnyplan.model.DietRequest;
import com.bantvegas.dietnyplan.service.DietService;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class DietController {

    private final DietService dietService;

    @Value("${stripe.secret-key}")
    private String stripeSecretKey;

    @GetMapping("/success")
    public String success(@RequestParam("session_id") String sessionId, Model model) {
        try {
            Stripe.apiKey = stripeSecretKey;
            Session session = Session.retrieve(sessionId);
            String email = session.getCustomerEmail();
            model.addAttribute("message", "✅ Platba prebehla úspešne! Plán bol odoslaný na e-mail: " + email);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", "❌ Nepodarilo sa overiť platbu.");
            return "error";
        }
    }

    @GetMapping("/download-pdf")
    public ResponseEntity<byte[]> downloadPdf(@RequestParam String token) {
        String plan = dietService.getPlanByToken(token);
        if (plan == null) {
            return ResponseEntity.notFound().build();
        }
        String email = dietService.getEmailByToken(token);
        DietRequest req = dietService.getRequestByEmail(email);
        if (req == null) {
            return ResponseEntity.notFound().build();
        }
        byte[] pdf = dietService.generatePdfFromPlan(plan, req);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=dietny-plan.pdf")
                .body(pdf);
    }
}
