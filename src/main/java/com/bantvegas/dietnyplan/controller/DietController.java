package com.bantvegas.dietnyplan.controller;

import com.bantvegas.dietnyplan.model.DietRequest;
import com.bantvegas.dietnyplan.service.DietService;
import com.bantvegas.dietnyplan.service.MailService;
import com.bantvegas.dietnyplan.service.PdfService;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class DietController {

    @Autowired
    private MailService mailService;

    @Autowired
    private PdfService pdfService;

    @Autowired
    private DietService dietService;

    // ✅ ROOT MAPPING - zobrazí formulár
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("dietRequest", new DietRequest());
        return "index";
    }

    // ✅ SPRACOVANIE FORMULÁRA S VALIDÁCIOU
    @PostMapping("/generate")
    public String handleForm(@Valid @ModelAttribute DietRequest dietRequest, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "index";
        }

        String plan = dietService.generatePlan(dietRequest);
        byte[] pdf = new byte[0];
        try {
            pdf = pdfService.generatePdf(plan);
            mailService.sendPdf(dietRequest.getEmail(), pdf);
        } catch (Exception e) {
            e.printStackTrace();
        }

        model.addAttribute("plan", plan);
        model.addAttribute("shoppingListHtml", "");
        return "vygenerovany";
    }

    @GetMapping("/success")
    public String success(Model model, @RequestParam("session_id") String sessionId) {
        try {
            Stripe.apiKey = System.getenv("STRIPE_SECRET_KEY");
            Session session = Session.retrieve(sessionId);
            String email = session.getCustomerEmail();

            System.out.println("✅ SUCCESS page loaded with session_id: " + sessionId);
            System.out.println("📧 Email z platby: " + email);

            String plan = dietService.generatePlanForEmail(email);
            byte[] pdf = pdfService.generatePdf(plan);
            mailService.sendPdf(email, pdf);

            model.addAttribute("message", "Platba úspešná! Plán ti bol odoslaný na email.");
            return "success";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", "Platba prebehla, ale plán sa nepodarilo odoslať.");
            return "success";
        }
    }
}
