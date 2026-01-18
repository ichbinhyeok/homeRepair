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

    private static final String BASE_URL = "https://livingcostcheck.com";

    /**
     * Generate sitemap.xml
     * 
     * @param outputPath Path to write sitemap.xml (e.g.,
     *                   "src/main/resources/static/sitemap.xml")
     * @return Number of URLs in sitemap
     */
    public int generateSitemap(String outputPath) throws IOException {
        log.info("Generating sitemap.xml...");

        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");

        // Get all metro codes
        Map<String, ?> allMetros = verdictEngineService.getMetroMasterData().getData();
        List<String> metroCodes = new ArrayList<>(allMetros.keySet());

        // SORT: Tier 1 cities first, then alphabetical
        metroCodes.sort((a, b) -> {
            boolean aIsTier1 = TIER_1_METROS.contains(a);
            boolean bIsTier1 = TIER_1_METROS.contains(b);
            if (aIsTier1 && !bIsTier1)
                return -1;
            if (!aIsTier1 && bIsTier1)
                return 1;
            return a.compareTo(b);
        });

        String lastMod = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        int urlCount = 0;

        // Add homepage
        xml.append(buildUrlEntry(BASE_URL, lastMod, "weekly", "1.0"));
        urlCount++;

        // Add all verdict pages
        for (String metroCode : metroCodes) {
            boolean isTier1 = TIER_1_METROS.contains(metroCode);
            String priority = isTier1 ? "1.0" : "0.8";

            for (String era : ALL_ERAS) {
                String url = buildVerdictUrl(metroCode, era);
                xml.append(buildUrlEntry(url, lastMod, "monthly", priority));
                urlCount++;
            }
        }

        xml.append("</urlset>");

        // Write to file
        Path path = Paths.get(outputPath);
        Files.createDirectories(path.getParent());
        Files.writeString(path, xml.toString());

        log.info("Sitemap generated successfully: {} URLs", urlCount);
        return urlCount;
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
