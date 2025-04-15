package com.bantvegas.dietnyplan.controller;

import com.bantvegas.dietnyplan.model.DietRequest;
import com.bantvegas.dietnyplan.service.DietService;
import com.bantvegas.dietnyplan.service.MailService;
import com.bantvegas.dietnyplan.service.PdfService;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class DietController {

    @Autowired
    private MailService mailService;

    @Autowired
    private PdfService pdfService;

    @Autowired
    private DietService dietService;

    @GetMapping("/success")
    public String success(Model model, @RequestParam("session_id") String sessionId) {
        try {
            // nastav Stripe API key
            Stripe.apiKey = System.getenv("STRIPE_SECRET_KEY");

            Session session = Session.retrieve(sessionId);
            String email = session.getCustomerEmail();

            System.out.println("✅ SUCCESS page loaded with session_id: " + sessionId);
            System.out.println("📧 Email z platby: " + email);

            // Vygeneruj plán a pošli PDF
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
