package com.livingcostcheck.home_repair.web;

import com.livingcostcheck.home_repair.seo.StaticPageGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/admin/p-seo")
@RequiredArgsConstructor
public class AdministrativeController {

    private final StaticPageGeneratorService staticPageGeneratorService;

    @GetMapping("/generate")
    public String generate() {
        log.info("ADMIN: Triggering static page generation...");
        // Path relative to project root
        String outputPath = "src/main/resources/static/home-repair/verdicts";
        int count = staticPageGeneratorService.generateAllPages(outputPath).size();
        return "Generated " + count + " pages to " + outputPath;
    }
}
