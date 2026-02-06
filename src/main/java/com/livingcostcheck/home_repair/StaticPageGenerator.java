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
            // Check if running from source (dev/build) or JAR (classloader)
            Path templateDir = Paths.get("src/main/jte");
            gg.jte.CodeResolver codeResolver;
            if (java.nio.file.Files.exists(templateDir)) {
                codeResolver = new DirectoryCodeResolver(templateDir);
                System.out.println("✓ Using file system templates: " + templateDir.toAbsolutePath());
            } else {
                codeResolver = new gg.jte.resolve.ResourceCodeResolver("");
                System.out.println("✓ Using classpath templates (JAR mode)");
            }
            TemplateEngine templateEngine = TemplateEngine.create(codeResolver, ContentType.Html);
            System.out.println("✓ Template engine initialized");

            // Initialize pSEO services
            InternalLinkBuilder linkBuilder = new InternalLinkBuilder();
            com.livingcostcheck.home_repair.seo.VerdictSeoService verdictSeoService = new com.livingcostcheck.home_repair.seo.VerdictSeoService();
            SitemapGenerator sitemapGenerator = new SitemapGenerator();
            StaticPageGeneratorService pageGenerator = new StaticPageGeneratorService(
                    verdictService, linkBuilder, templateEngine, verdictSeoService);

            System.out.println();
            System.out.println("────────────────────────────────────────────────────────────");
            System.out.println("Phase 1: Generating 702 Static Verdict Pages");
            System.out.println("────────────────────────────────────────────────────────────");
            System.out.println();

            // Generate all pages
            String outputPath = "src/main/resources/static/home-repair/verdicts";
            java.util.List<String> allUrls = pageGenerator.generateAllPages(outputPath);
            int pageCount = allUrls.size();

            System.out.println();
            System.out.println("✓ Generated " + pageCount + " pages to: " + outputPath);

            System.out.println();
            System.out.println("────────────────────────────────────────────────────────────");
            System.out.println("Phase 2: Generating Sitemap");
            System.out.println("────────────────────────────────────────────────────────────");
            System.out.println();

            // Generate sitemap
            String sitemapPath = "src/main/resources/static/sitemap.xml";
            int urlCount = sitemapGenerator.generateSitemap(sitemapPath, allUrls);

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
