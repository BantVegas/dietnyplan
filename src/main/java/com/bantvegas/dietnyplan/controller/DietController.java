package com.bantvegas.dietnyplan.controller;

import com.bantvegas.dietnyplan.model.DietRequest;
import com.bantvegas.dietnyplan.service.StripeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class DietController {

    private final StripeService stripeService;

    public DietController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @GetMapping("/")
    public String showForm(Model model) {
        model.addAttribute("dietRequest", new DietRequest());
        return "form"; // zobrazí templates/form.html
    }

    @PostMapping("/generate")
    public String redirectToStripe(@ModelAttribute DietRequest dietRequest, Model model) {
        try {
            String email = dietRequest.getEmail();
            System.out.println("🟡 Vygenerovaný email: " + email);

            String checkoutUrl = stripeService.createCheckoutSession(email);
            System.out.println("➡️ checkoutUrl = " + checkoutUrl);

            if (checkoutUrl != null && checkoutUrl.startsWith("https://")) {
                return "redirect:" + checkoutUrl;
            } else {
                model.addAttribute("error", "Nepodarilo sa vytvoriť Stripe Checkout session.");
                return "form";
            }

        } catch (Exception e) {
            System.out.println("❌ Výnimka pri vytváraní Stripe session: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Nastala chyba pri spracovaní požiadavky.");
            return "form";
        }
    }

    @GetMapping("/success")
    public String success(Model model, @RequestParam("session_id") String sessionId) {
        System.out.println("✅ SUCCESS page loaded with session_id: " + sessionId);
        model.addAttribute("message", "Platba úspešná! Plán ti bol odoslaný na email.");
        return "success";
    }

    @GetMapping("/cancel")
    public String cancel() {
        System.out.println("⚠️ Platba bola zrušená používateľom.");
        return "cancel";
    }
}
