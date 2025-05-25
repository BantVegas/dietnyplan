package com.bantvegas.dietnyplan.controller;

import com.bantvegas.dietnyplan.model.DietRequest;
import com.bantvegas.dietnyplan.service.DietService;
import com.bantvegas.dietnyplan.service.PdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final DietService dietService;
    private final PdfService pdfService;

    @GetMapping("/test-plan")
    public ResponseEntity<byte[]> testPlan() throws Exception {
        // Vytvor testovací požiadavku
        DietRequest req = new DietRequest();
        req.setName("Martin Test");
        req.setAge(30);
        req.setGender("muž");
        req.setHeight(180);
        req.setWeight(82);
        req.setGoal("schudnúť");
        req.setPreferences("žiadne");
        req.setAllergies("bez");

        // Vygeneruj plán
        String plan = dietService.generatePlan(req);

        // PDF (POZOR: dávame aj req)
        byte[] pdf = pdfService.generatePdf(plan, req);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=dietnyplan-test.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
