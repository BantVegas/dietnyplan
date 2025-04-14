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

    @GetMapping("/") // ← TOTO je to, čo ti chýba
    public String showForm(Model model) {
        model.addAttribute("dietRequest", new DietRequest());
        return "form"; // zobrazí templates/form.html
    }

    @PostMapping("/generate")
    public String redirectToStripe(@ModelAttribute DietRequest dietRequest, Model model) {
        try {
            String checkoutUrl = stripeService.createCheckoutSession(dietRequest.getEmail());
            return "redirect:" + checkoutUrl;
        } catch (Exception e) {
            model.addAttribute("error", "Nepodarilo sa vytvoriť Stripe checkout session.");
            return "form";
        }
    }

    @GetMapping("/success")
    public String success(Model model, @RequestParam("session_id") String sessionId) {
        model.addAttribute("message", "Platba úspešná! Plán ti bol odoslaný na email.");
        return "success";
    }

    @GetMapping("/cancel")
    public String cancel() {
        return "cancel";
    }
}
