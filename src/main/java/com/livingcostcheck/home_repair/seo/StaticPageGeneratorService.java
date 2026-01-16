package com.livingcostcheck.home_repair.seo;

import com.livingcostcheck.home_repair.service.VerdictEngineService;
import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.*;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.TemplateOutput;
import gg.jte.output.StringOutput;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Generates 702 static HTML verdict pages for pSEO
 * 117 cities × 6 eras = 702 unique pages
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StaticPageGeneratorService {

    private final VerdictEngineService verdictEngineService;
    private final InternalLinkBuilder internalLinkBuilder;
    private final TemplateEngine templateEngine;

    // All eras to generate
    private static final List<String> ALL_ERAS = Arrays.asList(
            "PRE_1950",
            "1950_1970",
            "1970_1980",
            "1980_1995",
            "1995_2010",
            "2010_PRESENT");

    // Default budget for static pages (mid-range to get realistic verdicts)
    private static final double DEFAULT_BUDGET = 15000.0;
    private static final String DEFAULT_PURPOSE = "LIVING";

    /**
     * Generate all 702 static pages
     * 
     * @param outputBasePath Base path for output (e.g.,
     *                       "src/main/resources/static/verdicts")
     * @return Count of successfully generated pages
     */
    public int generateAllPages(String outputBasePath) {
        log.info("Starting pSEO static page generation...");

        // Get all metro codes from VerdictEngineService
        Map<String, ?> allMetros = verdictEngineService.getMetroMasterData().getData();
        List<String> metroCodes = new ArrayList<>(allMetros.keySet());

        int successCount = 0;
        int errorCount = 0;
        List<String> failedPages = new ArrayList<>();

        for (String metroCode : metroCodes) {
            for (String era : ALL_ERAS) {
                try {
                    generateSinglePage(metroCode, era, outputBasePath);
                    successCount++;

                    if (successCount % 50 == 0) {
                        log.info("Progress: {} pages generated...", successCount);
                    }
                } catch (Exception e) {
                    errorCount++;
                    String pageId = metroCode + "/" + era;
                    failedPages.add(pageId);
                    log.error("Failed to generate page: {} - Error: {}", pageId, e.getMessage());
                    // Continue with next page (don't break entire build)
                }
            }
        }

        log.info("pSEO Generation Complete!");
        log.info("Success: {} pages", successCount);
        log.info("Errors: {} pages", errorCount);

        if (!failedPages.isEmpty()) {
            log.warn("Failed pages: {}", failedPages);
        }

        return successCount;
    }

    /**
     * Generate a single static page
     */
    private void generateSinglePage(String metroCode, String era, String outputBasePath) throws IOException {
        // Create UserContext with default values
        UserContext context = UserContext.builder()
                .metroCode(metroCode)
                .era(era)
                .budget(DEFAULT_BUDGET)
                .purpose(DEFAULT_PURPOSE)
                .history(Collections.emptyList())
                .condition(null)
                .build();

        // Generate verdict
        Verdict verdict = verdictEngineService.generateVerdict(context);

        // Get internal links
        List<InternalLinkBuilder.InternalLink> eraLinks = internalLinkBuilder.getOtherErasInCity(metroCode, era);
        List<InternalLinkBuilder.InternalLink> cityLinks = internalLinkBuilder.getNearbyMetrosInEra(metroCode, era);

        // Prepare template data
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("metroCode", metroCode);
        templateData.put("metroName", formatMetroName(metroCode));
        templateData.put("era", era);
        templateData.put("eraName", formatEraName(era));
        templateData.put("verdict", verdict);
        templateData.put("eraLinks", eraLinks);
        templateData.put("cityLinks", cityLinks);
        templateData.put("baseUrl", "https://livingcostcheck.com");
        templateData.put("canonicalUrl", buildCanonicalUrl(metroCode, era));

        // Render HTML using JTE template
        StringOutput output = new StringOutput();
        templateEngine.render("seo/static-verdict.jte", templateData, output);
        String html = output.toString();

        // Minify HTML (simple minification)
        html = minifyHtml(html);

        // Write to file
        Path filePath = buildFilePath(outputBasePath, metroCode, era);
        Files.createDirectories(filePath.getParent());
        Files.writeString(filePath, html);

        log.debug("Generated: {}", filePath);
    }

    private Path buildFilePath(String basePath, String metroCode, String era) {
        String metroSlug = metroCode.toLowerCase().replace("_", "-");
        String eraSlug = era.toLowerCase().replace("_", "-");
        return Paths.get(basePath, metroSlug, eraSlug + ".html");
    }

    private String buildCanonicalUrl(String metroCode, String era) {
        String metroSlug = metroCode.toLowerCase().replace("_", "-");
        String eraSlug = era.toLowerCase().replace("_", "-");
        return "https://livingcostcheck.com/home-repair/verdicts/" + metroSlug + "/" + eraSlug + ".html";
    }

    private String formatMetroName(String metroCode) {
        // Convert AUSTIN_ROUND_ROCK_TX → Austin Round Rock TX
        String[] parts = metroCode.split("_");
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            // Last part is probably state code, keep it uppercase
            if (i == parts.length - 1 && part.length() == 2) {
                result.append(part);
            } else {
                // Capitalize first letter, lowercase rest
                result.append(part.substring(0, 1).toUpperCase())
                        .append(part.substring(1).toLowerCase());
            }
            if (i < parts.length - 1) {
                result.append(" ");
            }
        }

        return result.toString();
    }

    private String formatEraName(String era) {
        switch (era) {
            case "PRE_1950":
                return "Pre-1950 Era";
            case "1950_1970":
                return "1950s-1970s Era";
            case "1970_1980":
                return "1970s Era";
            case "1980_1995":
                return "1980s-1990s Era";
            case "1995_2010":
                return "1995-2010 Era";
            case "2010_PRESENT":
                return "2010-Present Era";
            default:
                return era;
        }
    }

    private String minifyHtml(String html) {
        // Simple minification: remove extra whitespace
        return html
                .replaceAll(">\\s+<", "><")
                .replaceAll("\\s{2,}", " ")
                .trim();
    }
}
