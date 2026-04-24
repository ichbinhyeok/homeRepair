package com.livingcostcheck.home_repair.seo;

import com.livingcostcheck.home_repair.web.AcquisitionSurface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Generates the current public sitemap for the tool-first product surface.
 */
@Slf4j
@Service
public class SitemapGenerator {

    private static final String BASE_URL = "https://lifeverdict.com";

    public int generateSitemap(String outputPath, List<String> extraUrls) throws IOException {
        log.info("Generating tool-only sitemap...");

        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");

        String lastMod = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        int urlCount = 0;

        for (AcquisitionSurface surface : AcquisitionSurface.indexableSurfaces()) {
            xml.append(buildUrlEntry(BASE_URL + surface.path(), lastMod, "weekly", "0.8"));
            urlCount++;
        }
        xml.append(buildUrlEntry(BASE_URL + "/home-repair", lastMod, "weekly", "1.0"));
        urlCount++;
        xml.append(buildUrlEntry(BASE_URL + "/for-buyer-agents", lastMod, "weekly", "0.7"));
        urlCount++;
        xml.append(buildUrlEntry(BASE_URL + "/sample-seller-credit-request-after-home-inspection", lastMod, "weekly",
                "0.7"));
        urlCount++;
        xml.append(buildUrlEntry(BASE_URL + "/fha-va-inspection-repairs-and-seller-credit", lastMod, "weekly", "0.7"));
        urlCount++;

        xml.append("</urlset>");

        Path path = Paths.get(outputPath);
        Files.createDirectories(path.getParent());
        Files.writeString(path, xml.toString());

        log.info("Sitemap generated successfully: {} URLs (tool surface only)", urlCount);
        return urlCount;
    }

    private String buildUrlEntry(String loc, String lastMod, String changeFreq, String priority) {
        return String.format(
                "  <url>\n    <loc>%s</loc>\n    <lastmod>%s</lastmod>\n    <changefreq>%s</changefreq>\n    <priority>%s</priority>\n  </url>\n",
                loc, lastMod, changeFreq, priority);
    }
}
