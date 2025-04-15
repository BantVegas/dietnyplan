package com.bantvegas.dietnyplan.controller;

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

    private final PdfService pdfService;

    @GetMapping("/test-pdf")
    public ResponseEntity<byte[]> testPdf() throws Exception {
        String markdown = """
            ### Pondelok
            - **Raňajky**: Ovsená kaša s banánom
            - **Desiata**: Jablko
            - **Obed**: Kuracie mäso s ryžou
            - **Olovrant**: Jogurt
            - **Večera**: Tuniakový šalát
        """;

        byte[] pdf = pdfService.generatePdf(markdown);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=dietnyplan-test.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
