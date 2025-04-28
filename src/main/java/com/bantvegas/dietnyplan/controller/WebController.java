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

    @GetMapping("/onas")
    public String onas() {
        return "onas"; // => src/main/resources/templates/onas.html
    }

    @GetMapping("/blog")
    public String blog() {
        return "blog"; // => src/main/resources/templates/blog.html
    }

    @GetMapping("/privacy-policy")
    public String privacyPolicy() {
        return "privacy-policy"; // => src/main/resources/templates/privacy-policy.html
    }

    @GetMapping("/terms")
    public String terms() {
        return "terms"; // => src/main/resources/templates/terms.html
    }
}
