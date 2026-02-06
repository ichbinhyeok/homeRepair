package com.livingcostcheck.home_repair.seo;

import com.livingcostcheck.home_repair.service.VerdictEngineService;
import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs;
import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.*;
import com.livingcostcheck.home_repair.service.dto.verdict.StateHubPage;
import com.livingcostcheck.home_repair.service.dto.verdict.DataMapping;
import com.livingcostcheck.home_repair.util.TextUtil;
import gg.jte.TemplateEngine;
import gg.jte.output.StringOutput;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class StaticPageGeneratorService {

    private final VerdictEngineService verdictEngineService;
    private final InternalLinkBuilder internalLinkBuilder;
    private final TemplateEngine templateEngine;
    private final VerdictSeoService verdictSeoService;

    private static final List<String> ALL_ERAS = Arrays.asList(
            "PRE_1950", "1950_1970", "1970_1980", "1980_1995", "1995_2010", "2010_PRESENT");

    private static final double DEFAULT_BUDGET = -1.0;
    private static final String DEFAULT_PURPOSE = "LIVING";

    public List<String> generateAllPages(String outputBasePath) {
        log.info("Starting Parallel pSEO static page generation for {} cities...",
                verdictEngineService.getMetroMasterData().getData().size());
        Map<String, DataMapping.MetroCityData> allMetros = verdictEngineService.getMetroMasterData().getData();
        List<String> metroCodes = new ArrayList<>(allMetros.keySet());

        List<String> allGeneratedUrls = Collections.synchronizedList(new ArrayList<>());
        String currentMonthYear = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH));

        // Use parallelStream for 5-10x performance boost
        metroCodes.parallelStream().forEach(metroCode -> {
            for (String era : ALL_ERAS) {
                try {
                    List<String> pageUrls = generateSinglePage(metroCode, era, outputBasePath, currentMonthYear);
                    allGeneratedUrls.addAll(pageUrls);
                } catch (Exception e) {
                    log.error("Failed to generate: {}/{} - {}", metroCode, era, e.getMessage());
                }
            }
        });

        try {
            generateStateHubPages(metroCodes, outputBasePath);
            for (String state : getAllStates(metroCodes)) {
                allGeneratedUrls
                        .add("https://lifeverdict.com/home-repair/verdicts/states/" + state.toLowerCase() + ".html");
            }
        } catch (Exception e) {
            log.error("Post-generation State Hub failed: {}", e.getMessage());
        }

        return allGeneratedUrls;
    }

    private List<String> generateSinglePage(String metroCode, String era, String outputBasePath, String dateString)
            throws IOException {
        List<String> generatedUrls = new ArrayList<>();
        VerdictDTOs.UserContext context = VerdictDTOs.UserContext.builder().metroCode(metroCode).era(era)
                .budget(DEFAULT_BUDGET).purpose(DEFAULT_PURPOSE).build();
        VerdictDTOs.Verdict verdict = verdictEngineService.generateVerdict(context);

        String metroName = TextUtil.formatMetroName(metroCode);
        String eraName = TextUtil.formatEraName(era);
        VerdictSeoService.SeoVariant seoVariant = verdictSeoService.getStaticPageHeader(metroName, eraName);

        Map<String, Object> templateData = new HashMap<>();
        // Clever Strategy 4: Dynamic Freshness in Title
        templateData.put("title", seoVariant.title() + " (" + dateString + ")");
        templateData.put("h1Content", seoVariant.h1());
        templateData.put("metroCode", metroCode);
        templateData.put("metroName", metroName);
        templateData.put("era", era);
        templateData.put("eraName", eraName);
        templateData.put("verdict", verdict);
        templateData.put("baseUrl", "https://lifeverdict.com");
        templateData.put("canonicalUrl", buildCanonicalUrl(metroCode, era));
        templateData.put("dateString", dateString);

        templateData.put("faqSchema", generateFAQSchema(metroName, eraName, verdict));
        templateData.put("howToSchema", generateHowToSchema(metroName, eraName));
        templateData.put("breadcrumbSchema",
                generateBreadcrumbSchema(metroName, eraName, (String) templateData.get("canonicalUrl")));

        // Updated InternalLinkBuilder Calls
        templateData.put("stateLinks", internalLinkBuilder.getRelatedCitiesInState(metroCode, era,
                verdictEngineService.getMetroMasterData().getData().keySet()));
        templateData.put("eraLinks", internalLinkBuilder.getOtherErasInCity(metroCode, era));
        templateData.put("cityLinks", internalLinkBuilder.getNearbyMetrosInEra(metroCode, era,
                verdictEngineService.getMetroMasterData().getData()));

        String stateCode = extractStateCode(metroCode);
        if (stateCode != null) {
            templateData.put("stateHubUrl", "/home-repair/verdicts/states/" + stateCode.toLowerCase() + ".html");
            templateData.put("stateName", stateCode);
        }

        long seed = (metroCode + era).hashCode();
        templateData.put("climateFragment", FragmentLibrary.selectClimateFragment(null, seed));
        templateData.put("eraFragment", FragmentLibrary.selectEraFragment(era, seed + 1));
        templateData.put("costFragment", FragmentLibrary.selectCostFragment(1.0, seed + 2));

        DataMapping.MetroCityData mData = verdictEngineService.getMetroMasterData().getData().get(metroCode);
        if (mData != null) {
            templateData.put("metroRisk", mData.getRisk());
            templateData.put("climateZone", mData.getClimateZone());
            templateData.put("foundation", mData.getFoundation());
            templateData.put("avgHouseAge", "N/A");

            // Clever Strategy 2: Comparison Hook
            String comparisonInsight = generateComparisonInsight(mData, metroName, seed);
            templateData.put("regionalInsight", comparisonInsight + " " + FragmentLibrary
                    .generateRegionalInsight(mData.getClimateZone(), era, mData.getLaborMult(), metroName, seed + 3));
        }

        templateData.put("faqItems", new ArrayList<>());
        templateData.put("lowPrice", String.format("%,.0f", verdict.getPlan().getMustDo().stream()
                .mapToDouble(RiskAdjustedItem::getAdjustedCost).min().orElse(0.0)));
        templateData.put("highPrice", String.format("%,.0f",
                verdict.getPlan().getMustDo().stream().mapToDouble(RiskAdjustedItem::getAdjustedCost).sum()));

        StringOutput output = new StringOutput();
        templateEngine.render("seo/static-verdict.jte", templateData, output);
        Path filePath = buildFilePath(outputBasePath, metroCode, era);
        Files.createDirectories(filePath.getParent());
        Files.writeString(filePath, minifyHtml(output.toString()));

        generatedUrls.add((String) templateData.get("canonicalUrl"));

        // Clever Strategy 1: Expand L2 to ALL Metros (Removed TIER_1 check)
        Set<String> processedCategories = new HashSet<>();
        for (VerdictDTOs.RiskAdjustedItem item : verdict.getPlan().getMustDo()) {
            String code = item.getItemCode();
            String category = null;
            if (code.contains("ROOF"))
                category = "ROOFING";
            else if (code.contains("PLUMB"))
                category = "PLUMBING";
            else if (code.contains("HVAC"))
                category = "HVAC";
            else if (code.contains("ELECTR"))
                category = "ELECTRICAL";
            else if (code.contains("FOUNDATION"))
                category = "FOUNDATION";

            if (category != null && !processedCategories.contains(category)) {
                String l2Url = generateRiskPage(metroCode, era, item, templateData, outputBasePath, dateString);
                generatedUrls.add(l2Url);
                processedCategories.add(category);
            }
        }

        return generatedUrls;
    }

    private String generateComparisonInsight(DataMapping.MetroCityData mData, String metroName, long seed) {
        double laborMult = mData.getLaborMult();
        String context = "";
        if (laborMult > 1.2) {
            context = String.format(
                    "Labor costs in %s are significantly higher than the national average, making DIY solutions particularly valuable here.",
                    metroName);
        } else if (laborMult < 0.9) {
            context = String.format(
                    "%s remains one of the more affordable markets for professional renovations compared to neighboring regions.",
                    metroName);
        } else {
            context = String.format(
                    "Renovation costs in %s align closely with national benchmarks, providing a stable market for standard living upgrades.",
                    metroName);
        }
        return context;
    }

    private String generateRiskPage(String metroCode, String era, VerdictDTOs.RiskAdjustedItem item,
            Map<String, Object> parentData, String basePath, String dateString) throws IOException {
        Map<String, Object> data = new HashMap<>(parentData);
        String metroName = (String) parentData.get("metroName");
        String eraName = (String) parentData.get("eraName");

        String title = String.format("%s in %s: %s Estimated Repair Costs (%s)", item.getPrettyName(), metroName,
                eraName, dateString);
        data.put("title", title);
        data.put("item", item);
        data.put("parentUrl", parentData.get("canonicalUrl"));

        // Clever Strategy 3: Mesh Linking (L2 Cross-Linking)
        data.put("otherRisks", internalLinkBuilder.getOtherRisksInSameHome(metroCode, era, item.getItemCode()));

        String slug = item.getItemCode().toLowerCase().replace("_", "-");
        String canonical = ((String) parentData.get("canonicalUrl")).replace(".html", "") + "/" + slug + ".html";
        data.put("canonicalUrl", canonical);
        data.put("faqSchema", String.format(
                "<script type=\"application/ld+json\">{\"@context\":\"https://schema.org\",\"@type\":\"FAQPage\",\"mainEntity\":[{\"@type\":\"Question\",\"name\":\"Repair cost for %s?\",\"acceptedAnswer\":{\"@type\":\"Answer\",\"text\":\"Estimated at $%s in %s based on %s market data.\"}}]}</script>",
                item.getPrettyName(), item.getAdjustedCost(), metroName, dateString));
        data.put("breadcrumbSchema",
                "<script type=\"application/ld+json\">{\"@context\":\"https://schema.org\",\"@type\":\"BreadcrumbList\"}</script>");

        StringOutput output = new StringOutput();
        templateEngine.render("seo/static-risk-detail.jte", data, output);
        Path path = buildFilePath(basePath, metroCode, era, item.getItemCode());
        Files.createDirectories(path.getParent());
        Files.writeString(path, minifyHtml(output.toString()));
        return canonical;
    }

    private String generateFAQSchema(String m, String e, VerdictDTOs.Verdict v) {
        String items = v.getPlan().getMustDo().stream().map(VerdictDTOs.RiskAdjustedItem::getPrettyName).limit(2)
                .collect(java.util.stream.Collectors.joining(", "));
        double total = v.getPlan().getMustDo().stream().mapToDouble(VerdictDTOs.RiskAdjustedItem::getAdjustedCost)
                .sum();

        return String.format(
                "<script type=\"application/ld+json\">{" +
                        "\"@context\":\"https://schema.org\"," +
                        "\"@type\":\"FAQPage\"," +
                        "\"mainEntity\":[{" +
                        "\"@type\":\"Question\"," +
                        "\"name\":\"How much are typically repair costs for a %s home in %s?\"," +
                        "\"acceptedAnswer\":{\"@type\":\"Answer\",\"text\":\"Estimated liabilities are approximately $%,.0f, with primary drivers being %s.\"}"
                        +
                        "}]}</script>",
                e, m, total, items);
    }

    private String generateHowToSchema(String m, String e) {
        return String.format(
                "<script type=\"application/ld+json\">{" +
                        "\"@context\":\"https://schema.org\"," +
                        "\"@type\":\"HowTo\"," +
                        "\"name\":\"Evaluating %s home repair costs in %s\"," +
                        "\"step\":[" +
                        "{\"@type\":\"HowToStep\",\"text\":\"Identify era-specific risks for %s builds.\"}," +
                        "{\"@type\":\"HowToStep\",\"text\":\"Apply %s regional labor multipliers.\"}," +
                        "{\"@type\":\"HowToStep\",\"text\":\"Calculate total estimated liability before closing.\"}" +
                        "]}</script>",
                e, m, e, m);
    }

    private String generateBreadcrumbSchema(String m, String e, String u) {
        return String.format(
                "<script type=\"application/ld+json\">{" +
                        "\"@context\":\"https://schema.org\"," +
                        "\"@type\":\"BreadcrumbList\"," +
                        "\"itemListElement\":[" +
                        "{\"@type\":\"ListItem\",\"position\":1,\"name\":\"Home\",\"item\":\"https://lifeverdict.com/\"},"
                        +
                        "{\"@type\":\"ListItem\",\"position\":2,\"name\":\"%s\",\"item\":\"%s\"}" +
                        "]}</script>",
                m, u);
    }

    private String minifyHtml(String html) {
        if (html == null)
            return "";
        return html
                .replaceAll("(?s)<!--.*?-->", "") // Remove HTML comments
                .replaceAll(">\\s+<", "><") // Remove whitespace between tags
                .replaceAll("\\s{2,}", " ") // Collapse multiple spaces
                .trim();
    }

    private String extractStateCode(String m) {
        String[] p = m.split("_");
        return (p.length > 0 && p[p.length - 1].length() == 2) ? p[p.length - 1] : null;
    }

    private Path buildFilePath(String b, String m, String e) {
        return Paths.get(b, m.toLowerCase().replace("_", "-"), e.toLowerCase().replace("_", "-") + ".html");
    }

    private Path buildFilePath(String b, String m, String e, String i) {
        return Paths.get(b, m.toLowerCase().replace("_", "-"), e.toLowerCase().replace("_", "-"),
                i.toLowerCase().replace("_", "-") + ".html");
    }

    private String buildCanonicalUrl(String m, String e) {
        return "https://lifeverdict.com/home-repair/verdicts/" + m.toLowerCase().replace("_", "-") + "/"
                + e.toLowerCase().replace("_", "-") + ".html";
    }

    private void generateStateHubPages(List<String> codes, String outputBasePath) throws IOException {
        Map<String, List<String>> byState = new HashMap<>();
        for (String c : codes) {
            String s = extractStateCode(c);
            if (s != null)
                byState.computeIfAbsent(s, k -> new ArrayList<>()).add(c);
        }
        for (var entry : byState.entrySet()) {
            List<StateHubPage.CityData> cities = new ArrayList<>();
            for (String cityCode : entry.getValue()) {
                List<InternalLinkBuilder.InternalLink> links = new ArrayList<>();
                for (String era : ALL_ERAS)
                    links.add(new InternalLinkBuilder.InternalLink(TextUtil.formatEraText(era),
                            buildCanonicalUrl(cityCode, era).replace("https://lifeverdict.com", "")));
                cities.add(new StateHubPage.CityData(TextUtil.formatMetroName(cityCode), links));
            }
            StateHubPage page = new StateHubPage(entry.getKey(), entry.getKey(),
                    "https://lifeverdict.com/home-repair/verdicts/states/" + entry.getKey().toLowerCase() + ".html",
                    "{}", cities);
            StringOutput output = new StringOutput();
            templateEngine.render("seo/static-state-hub.jte", Collections.singletonMap("page", page), output);
            Path path = Paths.get(outputBasePath.replace("verdicts", "verdicts/states"),
                    entry.getKey().toLowerCase() + ".html");
            Files.createDirectories(path.getParent());
            Files.writeString(path, output.toString());
        }
    }

    private Set<String> getAllStates(List<String> codes) {
        Set<String> states = new HashSet<>();
        for (String m : codes) {
            String s = extractStateCode(m);
            if (s != null)
                states.add(s);
        }
        return states;
    }
}
