package com.bantvegas.dietnyplan.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    // Forward na index.html pre rôzne úrovne SPA routingu (napr. /a, /a/b, /a/b/c ...)
    @GetMapping({"/", "/{x:[\\w\\-]+}", "/{x:^(?!api|static|assets|favicon).*}/{y:[\\w\\-]+}", "/{x:^(?!api|static|assets|favicon).*}/{y:[\\w\\-]+}/{z:[\\w\\-]+}"})
    public String forwardSPA() {
        return "forward:/index.html";
    }
}
