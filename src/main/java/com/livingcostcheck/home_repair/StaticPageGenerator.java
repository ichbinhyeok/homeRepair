package com.livingcostcheck.home_repair;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.livingcostcheck.home_repair.seo.InternalLinkBuilder;
import com.livingcostcheck.home_repair.seo.SitemapGenerator;
import com.livingcostcheck.home_repair.seo.StaticPageGeneratorService;
import com.livingcostcheck.home_repair.service.VerdictEngineService;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.DirectoryCodeResolver;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Standalone executable to generate all pSEO static pages
 * Run with: ./gradlew generateStaticPages
 */
public class StaticPageGenerator {

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║  pSEO Static Page Generator - Home Repair Verdict Engine  ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();

        try {
            // Initialize dependencies manually (no Spring context)
            ResourceLoader resourceLoader = new DefaultResourceLoader();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            // Initialize VerdictEngineService
            VerdictEngineService verdictService = new VerdictEngineService(resourceLoader, objectMapper);
            verdictService.loadData();
            System.out.println("✓ Verdict engine data loaded");

            // Initialize JTE template engine
            Path templatePath = Paths.get("src/main/resources/jte");
            DirectoryCodeResolver codeResolver = new DirectoryCodeResolver(templatePath);
            TemplateEngine templateEngine = TemplateEngine.create(codeResolver, ContentType.Html);
            System.out.println("✓ Template engine initialized");

            // Initialize pSEO services
            InternalLinkBuilder linkBuilder = new InternalLinkBuilder();
            StaticPageGeneratorService pageGenerator = new StaticPageGeneratorService(
                    verdictService, linkBuilder, templateEngine);
            SitemapGenerator sitemapGenerator = new SitemapGenerator(verdictService);

            System.out.println();
            System.out.println("────────────────────────────────────────────────────────────");
            System.out.println("Phase 1: Generating 702 Static Verdict Pages");
            System.out.println("────────────────────────────────────────────────────────────");
            System.out.println();

            // Generate all pages
            String outputPath = "src/main/resources/static/verdicts";
            int pageCount = pageGenerator.generateAllPages(outputPath);

            System.out.println();
            System.out.println("✓ Generated " + pageCount + " pages to: " + outputPath);

            System.out.println();
            System.out.println("────────────────────────────────────────────────────────────");
            System.out.println("Phase 2: Generating Sitemap");
            System.out.println("────────────────────────────────────────────────────────────");
            System.out.println();

            // Generate sitemap
            String sitemapPath = "src/main/resources/static/sitemap-home-repair.xml";
            int urlCount = sitemapGenerator.generateSitemap(sitemapPath);

            System.out.println("✓ Sitemap generated with " + urlCount + " URLs: " + sitemapPath);

            System.out.println();
            System.out.println("╔════════════════════════════════════════════════════════════╗");
            System.out.println("║                    ✓ SUCCESS!                              ║");
            System.out.println("║                                                            ║");
            System.out.println("║  Generated: " + String.format("%-4d", pageCount)
                    + " static pages                              ║");
            System.out.println(
                    "║  Sitemap:   " + String.format("%-4d", urlCount) + " URLs                                   ║");
            System.out.println("║                                                            ║");
            System.out.println("║  Next Steps:                                               ║");
            System.out.println("║  1. Review sample pages in static/verdicts/                ║");
            System.out.println("║  2. Deploy static/ folder to web server                    ║");
            System.out.println("║  3. Submit sitemap.xml to Google Search Console            ║");
            System.out.println("╚════════════════════════════════════════════════════════════╝");

        } catch (Exception e) {
            System.err.println();
            System.err.println("╔════════════════════════════════════════════════════════════╗");
            System.err.println("║                    ✗ ERROR!                                ║");
            System.err.println("╚════════════════════════════════════════════════════════════╝");
            System.err.println();
            System.err.println("Generation failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
