package com.bantvegas.dietnyplan.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/")
    public String index() {
        return "index"; // => src/main/resources/templates/index.html
    }

    @GetMapping("/ako-to-funguje")
    public String akoToFunguje() {
        return "ako-to-funguje"; // => src/main/resources/templates/ako-to-funguje.html
    }

    @GetMapping("/kontakt")
    public String kontakt() {
        return "kontakt"; // => src/main/resources/templates/kontakt.html
    }
}
