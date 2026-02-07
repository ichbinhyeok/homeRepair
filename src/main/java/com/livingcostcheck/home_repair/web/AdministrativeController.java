package com.livingcostcheck.home_repair.web;

import com.livingcostcheck.home_repair.seo.StaticPageGeneratorService;
import com.livingcostcheck.home_repair.service.VerdictEngineService;
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
    private final VerdictEngineService verdictEngineService;
    private final com.livingcostcheck.home_repair.seo.SitemapGenerator sitemapGenerator;

    @GetMapping("/generate")
    public String generate() {
        log.info("ADMIN: Triggering static page generation...");
        // Path relative to project root
        String outputPath = "src/main/resources/static/home-repair/verdicts";

        var masterData = verdictEngineService.getMetroMasterData();
        if (masterData == null) {
            return "ERROR: MetroMasterData is NULL";
        }
        if (masterData.getData() == null) {
            return "ERROR: MetroMasterData.getData() is NULL";
        }
        log.info("DEBUG: Metro Data Size: {}", masterData.getData().size());

        java.util.List<String> results = staticPageGeneratorService.generateAllPages(outputPath);
        long errorCount = results.stream().filter(s -> s.startsWith("ERROR")).count();

        if (errorCount > 0) {
            String firstError = results.stream().filter(s -> s.startsWith("ERROR")).findFirst().orElse("Unknown Error");
            return "FAILED: " + errorCount + " errors. First Error: " + firstError;
        }

        try {
            // Sitemap path relative to project root (same level as 'static')
            String sitemapPath = "src/main/resources/static/sitemap.xml";
            int sitemapCount = sitemapGenerator.generateSitemap(sitemapPath, results);

            return "Generated " + results.size() + " pages to " + outputPath +
                    " (Metro Count: " + masterData.getData().size() + ").\n" +
                    "Sitemap updated: " + sitemapCount + " URLs.";
        } catch (Exception e) {
            log.error("Sitemap generation failed", e);
            return "Generated pages but Sitemap FAILED: " + e.getMessage();
        }
    }
}
