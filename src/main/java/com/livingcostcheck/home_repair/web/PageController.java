package com.livingcostcheck.home_repair.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/home-repair")
public class PageController {

    @GetMapping("/methodology")
    public String methodology() {
        return "pages/methodology";
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
