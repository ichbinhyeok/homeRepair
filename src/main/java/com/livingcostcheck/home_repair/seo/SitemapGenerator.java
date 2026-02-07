package com.livingcostcheck.home_repair.seo;

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
 * Generates Strategic Seed Sitemap for pSEO
 */
@Slf4j
@Service
public class SitemapGenerator {

    private static final List<String> ALL_ERAS = Arrays.asList(
            "PRE_1950", "1950_1970", "1970_1980", "1980_1995", "1995_2010", "2010_PRESENT");

    private static final Set<String> TIER_1_METROS = Set.of(
            "ATLANTA_SANDY_SPRINGS_GA", "BOSTON_CAMBRIDGE_MA", "CHICAGO_NAPERVILLE_IL",
            "DALLAS_FT_WORTH_ARLINGTON_TX", "HOUSTON_THE_WOODLANDS_TX", "LOS_ANGELES_LONG_BEACH_CA",
            "MIAMI_FT_LAUDERDALE_FL", "PHILADELPHIA_PA_NJ", "PHOENIX_MESA_CHANDLER_AZ",
            "SAN_ANTONIO_NEW_BRAUNFELS_TX", "SAN_DIEGO_CHULA_VISTA_CA", "SAN_FRANCISCO_OAKLAND_CA",
            "SAN_JOSE_SUNNYVALE_CA", "SEATTLE_TACOMA_BELLEVUE_WA", "WASHINGTON_ARLINGTON_DC_VA");

    private static final String BASE_URL = "https://lifeverdict.com";

    public int generateSitemap(String outputPath, List<String> extraUrls) throws IOException {
        log.info("Generating Strategic Seed Sitemap...");

        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");

        String lastMod = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        int urlCount = 0;

        // 1. Core Pages (Seed)
        xml.append(buildUrlEntry(BASE_URL + "/", lastMod, "daily", "1.0"));
        xml.append(buildUrlEntry(BASE_URL + "/home-repair", lastMod, "weekly", "0.9"));
        xml.append(buildUrlEntry(BASE_URL + "/methodology", lastMod, "monthly", "0.8"));
        xml.append(buildUrlEntry(BASE_URL + "/about", lastMod, "monthly", "0.7"));
        xml.append(buildUrlEntry(BASE_URL + "/editorial-policy", lastMod, "monthly", "0.7"));
        urlCount += 5;

        // 2. Generated Pages (All Static Seed Pages)
        // Includes State Hubs and Verdict Pages (L1)
        if (extraUrls != null) {
            Set<String> uniqueUrls = new HashSet<>(extraUrls); // Deduplicate just in case
            for (String url : uniqueUrls) {
                // Determine priority based on type
                String priority = "0.8";
                String freq = "monthly";

                if (url.contains("/states/")) {
                    priority = "0.9";
                    freq = "weekly";
                }

                xml.append(buildUrlEntry(url, lastMod, freq, priority));
                urlCount++;
            }
        }

        xml.append("</urlset>");

        Path path = Paths.get(outputPath);
        Files.createDirectories(path.getParent());
        Files.writeString(path, xml.toString());

        log.info("Sitemap generated successfully: {} URLs (Seed Strategy applied)", urlCount);
        return urlCount;
    }

    private boolean isTier1Url(String url) {
        for (String metro : TIER_1_METROS) {
            if (url.contains(metro.toLowerCase().replace("_", "-")))
                return true;
        }
        return false;
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
