package com.livingcostcheck.home_repair.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/home-repair")
public class PageController {

    @GetMapping("/methodology")
    public String methodology(Model model) {
        model.addAttribute("baseUrl", "https://livingcostcheck.com");
        return "pages/methodology";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("baseUrl", "https://livingcostcheck.com");
        return "pages/about";
    }

    @GetMapping("/editorial-policy")
    public String editorialPolicy(Model model) {
        model.addAttribute("baseUrl", "https://livingcostcheck.com");
        return "pages/editorial-policy";
    }

    @GetMapping("/data-sources")
    public String dataSources() {
        return "pages/data-sources";
    }

    @GetMapping("/disclaimer")
    public String disclaimer() {
        return "pages/disclaimer";
    }
}
