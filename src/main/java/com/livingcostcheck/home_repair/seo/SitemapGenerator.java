package com.livingcostcheck.home_repair.seo;

import com.livingcostcheck.home_repair.service.VerdictEngineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Generates sitemap.xml for all 702 static pSEO pages
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SitemapGenerator {

    private final VerdictEngineService verdictEngineService;

    private static final List<String> ALL_ERAS = Arrays.asList(
            "PRE_1950",
            "1950_1970",
            "1970_1980",
            "1980_1995",
            "1995_2010",
            "2010_PRESENT");

    private static final Set<String> TIER_1_METROS = Set.of(
            "ATLANTA_SANDY_SPRINGS_GA",
            "BOSTON_CAMBRIDGE_MA",
            "CHICAGO_NAPERVILLE_IL",
            "DALLAS_FT_WORTH_ARLINGTON_TX",
            "HOUSTON_THE_WOODLANDS_TX",
            "LOS_ANGELES_LONG_BEACH_CA",
            "MIAMI_FT_LAUDERDALE_FL",
            "PHILADELPHIA_PA_NJ",
            "PHOENIX_MESA_CHANDLER_AZ",
            "SAN_ANTONIO_NEW_BRAUNFELS_TX",
            "SAN_DIEGO_CHULA_VISTA_CA",
            "SAN_FRANCISCO_OAKLAND_CA",
            "SAN_JOSE_SUNNYVALE_CA",
            "SEATTLE_TACOMA_BELLEVUE_WA",
            "WASHINGTON_ARLINGTON_DC_VA");

    private static final String BASE_URL = "https://lifeverdict.com";

    /**
     * Generate sitemap.xml
     * 
     * @param outputPath Path to write sitemap.xml (e.g.,
     *                   "src/main/resources/static/sitemap.xml")
     * @return Number of URLs in sitemap
     */
    /**
     * Generate sitemap-core.xml (Seed Sitemap)
     * Only contains:
     * 1. Homepage & Hubs
     * 2. State Hubs (50)
     * 3. Tier 1 Metro Verdicts (15 * 6 = 90)
     * Total: ~140 URLs
     */
    public int generateSitemap(String outputPath) throws IOException {
        log.info("Generating sitemap-core.xml (Seed Only)...");

        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");

        String lastMod = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        int urlCount = 0;

        // 1. Core Pages
        xml.append(buildUrlEntry(BASE_URL + "/", lastMod, "daily", "1.0"));
        urlCount++;
        xml.append(buildUrlEntry(BASE_URL + "/home-repair", lastMod, "weekly", "0.9"));
        urlCount++;

        // 2. State Hubs (50)
        // Hardcoded list of states for now (or could be derived)
        List<String> states = Arrays.asList("tx", "ca", "fl", "ny", "pa", "il", "oh", "ga", "nc", "mi", "nj", "va", "wa", "az", "ma", "tn", "in", "mo", "md", "wi", "co", "mn", "sc", "al", "la", "ky", "or", "ok", "ct", "ut", "ia", "nv", "ar", "ms", "ks", "nm", "ne", "wv", "id", "hi", "nh", "me", "ri", "mt", "de", "sd", "nd", "ak", "vt", "wy");
        for (String state : states) {
            xml.append(buildUrlEntry(BASE_URL + "/home-repair/states/" + state, lastMod, "weekly", "0.8"));
            urlCount++;
        }

        // 3. Tier 1 Metro Verdicts only
        for (String metroCode : TIER_1_METROS) {
            // Check if metro exists in data to be safe
            if (verdictEngineService.getMetroMasterData().getData().containsKey(metroCode)) {
                for (String era : ALL_ERAS) {
                    String url = buildVerdictUrl(metroCode, era);
                    xml.append(buildUrlEntry(url, lastMod, "monthly", "0.7"));
                    urlCount++;
                }
            }
        }

        // 4. Glossary Pages
        int[] glossaryCount = {0};
        addGlossaryPages(xml, glossaryCount);
        urlCount += glossaryCount[0];

        // 5. Support Pages
        String[] supportPages = {"privacy-policy.html", "terms-of-service.html", "disclaimer.html", "home-repair/about.html", "home-repair/methodology.html", "home-repair/editorial-policy.html"};
        for(String page : supportPages) {
             xml.append(buildUrlEntry(BASE_URL + "/" + page, lastMod, "monthly", "0.5"));
             urlCount++;
        }

        xml.append("</urlset>");

        // Write to file (force filename to sitemap-core.xml if not already)
        if (outputPath.endsWith("sitemap.xml")) {
            outputPath = outputPath.replace("sitemap.xml", "sitemap-core.xml");
        }
        
        Path path = Paths.get(outputPath);
        Files.createDirectories(path.getParent());
        Files.writeString(path, xml.toString());

        log.info("Seed Sitemap generated successfully: {} URLs at {}", urlCount, outputPath);
        
        // Also generate a simple robots.txt pointing to this sitemap
        generateRobotsTxt(path.getParent(), "https://lifeverdict.com/sitemap-core.xml");
        
        return urlCount;
    }

    private void addGlossaryPages(StringBuilder xml, int[] count) {
         try {
            com.fasterxml.jackson.databind.JsonNode root = verdictEngineService.getRiskData();
            if (root != null && root.has("eras")) {
                Set<String> uniqueRisks = new HashSet<>();
                root.get("eras").forEach(eraNode -> {
                    if (eraNode.has("critical_risks")) {
                        eraNode.get("critical_risks").forEach(risk -> {
                            uniqueRisks.add(risk.get("item").asText());
                        });
                    }
                });

                String lastMod = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
                for (String riskCode : uniqueRisks) {
                    String slug = riskCode.toLowerCase().replace("_", "-");
                    String url = BASE_URL + "/home-repair/guides/" + slug + ".html";
                    xml.append(buildUrlEntry(url, lastMod, "monthly", "0.6"));
                    count[0]++;
                }
            }
        } catch (Exception e) {
            log.warn("Failed to add Glossary to Sitemap: {}", e.getMessage());
        }
    }

    private void generateRobotsTxt(Path dir, String sitemapUrl) throws IOException {
        String robots = "User-agent: *\nAllow: /\n\nSitemap: " + sitemapUrl;
        Files.writeString(dir.resolve("robots.txt"), robots);
        log.info("Updated robots.txt to point to seed sitemap");
    }

    private String buildVerdictUrl(String metroCode, String era) {
        String metroSlug = metroCode.toLowerCase().replace("_", "-");
        String eraSlug = era.toLowerCase().replace("_", "-");
        return BASE_URL + "/home-repair/verdicts/" + metroSlug + "/" + eraSlug + ".html";
    }

    private String buildUrlEntry(String loc, String lastMod, String changeFreq, String priority) {
        return String.format(
                "  <url>\n    <loc>%s</loc>\n    <lastmod>%s</lastmod>\n    <changefreq>%s</changefreq>\n    <priority>%s</priority>\n  </url>\n",
                loc, lastMod, changeFreq, priority);
    }
}
