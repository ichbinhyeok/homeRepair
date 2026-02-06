package com.livingcostcheck.home_repair.web;

import com.livingcostcheck.home_repair.service.VerdictEngineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class RootController {

    private final VerdictEngineService verdictEngineService;

    @GetMapping("/")
    public String index(Model model) {
        // Prepare Metros List for the Calculator
        List<String> metros = verdictEngineService.getMetroMasterData().getData().keySet().stream()
                .sorted()
                .toList();

        model.addAttribute("metros", metros);

        // Use the Product-Focused Hub Page
        return "pages/hub";
    }

    @GetMapping("/privacy-policy")
    public String privacyPolicy() {
        return "pages/disclaimer";
    }

    @GetMapping("/terms-of-service")
    public String termsOfService() {
        return "pages/disclaimer";
    }

    @GetMapping("/disclaimer")
    public String disclaimer() {
        return "pages/disclaimer";
    }
}
