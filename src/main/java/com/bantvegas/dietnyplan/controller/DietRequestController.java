package com.bantvegas.dietnyplan.controller;

import com.bantvegas.dietnyplan.model.DietRequest;
import com.bantvegas.dietnyplan.service.DietService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/diet-request")
public class DietRequestController {

    private final DietService dietService;

    @PostMapping("/save")
    public ResponseEntity<Void> saveDietRequest(@RequestBody DietRequest req) {
        dietService.storeDietRequest(req);
        return ResponseEntity.ok().build();
    }
}
