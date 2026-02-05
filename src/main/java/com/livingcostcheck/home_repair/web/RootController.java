package com.livingcostcheck.home_repair.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootController {

    @GetMapping("/")
    public String index() {
        // Redirect root to home-repair hub for now, or render a landing page
        return "redirect:/home-repair"; 
    }

    @GetMapping("/privacy-policy")
    public String privacyPolicy() {
        return "pages/privacy-policy";
    }

    @GetMapping("/terms-of-service")
    public String termsOfService() {
        return "pages/terms-of-service";
    }

    @GetMapping("/disclaimer")
    public String disclaimer() {
        return "pages/disclaimer";
    }
}
