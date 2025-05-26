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
        dietService.storeDietRequest(req);
        return ResponseEntity.ok(Map.of("url", url));
    }

    @PostMapping("/generate-weekly-plan")
    public ResponseEntity<Map<String, String>> generateWeeklyPlan(@RequestBody DietRequest req) {
        dietService.storeDietRequest(req);
        String weeklyPlan = dietService.generateWeeklyPlan(req);
        return ResponseEntity.ok(Map.of("weeklyPlan", weeklyPlan));
    }

    // ďalšie API endpointy podľa potreby
}
