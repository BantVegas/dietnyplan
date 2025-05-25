package com.bantvegas.dietnyplan.controller;

import com.bantvegas.dietnyplan.service.MailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestEmailController {

    private final MailService mailService;

    public TestEmailController(MailService mailService) {
        this.mailService = mailService;
    }

    @GetMapping("/api/test-email")
    public ResponseEntity<String> testEmail() {
        try {
            // Pošli testovací email bez prílohy
            mailService.sendSimpleEmail("tvoj.email@example.com", "Testovací e-mail", "Toto je testovací e-mail z dietnyplan backendu.");
            return ResponseEntity.ok("Testovací e-mail odoslaný.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Chyba pri odosielaní testovacieho e-mailu: " + e.getMessage());
        }
    }
}
