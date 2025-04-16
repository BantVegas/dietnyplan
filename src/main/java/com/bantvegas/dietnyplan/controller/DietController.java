package com.bantvegas.dietnyplan.controller;

import com.bantvegas.dietnyplan.model.DietRequest;
import com.bantvegas.dietnyplan.service.DietService;
import com.bantvegas.dietnyplan.service.MailService;
import com.bantvegas.dietnyplan.service.PdfService;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class DietController {

    @Autowired private MailService mailService;
    @Autowired private PdfService pdfService;
    @Autowired private DietService dietService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("dietRequest", new DietRequest());
        return "index";
    }

    @PostMapping("/generate")
    public String handleForm(@Valid @ModelAttribute DietRequest dietRequest,
                             BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "index";
        }

        try {
            String plan = dietService.generatePlan(dietRequest);
            String token = dietService.storePlan(plan, dietRequest.getEmail());
            byte[] pdf = pdfService.generatePdf(plan);
            mailService.sendPdf(dietRequest.getEmail(), pdf);


            model.addAttribute("plan", plan);
            model.addAttribute("pdfToken", token);
            return "vygenerovany";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", "❌ Nepodarilo sa vygenerovať plán.");
            return "error";
        }
    }

    @GetMapping("/success")
    public String success(Model model, @RequestParam("session_id") String sessionId) {
        try {
            Stripe.apiKey = System.getenv("STRIPE_SECRET_KEY");
            Session session = Session.retrieve(sessionId);
            String email = session.getCustomerEmail();

            String plan = dietService.generatePlanForEmail(email);
            String token = dietService.storePlan(plan, email);


            byte[] pdf = pdfService.generatePdf(plan);
            mailService.sendPdf(email, pdf);

            model.addAttribute("message", "✅ Platba prebehla úspešne! Plán ti bol odoslaný.");
            model.addAttribute("plan", plan);
            model.addAttribute("pdfToken", token);
            return "vygenerovany";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", "Platba prebehla, ale plán sa nepodarilo odoslať.");
            return "success";
        }
    }

    @GetMapping("/download-pdf")
    public ResponseEntity<byte[]> downloadPdf(@RequestParam("token") String token) {
        String plan = dietService.getPlanByToken(token);
        if (plan == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            byte[] pdf = pdfService.generatePdf(plan);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=dietny-plan.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/cancel")
    public String cancel() {
        return "cancel";
    }

    @GetMapping("/check-model")
    @ResponseBody
    public String checkModel() {
        return dietService.testModelName();
    }
}
