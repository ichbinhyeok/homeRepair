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
            StaticPageGeneratorService pageGenerator = new StaticPageGeneratorService(
                    verdictService, linkBuilder, templateEngine, verdictSeoService);
            SitemapGenerator sitemapGenerator = new SitemapGenerator(verdictService);

            System.out.println();
            System.out.println("────────────────────────────────────────────────────────────");
            System.out.println("Phase 1: Generating 702 Static Verdict Pages");
            System.out.println("────────────────────────────────────────────────────────────");
            System.out.println();

            // Generate all pages
            String outputPath = "src/main/resources/static/home-repair/verdicts";
            int pageCount = pageGenerator.generateAllPages(outputPath);

            System.out.println();
            System.out.println("✓ Generated " + pageCount + " pages to: " + outputPath);

            System.out.println();
            System.out.println("────────────────────────────────────────────────────────────");
            System.out.println("Phase 2: Generating Sitemap");
            System.out.println("────────────────────────────────────────────────────────────");
            System.out.println();

            // Generate sitemap
            String sitemapPath = "src/main/resources/static/sitemap.xml";
            int urlCount = sitemapGenerator.generateSitemap(sitemapPath);

            System.out.println("✓ Sitemap generated with " + urlCount + " URLs: " + sitemapPath);

            System.out.println();
            System.out.println("────────────────────────────────────────────────────────────");
            System.out.println("Phase 3: Generating State Hubs & Landing Pages");
            System.out.println("────────────────────────────────────────────────────────────");
            System.out.println();
            
            // Generate State Hubs
            com.livingcostcheck.home_repair.seo.StateHubGeneratorService stateHubGenerator = new com.livingcostcheck.home_repair.seo.StateHubGeneratorService(verdictService, templateEngine);
            String stateHubOutputPath = "src/main/resources/static/home-repair";
            int stateHubsCount = stateHubGenerator.generateAllStateHubs(stateHubOutputPath);
            System.out.println("✓ Generated " + stateHubsCount + " State Hubs");

            // Generate Landing Page (Index) with updated links
            // We need to re-render index.jte with the new links structure
            // NOTE: Ideally we would move this logic to a service, but for now we put it here to ensure it's built
            // Update: We'll skip complex dynamic generation for index for now as it requires list of states,
            // but we'll assume index.jte is the source of truth and just needs to be rendered if not handled by Spring.
            // However, the user asked to modify index.jte to ADD links first.


            
            System.out.println();
            System.out.println("────────────────────────────────────────────────────────────");
            System.out.println("Phase 4: Generating Glossary Pages (Guides)");
            System.out.println("────────────────────────────────────────────────────────────");
            System.out.println();

            // Generate Glossary Pages
            com.livingcostcheck.home_repair.seo.GlossaryGeneratorService glossaryGenerator = new com.livingcostcheck.home_repair.seo.GlossaryGeneratorService(verdictService, templateEngine);
            String glossaryOutputPath = "src/main/resources/static/home-repair";
            int glossaryCount = glossaryGenerator.generateAllGlossaryPages(glossaryOutputPath);
            System.out.println("✓ Generated " + glossaryCount + " Glossary Pages");

            System.out.println();
            System.out.println("────────────────────────────────────────────────────────────");
            System.out.println("Phase 5: Generating Support Pages (Legal & Info)");
            System.out.println("────────────────────────────────────────────────────────────");
            System.out.println();
            
            
            // Support Pages List: Template Name -> Output Path (Relative to static root)
            // Group 1: No Parameters
            java.util.Map<String, String> simplePages = new java.util.HashMap<>();
            simplePages.put("pages/privacy-policy.jte", "src/main/resources/static/privacy-policy.html");
            simplePages.put("pages/terms-of-service.jte", "src/main/resources/static/terms-of-service.html");
            simplePages.put("pages/disclaimer.jte", "src/main/resources/static/disclaimer.html");

            // Group 2: Requires baseUrl
            java.util.Map<String, String> contentPages = new java.util.HashMap<>();
            contentPages.put("pages/about.jte", "src/main/resources/static/home-repair/about.html");
            contentPages.put("pages/methodology.jte", "src/main/resources/static/home-repair/methodology.html");
            contentPages.put("pages/editorial-policy.jte", "src/main/resources/static/home-repair/editorial-policy.html");

            int supportCount = 0;
            
            // Render Simple Pages
            for (java.util.Map.Entry<String, String> entry : simplePages.entrySet()) {
                try {
                    gg.jte.output.StringOutput output = new gg.jte.output.StringOutput();
                    templateEngine.render(entry.getKey(), null, output);
                    Path path = Paths.get(entry.getValue());
                    java.nio.file.Files.createDirectories(path.getParent());
                    java.nio.file.Files.writeString(path, output.toString());
                    System.out.println("✓ Generated: " + entry.getValue());
                    supportCount++;
                } catch (Exception e) {
                    System.err.println("✗ Failed to generate " + entry.getValue() + ": " + e.getMessage());
                }
            }

            // Render Content Pages (with baseUrl)
            java.util.Map<String, Object> contentParams = new java.util.HashMap<>();
            contentParams.put("baseUrl", "https://lifeverdict.com/home-repair"); // Prod URL base
            
            for (java.util.Map.Entry<String, String> entry : contentPages.entrySet()) {
                try {
                    gg.jte.output.StringOutput output = new gg.jte.output.StringOutput();
                    templateEngine.render(entry.getKey(), contentParams, output);
                    Path path = Paths.get(entry.getValue());
                    java.nio.file.Files.createDirectories(path.getParent());
                    java.nio.file.Files.writeString(path, output.toString());
                    System.out.println("✓ Generated: " + entry.getValue());
                    supportCount++;
                } catch (Exception e) {
                    System.err.println("✗ Failed to generate " + entry.getValue() + ": " + e.getMessage());
                }
            }



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
