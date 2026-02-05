package com.livingcostcheck.home_repair.seo;

import com.livingcostcheck.home_repair.service.VerdictEngineService;
import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.*;
import gg.jte.TemplateEngine;
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
    private final VerdictSeoService verdictSeoService;
    private final SitemapGenerator sitemapGenerator;

    // All eras to generate
    private static final List<String> ALL_ERAS = Arrays.asList(
            "PRE_1950",
            "1950_1970",
            "1970_1980",
            "1980_1995",
            "1995_2010",
            "2010_PRESENT");

    // Default budget for static pages (-1.0 triggers Benchmark Mode)
    private static final double DEFAULT_BUDGET = -1.0;
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
        List<String> allGeneratedUrls = new ArrayList<>();

        for (String metroCode : metroCodes) {
            for (String era : ALL_ERAS) {
                try {
                    List<String> pageUrls = generateSinglePage(metroCode, era, outputBasePath);
                    allGeneratedUrls.addAll(pageUrls);
                    successCount += pageUrls.size();

                    if (successCount % 100 == 0) {
                        log.info("Progress: {} total URLs generated...", successCount);
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

        // --- NEW: Generate Sitemap automatically ---
        try {
            String sitemapPath = outputBasePath.replace("home-repair/verdicts", "sitemap.xml");
            int sitemapUrls = sitemapGenerator.generateSitemap(sitemapPath, allGeneratedUrls);
            log.info("Sitemap updated with {} URLs at {}", sitemapUrls, sitemapPath);
        } catch (IOException e) {
            log.error("Failed to generate sitemap after pSEO generation: {}", e.getMessage());
        }

        if (!failedPages.isEmpty()) {
            log.warn("Failed pages: {}", failedPages);
        }

        // --- NEW: Generate State Hub Pages ---
        try {
            int statePages = generateStateHubPages(metroCodes, outputBasePath);
            log.info("Generated {} State Hub pages.", statePages);

            // Add state pages to sitemap URLs
            for (String state : getAllStates(metroCodes)) {
                allGeneratedUrls
                        .add("https://lifeverdict.com/home-repair/verdicts/states/" + state.toLowerCase() + ".html");
            }

            // Regenerate sitemap with state pages
            try {
                String sitemapPath = outputBasePath.replace("home-repair/verdicts", "sitemap.xml");
                // Seed Strategy: Level 1 + State Hubs
                int sitemapUrls = sitemapGenerator.generateSitemap(sitemapPath, allGeneratedUrls);
                log.info("Sitemap updated AGAIN with State Hubs: {} URLs", sitemapUrls);
            } catch (IOException e) {
                log.error("Failed to update sitemap with State Hubs: {}", e.getMessage());
            }

        } catch (Exception e) {
            log.error("Failed to generate State Hub Pages", e);
        }

        return successCount;
    }

    /**
     * Generate a single static page and its child risk pages
     * 
     * @return List of all generated URLs (absolute)
     */
    private List<String> generateSinglePage(String metroCode, String era, String outputBasePath) throws IOException {
        List<String> generatedUrls = new ArrayList<>();
        // Create UserContext with default values
        UserContext context = UserContext.builder()
                .metroCode(metroCode)
                .era(era)
                .budget(DEFAULT_BUDGET)
                .purpose(DEFAULT_PURPOSE)
                .condition(null)
                .build();

        // Generate verdict
        Verdict verdict = verdictEngineService.generateVerdict(context);

        // Get internal links
        List<InternalLinkBuilder.InternalLink> eraLinks = internalLinkBuilder.getOtherErasInCity(metroCode, era);
        List<InternalLinkBuilder.InternalLink> cityLinks = internalLinkBuilder.getNearbyMetrosInEra(metroCode, era);

        // CTR Optimization: Verdict-First Titles & Decision-Oriented H1s
        String metroName = formatMetroName(metroCode);
        String eraName = formatEraName(era);

        // Uses VerdictSeoService to ensure "Market Benchmark" branding (Informational)
        VerdictSeoService.SeoVariant seoVariant = verdictSeoService.getStaticPageHeader(metroName, eraName);

        // Prepare template data
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("title", seoVariant.title());
        templateData.put("h1Content", seoVariant.h1());
        templateData.put("metroCode", metroCode);
        templateData.put("metroName", metroName);
        templateData.put("era", era);
        templateData.put("eraName", eraName);
        templateData.put("verdict", verdict);
        templateData.put("eraLinks", eraLinks);
        templateData.put("cityLinks", cityLinks);
        templateData.put("baseUrl", "https://lifeverdict.com");
        templateData.put("canonicalUrl", buildCanonicalUrl(metroCode, era));
        templateData.put("faqSchema", generateFAQSchema(metroName, eraName, verdict));
        templateData.put("breadcrumbSchema",
                generateBreadcrumbSchema(metroName, eraName, buildCanonicalUrl(metroCode, era)));
        templateData.put("stateLinks", internalLinkBuilder.getRelatedCitiesInState(metroCode, era));

        // Add FragmentLibrary content for uniqueness
        long seed = (metroCode + era).hashCode();
        templateData.put("climateFragment", FragmentLibrary.selectClimateFragment(null, seed));
        templateData.put("eraFragment", FragmentLibrary.selectEraFragment(era, seed + 1));
        templateData.put("costFragment", FragmentLibrary.selectCostFragment(1.0, seed + 2));

        // Phase 2: Add Regional Insight (Combinatorial Logic)
        String climateZone = verdictEngineService.getMetroMasterData().getData().get(metroCode).getClimateZone();
        double laborMult = verdictEngineService.getMetroMasterData().getData().get(metroCode).getLaborMult();
        String regionalInsight = FragmentLibrary.generateRegionalInsight(climateZone, era, laborMult, metroName, seed);
        templateData.put("regionalInsight", regionalInsight);

        // Calculate price range for schema
        double lowPrice = verdict.getPlan().getMustDo().stream()
                .mapToDouble(item -> item.getAdjustedCost())
                .min()
                .orElse(0.0);
        double highPrice = verdict.getPlan().getMustDo().stream()
                .mapToDouble(item -> item.getAdjustedCost())
                .sum();

        // Generate FAQ items for HTML display
        templateData.put("faqItems", generateFAQItems(formatMetroName(metroCode), formatEraName(era), verdict));
        templateData.put("lowPrice", String.format("%,.0f", lowPrice));
        templateData.put("highPrice", String.format("%,.0f", highPrice));

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

        log.debug("Generated Level 1: {}", filePath);
        generatedUrls.add(buildCanonicalUrl(metroCode, era));

        // ----------------------------------------------------------------
        // LEVEL 2 GENERATION: SPECIFIC RISK PAGES
        // ----------------------------------------------------------------
        if (verdict.getPlan() != null && verdict.getPlan().getMustDo() != null) {
            for (RiskAdjustedItem item : verdict.getPlan().getMustDo()) {
                String riskUrl = generateRiskPage(metroCode, era, item, verdict, regionalInsight, outputBasePath);
                generatedUrls.add(riskUrl);
            }
        }

        return generatedUrls;
    }

    /**
     * Generate a Level 2 Risk Detail Page
     * 
     * @return Absolute URL of the generated page
     */
    private String generateRiskPage(String metroCode, String era, RiskAdjustedItem item, Verdict verdict,
            String regionalInsight, String outputBasePath) throws IOException {
        String metroName = formatMetroName(metroCode);
        String eraName = formatEraName(era);
        String itemSlug = item.getItemCode().toLowerCase().replace("_", "-");

        // Prepare Template Data
        Map<String, Object> templateData = new HashMap<>();
        String title = String.format("%s Cost in %s (%s Guide)", item.getPrettyName(), metroName, "2026");
        String h1 = String.format("%s Replacement Cost", item.getPrettyName());

        templateData.put("title", title);
        templateData.put("h1Content", h1);
        templateData.put("metroCode", metroCode);
        templateData.put("metroName", metroName);
        templateData.put("era", era);
        templateData.put("eraName", eraName);
        templateData.put("item", item);
        templateData.put("verdict", verdict);
        templateData.put("regionalInsight", regionalInsight);

        // internal linking
        String parentUrl = "/home-repair/verdicts/" + metroCode.toLowerCase().replace("_", "-") + "/"
                + era.toLowerCase().replace("_", "-") + ".html";
        templateData.put("parentUrl", parentUrl);
        templateData.put("baseUrl", "https://lifeverdict.com");
        templateData.put("faqSchema", generateRiskFAQSchema(item, metroName));
        String riskCanonical = "https://lifeverdict.com" + parentUrl.replace(".html", "/") + itemSlug + ".html";
        templateData.put("canonicalUrl", riskCanonical);
        templateData.put("breadcrumbSchema",
                generateRiskBreadcrumbSchema(metroName, eraName, parentUrl, item.getPrettyName(), riskCanonical));

        // Render Level 2 Template
        StringOutput output = new StringOutput();
        templateEngine.render("seo/static-risk-detail.jte", templateData, output);
        String html = minifyHtml(output.toString());

        // Write File: /verdicts/{city}/{era}/{item-slug}.html
        Path directory = buildFilePath(outputBasePath, metroCode, era).getParent();
        Path eraDir = directory.resolve(era.toLowerCase().replace("_", "-"));
        Files.createDirectories(eraDir);
        Path filePath = eraDir.resolve(itemSlug + ".html");

        Files.writeString(filePath, html);
        log.debug("Generated Level 2: {}", filePath);
        return riskCanonical;
    }

    private String generateRiskFAQSchema(RiskAdjustedItem item, String metroName) {
        return String.format(
                "<script type=\"application/ld+json\">{\"@context\":\"https://schema.org\",\"@type\":\"FAQPage\",\"mainEntity\":[{\"@type\":\"Question\",\"name\":\"How much does it cost to fix %s in %s?\",\"acceptedAnswer\":{\"@type\":\"Answer\",\"text\":\"The estimated cost is $%s depending on home size and local labor rates.\"}}]}</script>",
                escapeJson(item.getPrettyName()), escapeJson(metroName),
                String.format("%,.0f", item.getAdjustedCost()));
    }

    private Path buildFilePath(String basePath, String metroCode, String era) {
        String metroSlug = metroCode.toLowerCase().replace("_", "-");
        String eraSlug = era.toLowerCase().replace("_", "-");
        return Paths.get(basePath, metroSlug, eraSlug + ".html");
    }

    private String buildCanonicalUrl(String metroCode, String era) {
        String metroSlug = metroCode.toLowerCase().replace("_", "-");
        String eraSlug = era.toLowerCase().replace("_", "-");
        return "https://lifeverdict.com/home-repair/verdicts/" + metroSlug + "/" + eraSlug + ".html";
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

    /**
     * Generate FAQ items for HTML display (synced with FAQ Schema)
     */
    private List<Map<String, String>> generateFAQItems(String metroName, String eraName, Verdict verdict) {
        List<Map<String, String>> faqItems = new ArrayList<>();

        // Q1: Cost-related question
        if (verdict.getCostRangeLabel() != null) {
            faqItems.add(Map.of(
                    "question",
                    String.format("What is the average home repair cost for %s homes in %s?", eraName, metroName),
                    "answer", String.format("The typical repair cost range is %s. %s",
                            verdict.getCostRangeLabel(),
                            verdict.getHeadline() != null ? verdict.getHeadline() : "")));
        }

        // Q2: Top risk question
        if (verdict.getPlan() != null && verdict.getPlan().getMustDo() != null
                && !verdict.getPlan().getMustDo().isEmpty()) {
            String topRisk = verdict.getPlan().getMustDo().get(0).getPrettyName();
            faqItems.add(Map.of(
                    "question", String.format("What are the most critical repairs for %s homes?", eraName),
                    "answer",
                    String.format("%s is the highest priority item requiring immediate attention.", topRisk)));
        }

        // Q3: Deal Killer question (if applicable)
        if (verdict.isDealKiller() && verdict.getDealKillerMessage() != null) {
            faqItems.add(Map.of(
                    "question", String.format("Is it safe to buy a %s home in %s?", eraName, metroName),
                    "answer", verdict.getDealKillerMessage()));
        }

        return faqItems;
    }

    /**
     * Generate FAQ Schema (JSON-LD) for Rich Snippets
     * This dramatically improves CTR by showing FAQ in search results
     */
    private String generateFAQSchema(String metroName, String eraName, Verdict verdict) {
        List<Map<String, String>> faqItems = new ArrayList<>();

        // Q1: Cost-related question
        if (verdict.getCostRangeLabel() != null) {
            faqItems.add(Map.of(
                    "question",
                    String.format("What is the average home repair cost for %s homes in %s?", eraName, metroName),
                    "answer", String.format("The typical repair cost range is %s. %s",
                            verdict.getCostRangeLabel(),
                            verdict.getHeadline() != null ? verdict.getHeadline() : "")));
        }

        // Q2: Top risk question
        if (verdict.getPlan() != null && verdict.getPlan().getMustDo() != null
                && !verdict.getPlan().getMustDo().isEmpty()) {
            String topRisk = verdict.getPlan().getMustDo().get(0).getPrettyName();
            faqItems.add(Map.of(
                    "question", String.format("What are the most critical repairs for %s homes?", eraName),
                    "answer",
                    String.format("%s is the highest priority item requiring immediate attention.", topRisk)));
        }

        // Q3: Deal Killer question (if applicable)
        if (verdict.isDealKiller() && verdict.getDealKillerMessage() != null) {
            faqItems.add(Map.of(
                    "question", String.format("Is it safe to buy a %s home in %s?", eraName, metroName),
                    "answer", verdict.getDealKillerMessage()));
        }

        // Build JSON-LD schema
        StringBuilder schema = new StringBuilder();
        schema.append("<script type=\"application/ld+json\">\n");
        schema.append("{\n");
        schema.append("  \"@context\": \"https://schema.org\",\n");
        schema.append("  \"@type\": \"FAQPage\",\n");
        schema.append("  \"mainEntity\": [\n");

        for (int i = 0; i < faqItems.size(); i++) {
            Map<String, String> item = faqItems.get(i);
            schema.append("    {\n");
            schema.append("      \"@type\": \"Question\",\n");
            schema.append(String.format("      \"name\": \"%s\",\n", escapeJson(item.get("question"))));
            schema.append("      \"acceptedAnswer\": {\n");
            schema.append("        \"@type\": \"Answer\",\n");
            schema.append(String.format("        \"text\": \"%s\"\n", escapeJson(item.get("answer"))));
            schema.append("      }\n");
            schema.append("    }");
            if (i < faqItems.size() - 1) {
                schema.append(",");
            }
            schema.append("\n");
        }

        schema.append("  ]\n");
        schema.append("}\n");
        schema.append("</script>");

        return schema.toString();
    }

    // Generate Breadcrumb Schema for Level 1
    private String generateBreadcrumbSchema(String metroName, String eraName, String currentUrl) {
        return "<script type=\"application/ld+json\">\n" +
                "{\n" +
                "  \"@context\": \"https://schema.org\",\n" +
                "  \"@type\": \"BreadcrumbList\",\n" +
                "  \"itemListElement\": [{\n" +
                "    \"@type\": \"ListItem\",\n" +
                "    \"position\": 1,\n" +
                "    \"name\": \"Home\",\n" +
                "    \"item\": \"https://lifeverdict.com\"\n" +
                "  },{\n" +
                "    \"@type\": \"ListItem\",\n" +
                "    \"position\": 2,\n" +
                "    \"name\": \"Home Repair\",\n" +
                "    \"item\": \"https://lifeverdict.com/home-repair\"\n" +
                "  },{\n" +
                "    \"@type\": \"ListItem\",\n" +
                "    \"position\": 3,\n" +
                "    \"name\": \"" + metroName + " (" + eraName + ")\",\n" +
                "    \"item\": \"" + currentUrl + "\"\n" +
                "  }]\n" +
                "}\n" +
                "</script>";
    }

    // ----------------------------------------------------------------
    // STATE HUB GENERATION
    // ----------------------------------------------------------------

    private int generateStateHubPages(List<String> metroCodes, String outputBasePath) throws IOException {
        // Group by State
        Map<String, List<String>> metrosByState = new HashMap<>();
        for (String metro : metroCodes) {
            String state = extractStateCode(metro);
            if (state != null) {
                metrosByState.computeIfAbsent(state, k -> new ArrayList<>()).add(metro);
            }
        }

        int count = 0;
        for (Map.Entry<String, List<String>> entry : metrosByState.entrySet()) {
            String stateCode = entry.getKey();
            List<String> cities = entry.getValue();

            generateSingleStatePage(stateCode, cities, outputBasePath);
            count++;
        }
        return count;
    }

    private void generateSingleStatePage(String stateCode, List<String> cityCodes, String outputBasePath)
            throws IOException {
        String stateName = getStateName(stateCode);
        String fileName = stateCode.toLowerCase() + ".html";
        String filePath = outputBasePath.replace("verdicts", "verdicts/states/") + fileName;

        // Prepare City Data
        List<Map<String, Object>> cityList = new ArrayList<>();
        for (String cityCode : cityCodes) {
            Map<String, Object> cityData = new HashMap<>();
            cityData.put("name", formatMetroName(cityCode));

            List<InternalLinkBuilder.InternalLink> eraLinks = new ArrayList<>();
            for (String era : ALL_ERAS) {
                eraLinks.add(new InternalLinkBuilder.InternalLink(
                        formatEraText(era),
                        buildCanonicalUrl(cityCode, era).replace("https://lifeverdict.com", "")));
            }
            cityData.put("eras", eraLinks);
            cityList.add(cityData);
        }

        // Sort by City Name
        cityList.sort((a, b) -> ((String) a.get("name")).compareTo((String) b.get("name")));

        String canonicalUrl = "https://lifeverdict.com/home-repair/verdicts/states/" + fileName;

        Map<String, Object> templateData = new HashMap<>();
        templateData.put("stateCode", stateCode);
        templateData.put("stateName", stateName);
        templateData.put("canonicalUrl", canonicalUrl);
        templateData.put("cities", cityList);
        templateData.put("breadcrumbSchema", generateStateBreadcrumbSchema(stateName, canonicalUrl));

        StringOutput output = new StringOutput();
        templateEngine.render("seo/static-state-hub.jte", templateData, output);

        Path path = Paths.get(filePath);
        Files.createDirectories(path.getParent());
        Files.writeString(path, output.toString());
    }

    private String extractStateCode(String metroCode) {
        String[] parts = metroCode.split("_");
        if (parts.length > 0) {
            String last = parts[parts.length - 1];
            if (last.length() == 2)
                return last;
        }
        return null; // Fallback
    }

    // Formatting Helper
    private String formatEraText(String era) {
        switch (era) {
            case "PRE_1950":
                return "Pre-1950";
            case "1950_1970":
                return "1950s-1970s";
            case "1970_1980":
                return "1970s";
            case "1980_1995":
                return "1980s-1990s";
            case "1995_2010":
                return "1995-2010";
            case "2010_PRESENT":
                return "2010-Present";
            default:
                return era;
        }
    }

    private String getStateName(String code) {
        // Simple mapping for demo, can be expanded
        switch (code) {
            case "AL":
                return "Alabama";
            case "AZ":
                return "Arizona";
            case "CA":
                return "California";
            case "CO":
                return "Colorado";
            case "CT":
                return "Connecticut";
            case "DC":
                return "District of Columbia";
            case "FL":
                return "Florida";
            case "GA":
                return "Georgia";
            case "IL":
                return "Illinois";
            case "IN":
                return "Indiana";
            case "MA":
                return "Massachusetts";
            case "MD":
                return "Maryland";
            case "MI":
                return "Michigan";
            case "MN":
                return "Minnesota";
            case "MO":
                return "Missouri";
            case "NC":
                return "North Carolina";
            case "NJ":
                return "New Jersey";
            case "NV":
                return "Nevada";
            case "NY":
                return "New York";
            case "OH":
                return "Ohio";
            case "OR":
                return "Oregon";
            case "PA":
                return "Pennsylvania";
            case "RI":
                return "Rhode Island";
            case "TN":
                return "Tennessee";
            case "TX":
                return "Texas";
            case "UT":
                return "Utah";
            case "VA":
                return "Virginia";
            case "WA":
                return "Washington";
            case "WI":
                return "Wisconsin";
            default:
                return code;
        }
    }

    // Breadcrumb for State Page
    private String generateStateBreadcrumbSchema(String stateName, String currentUrl) {
        return "<script type=\"application/ld+json\">\n" +
                "{\n" +
                "  \"@context\": \"https://schema.org\",\n" +
                "  \"@type\": \"BreadcrumbList\",\n" +
                "  \"itemListElement\": [{\n" +
                "    \"@type\": \"ListItem\",\n" +
                "    \"position\": 1,\n" +
                "    \"name\": \"Home\",\n" +
                "    \"item\": \"https://lifeverdict.com\"\n" +
                "  },{\n" +
                "    \"@type\": \"ListItem\",\n" +
                "    \"position\": 2,\n" +
                "    \"name\": \"Home Repair\",\n" +
                "    \"item\": \"https://lifeverdict.com/home-repair\"\n" +
                "  },{\n" +
                "    \"@type\": \"ListItem\",\n" +
                "    \"position\": 3,\n" +
                "    \"name\": \"" + stateName + "\",\n" +
                "    \"item\": \"" + currentUrl + "\"\n" +
                "  }]\n" +
                "}\n" +
                "</script>";
    }

    private Set<String> getAllStates(List<String> metroCodes) {
        Set<String> states = new HashSet<>();
        for (String m : metroCodes) {
            String s = extractStateCode(m);
            if (s != null)
                states.add(s);
        }
        return states;
    }

    // Generate Breadcrumb Schema for Level 2
    private String generateRiskBreadcrumbSchema(String metroName, String eraName, String parentPath, String itemName,
            String currentUrl) {
        return "<script type=\"application/ld+json\">\n" +
                "{\n" +
                "  \"@context\": \"https://schema.org\",\n" +
                "  \"@type\": \"BreadcrumbList\",\n" +
                "  \"itemListElement\": [{\n" +
                "    \"@type\": \"ListItem\",\n" +
                "    \"position\": 1,\n" +
                "    \"name\": \"Home\",\n" +
                "    \"item\": \"https://lifeverdict.com\"\n" +
                "  },{\n" +
                "    \"@type\": \"ListItem\",\n" +
                "    \"position\": 2,\n" +
                "    \"name\": \"Home Repair\",\n" +
                "    \"item\": \"https://lifeverdict.com/home-repair\"\n" +
                "  },{\n" +
                "    \"@type\": \"ListItem\",\n" +
                "    \"position\": 3,\n" +
                "    \"name\": \"" + metroName + " (" + eraName + ")\",\n" +
                "    \"item\": \"https://lifeverdict.com" + parentPath + "\"\n" +
                "  },{\n" +
                "    \"@type\": \"ListItem\",\n" +
                "    \"position\": 4,\n" +
                "    \"name\": \"" + itemName + "\",\n" +
                "    \"item\": \"" + currentUrl + "\"\n" +
                "  }]\n" +
                "}\n" +
                "</script>";
    }

    private String escapeJson(String text) {
        if (text == null)
            return "";
        return text.replace("\"", "\\\"").replace("\n", " ").replace("\r", "");
    }
}
