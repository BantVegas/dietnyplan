package com.bantvegas.dietnyplan.controller;

import com.bantvegas.dietnyplan.model.DietRequest;
import com.bantvegas.dietnyplan.service.DietService;
import com.bantvegas.dietnyplan.service.StripeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApiController {

    private final DietService dietService;
    private final StripeService stripeService;

    @PostMapping("/create-checkout-session")
    public ResponseEntity<Map<String, String>> createCheckoutSession(@RequestBody DietRequest req) {
        String url = stripeService.createCheckoutSession(req);
        // uloz dietRequest v DietService alebo kde treba
        dietService.storeDietRequest(req);
        return ResponseEntity.ok(Map.of("url", url));
    }

    // ďalšie API endpointy podľa potreby
}
