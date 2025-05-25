package com.bantvegas.dietnyplan.controller;

import com.bantvegas.dietnyplan.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test")
@CrossOrigin(origins = {
        "https://www.dietnyplan.sk",
        "https://dietnyplan.sk",
        "https://dietnyplan-frontend.vercel.app",
        "http://localhost:5177"
})
public class TestEmailController {

    private final MailService mailService;

    @PostMapping("/send-email")
    public ResponseEntity<String> sendTestEmail(@RequestParam String email) {
        try {
            mailService.sendSimpleEmail(email, "Testovací e-mail", "Toto je testovací e-mail z dietnyplan backendu.");
            return ResponseEntity.ok("Testovací e-mail odoslaný na " + email);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Chyba pri odosielaní testovacieho e-mailu: " + e.getMessage());
        }
    }
}
