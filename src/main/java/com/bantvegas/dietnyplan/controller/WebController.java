package com.bantvegas.dietnyplan.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    // Forward len pre ne-API a ne-static cesty (teda iba frontendové routy)
    @GetMapping({
            "/",
            "/{x:[\\w\\-]+}",
            "/{x:^(?!api|static|assets|favicon).*}/{y:[\\w\\-]+}",
            "/{x:^(?!api|static|assets|favicon).*}/{y:[\\w\\-]+}/{z:[\\w\\-]+}"
    })
    public String forwardSPA() {
        return "forward:/index.html";
    }
}
