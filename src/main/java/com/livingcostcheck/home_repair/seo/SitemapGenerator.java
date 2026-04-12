package com.livingcostcheck.home_repair.seo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Generates Strategic Seed Sitemap for pSEO
 */
@Slf4j
@Service
public class SitemapGenerator {

    private static final String BASE_URL = "https://lifeverdict.com";
    private static final Set<String> INDEXABLE_URLS = Set.of(
            BASE_URL + "/home-repair/verdicts/states/tx.html",
            BASE_URL + "/home-repair/verdicts/states/fl.html",
            BASE_URL + "/home-repair/verdicts/pittsburgh-pa/pre-1950.html",
            BASE_URL + "/home-repair/verdicts/tulsa-ok/pre-1950.html",
            BASE_URL + "/home-repair/verdicts/little-rock-north-little-rock-ar/1950-1970.html",
            BASE_URL + "/home-repair/verdicts/chicago-naperville-il/1950-1970.html");

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
        urlCount += 2;

        // 2. Generated Pages (Winner-only)
        if (extraUrls != null) {
            Set<String> uniqueUrls = new HashSet<>(extraUrls); // Deduplicate just in case
            for (String url : uniqueUrls) {
                if (!INDEXABLE_URLS.contains(url)) {
                    continue;
                }
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

    private String buildUrlEntry(String loc, String lastMod, String changeFreq, String priority) {
        return String.format(
                "  <url>\n    <loc>%s</loc>\n    <lastmod>%s</lastmod>\n    <changefreq>%s</changefreq>\n    <priority>%s</priority>\n  </url>\n",
                loc, lastMod, changeFreq, priority);
    }
}
