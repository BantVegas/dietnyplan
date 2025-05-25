package com.bantvegas.dietnyplan.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    // Pre všetky cesty, ktoré nie sú API ani statické zdroje,
    // presmeruj na index.html (SPA entry point)
    @GetMapping({
            "/",
            "/{x:[\\w\\-]+}",
            "/{x:[\\w\\-]+}/{y:[\\w\\-]+}",
            "/{x:[\\w\\-]+}/{y:[\\w\\-]+}/{z:[\\w\\-]+}"
    })
    public String forwardSPA() {
        return "forward:/index.html";
    }
}
