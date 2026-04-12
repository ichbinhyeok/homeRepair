package com.livingcostcheck.home_repair.seo;

import com.livingcostcheck.home_repair.service.VerdictEngineService;
import com.livingcostcheck.home_repair.seo.InternalLinkBuilder.InternalLink;
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
        private static final Set<String> INDEXABLE_STATE_CODES = Set.of("TX", "FL");
        private static final Set<String> INDEXABLE_VERDICT_KEYS = Set.of(
                        verdictKey("PITTSBURGH_PA", "PRE_1950"),
                        verdictKey("TULSA_OK", "PRE_1950"),
                        verdictKey("LITTLE_ROCK_NORTH_LITTLE_ROCK_AR", "1950_1970"),
                        verdictKey("CHICAGO_NAPERVILLE_IL", "1950_1970"));

        private static final double DEFAULT_BUDGET = -1.0;
        private static final String DEFAULT_PURPOSE = "LIVING";

        public List<String> generateAllPages(String outputBasePath) {
                log.info("Starting Parallel pSEO static page generation for {} cities...",
                                verdictEngineService.getMetroMasterData().getData().size());
                Map<String, DataMapping.MetroCityData> allMetros = verdictEngineService.getMetroMasterData().getData();
                List<String> metroCodes = new ArrayList<>(allMetros.keySet());

                List<String> allGeneratedUrls = Collections.synchronizedList(new ArrayList<>());
                String currentMonthYear = LocalDate.now()
                                .format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH));

                // Use parallelStream for 5-10x performance boost
                metroCodes.parallelStream().forEach(metroCode -> {
                        for (String era : ALL_ERAS) {
                                try {
                                        List<String> pageUrls = generateSinglePage(metroCode, era, outputBasePath,
                                                        currentMonthYear);
                                        allGeneratedUrls.addAll(pageUrls);
                                } catch (Exception e) {
                                        log.error("Failed to generate: {}/{} - {}", metroCode, era, e.getMessage());
                                        allGeneratedUrls.add("ERROR: " + metroCode + "/" + era + ": " + e.getMessage());
                                }
                        }
                });

                try {
                        writeStateHubPages(metroCodes, outputBasePath);
                        for (String state : INDEXABLE_STATE_CODES) {
                                allGeneratedUrls
                                                .add("https://lifeverdict.com/home-repair/verdicts/states/"
                                                                + state.toLowerCase() + ".html");
                        }
                } catch (Exception e) {
                        log.error("Post-generation State Hub failed: {}", e.getMessage());
                }

                return allGeneratedUrls;
        }

        private List<String> generateSinglePage(String metroCode, String era, String outputBasePath, String dateString)
                        throws IOException {
                List<String> generatedUrls = new ArrayList<>();
                boolean indexablePage = isIndexableVerdictPage(metroCode, era);
                VerdictDTOs.UserContext context = VerdictDTOs.UserContext.builder().metroCode(metroCode).era(era)
                                .budget(DEFAULT_BUDGET).purpose(DEFAULT_PURPOSE).build();
                VerdictDTOs.Verdict verdict = verdictEngineService.generateVerdict(context);

                String metroName = TextUtil.formatMetroName(metroCode);
                String eraName = TextUtil.formatEraName(era);
                VerdictSeoService.SeoVariant seoVariant = verdictSeoService.getStaticPageHeader(metroName, eraName);

                Map<String, Object> templateData = new HashMap<>();
                templateData.put("title", seoVariant.title() + " (" + dateString + ")");
                templateData.put("h1Content", seoVariant.h1());
                templateData.put("metroCode", metroCode);
                templateData.put("metroName", metroName);
                templateData.put("era", era);
                templateData.put("eraName", eraName);
                templateData.put("verdict", verdict);
                templateData.put("baseUrl", "https://lifeverdict.com");
                templateData.put("canonicalUrl", buildCanonicalUrl(metroCode, era));
                templateData.put("calculatorUrl", buildCalculatorUrl(metroCode, era));
                templateData.put("dateString", dateString);
                templateData.put("robotsDirective", indexablePage ? "index,follow" : "noindex,follow");

                String stateCode = extractStateCode(metroCode);

                templateData.put("howToSchema", "");
                templateData.put("breadcrumbSchema",
                                generateBreadcrumbSchema(metroName, eraName, (String) templateData.get("canonicalUrl"),
                                                stateCode));

                // Removed Product Schema to prevent Google Manual Action Spam Flags

                // Explore Other Markets: Similar Labor Multiplier or broad state cities
                templateData.put("stateLinks", keepIndexableLinks(internalLinkBuilder.getRelatedCitiesInState(metroCode,
                                era, verdictEngineService.getMetroMasterData().getData().keySet())));
                templateData.put("eraLinks", internalLinkBuilder.getOtherErasInCity(metroCode, era));

                // Nearby Cities: Adjacent Metros (Different selection logic to avoid overlap)
                templateData.put("cityLinks", keepIndexableLinks(internalLinkBuilder.getNearbyMetrosInEra(metroCode, era,
                                verdictEngineService.getMetroMasterData().getData())));

                if (stateCode != null) {
                        templateData.put("stateHubUrl",
                                        "/home-repair/verdicts/states/" + stateCode.toLowerCase() + ".html");
                        templateData.put("stateName", stateCode);
                }

                long seed = (metroCode + era).hashCode();

                DataMapping.MetroCityData mData = verdictEngineService.getMetroMasterData().getData().get(metroCode);
                DataMapping.MetroUniqueSignal uniqueSignal = null;
                if (verdictEngineService.getMetroUniqueSignalsData() != null
                                && verdictEngineService.getMetroUniqueSignalsData().getData() != null) {
                        uniqueSignal = verdictEngineService.getMetroUniqueSignalsData().getData().get(metroCode);
                }
                String msaName = TextUtil.formatMsaName(metroCode);
                if (uniqueSignal != null && uniqueSignal.getMsaName() != null
                                && !uniqueSignal.getMsaName().isBlank()) {
                        msaName = uniqueSignal.getMsaName();
                }
                templateData.put("msaName", msaName);

                String actualClimateZone = (mData != null) ? mData.getClimateZone() : null;
                double actualLaborMult = (mData != null && mData.getLaborMult() != null) ? mData.getLaborMult() : 1.0;
                templateData.put("climateFragment", FragmentLibrary.selectClimateFragment(actualClimateZone, seed));
                templateData.put("eraFragment", FragmentLibrary.selectEraFragment(era, seed + 1));
                templateData.put("costFragment", FragmentLibrary.selectCostFragment(actualLaborMult, seed + 2));
                templateData.put("openDataCsvUrl", "/data/metro_unique_signals_2026.csv");

                if (mData != null) {
                        templateData.put("metroRisk", mData.getRisk());
                        templateData.put("climateZone", mData.getClimateZone());
                        templateData.put("foundation", mData.getFoundation());
                        templateData.put("avgHouseAge", "N/A");

                        // Clever Strategy 2: Comparison Hook
                        String comparisonInsight = generateComparisonInsight(mData, metroName, seed);
                        templateData.put("regionalInsight", comparisonInsight + " " + FragmentLibrary
                                        .generateRegionalInsight(mData.getClimateZone(), era, mData.getLaborMult(),
                                                        metroName, seed + 3));
                }

                if (uniqueSignal != null) {
                        templateData.put("femaDisasterCount", uniqueSignal.getFemaMajorDisaster10y());
                        templateData.put("ownerOccupancyRate", uniqueSignal.getOwnerOccupancyRatePct());
                        templateData.put("medianYearBuilt", uniqueSignal.getMedianYearBuilt());
                        templateData.put("repairPressureIndex", uniqueSignal.getRepairPressureIndex());
                        if (templateData.get("regionalInsight") != null) {
                                String regionalInsight = (String) templateData.get("regionalInsight");
                                templateData.put("regionalInsight",
                                                regionalInsight + " Public records show "
                                                                + uniqueSignal.getFemaMajorDisaster10y()
                                                                + " major FEMA disaster declarations since 2016 in "
                                                                + uniqueSignal.getStateCode()
                                                                + ", with a statewide median home build year of "
                                                                + uniqueSignal.getMedianYearBuilt() + ".");
                        }
                }

                // Keep FAQs focused on the three highest-intent buyer questions.
                List<Map<String, String>> dynamicFaqs = generateDynamicFAQ(metroName, eraName, verdict, mData);
                templateData.put("faqItems", dynamicFaqs);
                templateData.put("faqSchema", generateFAQSchemaFromItems(dynamicFaqs));
                templateData.put("lowPrice", String.format("%,.0f", verdict.getPlan().getMustDo().stream()
                                .mapToDouble(RiskAdjustedItem::getAdjustedCost).min().orElse(0.0)));
                templateData.put("highPrice", String.format("%,.0f",
                                verdict.getPlan().getMustDo().stream().mapToDouble(RiskAdjustedItem::getAdjustedCost)
                                                .sum()));

                StringOutput output = new StringOutput();
                templateEngine.render("seo/static-verdict.jte", templateData, output);
                Path filePath = buildFilePath(outputBasePath, metroCode, era);
                Files.createDirectories(filePath.getParent());
                Files.writeString(filePath, minifyHtml(output.toString()));

                if (indexablePage) {
                        generatedUrls.add((String) templateData.get("canonicalUrl"));
                }

                // STRATEGY UPDATE:
                // L2 Detail Pages (Risk Items) are now handled DYNAMICALLY by
                // HomeRepairController.
                // We only pre-generate the L1 Verdict Pages (Seed Strategy) to keep build times
                // fast and file count low (~400).
                // The controller listens for .html requests and renders them on-the-fly.

                return generatedUrls;
        }

        private String generateComparisonInsight(DataMapping.MetroCityData mData, String metroName, long seed) {
                double laborMult = mData.getLaborMult();
                String context = "";
                double deltaPct = Math.abs(laborMult - 1.0) * 100.0;

                if (laborMult > 1.1) {
                        context = String.format(
                                        "%s contractor rates run about %.1f%% above the national baseline. Buyers should expect sellers to push back unless the packet stays anchored to lender-visible or quote-backed issues.",
                                        metroName, deltaPct);
                } else if (laborMult < 0.95) {
                        context = String.format(
                                        "%s labor runs about %.1f%% below the national baseline. That can make a seller credit more defensible, but only if the ask stays focused on inspection items that are easy to document.",
                                        metroName, deltaPct);
                } else {
                        context = String.format(
                                        "%s labor is within %.1f%% of the national median. In this kind of market, buyers usually win by presenting a cleaner packet rather than inflating every repair line.",
                                        metroName, deltaPct);
                }
                return context;
        }

        // Method 'generateRiskPage' removed as L2 pages are now dynamic.

        private String generateFAQSchemaFromItems(List<Map<String, String>> faqItems) {
                StringBuilder sb = new StringBuilder();
                sb.append("<script type=\"application/ld+json\">{");
                sb.append("\"@context\":\"https://schema.org\",");
                sb.append("\"@type\":\"FAQPage\",");
                sb.append("\"mainEntity\":[");

                for (int i = 0; i < faqItems.size(); i++) {
                        Map<String, String> item = faqItems.get(i);
                        sb.append("{");
                        sb.append("\"@type\":\"Question\",");
                        sb.append("\"name\":\"").append(item.get("question")).append("\",");
                        sb.append("\"acceptedAnswer\":{\"@type\":\"Answer\",\"text\":\"").append(item.get("answer"))
                                        .append("\"}");
                        sb.append("}");
                        if (i < faqItems.size() - 1)
                                sb.append(",");
                }

                sb.append("]}</script>");
                return sb.toString();
        }

        private List<Map<String, String>> generateDynamicFAQ(String metroName, String eraName,
                        VerdictDTOs.Verdict verdict,
                        DataMapping.MetroCityData mData) {
                List<Map<String, String>> faqs = new ArrayList<>();
                double totalCost = verdict.getPlan().getMustDo().stream()
                                .mapToDouble(VerdictDTOs.RiskAdjustedItem::getAdjustedCost).sum();
                String riskList = verdict.getPlan().getMustDo().stream().limit(3)
                                .map(VerdictDTOs.RiskAdjustedItem::getPrettyName).reduce((a, b) -> a + ", " + b)
                                .orElse("aging systems");

                RiskAdjustedItem firstRisk = verdict.getPlan().getMustDo().stream().findFirst().orElse(null);
                String firstRiskName = firstRisk != null ? firstRisk.getPrettyName() : "the top repair item";

                // Q1: Cost Specifics (Unique Data: Math Driven Cost + Era + Metro)
                Map<String, String> q1 = new HashMap<>();
                q1.put("question", String.format("How much should I budget from the inspection report for a %s home in %s?",
                                eraName.split("\\(")[0].trim(), metroName.split(",")[0].trim()));
                q1.put("answer", String.format(
                                "Based on localized %s labor indexes (%.2fx multiplier) and %s housing stock patterns, a buyer should expect roughly $%,.0f in repair burden after inspection. Use that number to frame the seller credit request around %s first.",
                                metroName, (mData != null ? mData.getLaborMult() : 1.0), eraName.split("\\(")[0].trim(),
                                totalCost, riskList));
                faqs.add(q1);

                // Q2: Inspection focus (Unique Entity Data: Climate/Soil/Risk)
                if (mData != null) {
                        Map<String, String> q2 = new HashMap<>();
                        q2.put("question",
                                        String.format("What should I ask the inspector to verify first in %s?", metroName));
                        q2.put("answer", String.format(
                                        "Start with %s. In %s, the regional pressure is %s, compounded by its %s climate classification. For %s properties, that combination usually accelerates deterioration in the %s and should be documented with photos, remaining-life notes, and contractor follow-up bids before you ask for credit.",
                                        firstRiskName, metroName, mData.getRisk(), mData.getClimateZone(), eraName.split(" ")[0],
                                        mData.getFoundation().toLowerCase().contains("basement")
                                                        ? "foundation and subsurface drainage"
                                                        : "roofing membrane and exterior envelope"));
                        faqs.add(q2);
                }

                // Q3: Negotiation (Unique Data: Mathematical Leverage Logic)
                Map<String, String> q3 = new HashMap<>();
                q3.put("question", "How much seller credit should I ask for after the inspection?");
                q3.put("answer", String.format(
                                "A reasonable opening request in %s is roughly %.1f%% of the documented repair burden: about $%,.0f on a $%,.0f estimate. Use the inspection notes plus at least one contractor quote to support the request, especially for items like %s.",
                                metroName,
                                80.0, totalCost * 0.8, totalCost, firstRiskName));
                faqs.add(q3);

                return faqs;
        }

        private String generateHowToSchema(String m, String e) {
                return String.format(
                                "<script type=\"application/ld+json\">{" +
                                                "\"@context\":\"https://schema.org\"," +
                                                "\"@type\":\"HowTo\"," +
                                                "\"name\":\"Preparing a seller credit request for %s homes in %s\"," +
                                                "\"step\":[" +
                                                "{\"@type\":\"HowToStep\",\"text\":\"Identify inspection items that should be included in the seller credit request for %s builds.\"},"
                                                +
                                                "{\"@type\":\"HowToStep\",\"text\":\"Apply %s regional labor multipliers to size a defensible ask.\"},"
                                                +
                                                "{\"@type\":\"HowToStep\",\"text\":\"Turn the inspection notes into a seller credit range before closing.\"}"
                                                +
                                                "]}</script>",
                                e, m, e, m);
        }

        private String generateBreadcrumbSchema(String m, String e, String u, String stateCode) {
                String stateUrl = stateCode != null
                                ? "https://lifeverdict.com/home-repair/verdicts/states/" + stateCode.toLowerCase()
                                                + ".html"
                                : "https://lifeverdict.com/home-repair";

                return String.format(
                                "<script type=\"application/ld+json\">{" +
                                                "\"@context\":\"https://schema.org\"," +
                                                "\"@type\":\"BreadcrumbList\"," +
                                                "\"itemListElement\":[" +
                                                "{\"@type\":\"ListItem\",\"position\":1,\"name\":\"Home\",\"item\":\"https://lifeverdict.com/\"},"
                                                +
                                                "{\"@type\":\"ListItem\",\"position\":2,\"name\":\"Inspection Negotiation\",\"item\":\"https://lifeverdict.com/home-repair\"},"
                                                +
                                                "{\"@type\":\"ListItem\",\"position\":3,\"name\":\"%s\",\"item\":\"%s\"},"
                                                +
                                                "{\"@type\":\"ListItem\",\"position\":4,\"name\":\"%s\",\"item\":\"%s\"}"
                                                +
                                                "]}</script>",
                                stateCode != null ? stateCode : "Region", stateUrl, m, u);
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

        private String buildCalculatorUrl(String metroCode, String era) {
                return "/home-repair?metroCode=" + metroCode + "&era=" + era + "&relationship=BUYING";
        }

        private static final Map<String, String> STATE_NAMES = Map.ofEntries(
                        Map.entry("AL", "Alabama"), Map.entry("AK", "Alaska"), Map.entry("AZ", "Arizona"),
                        Map.entry("AR", "Arkansas"),
                        Map.entry("CA", "California"), Map.entry("CO", "Colorado"), Map.entry("CT", "Connecticut"),
                        Map.entry("DE", "Delaware"), Map.entry("FL", "Florida"), Map.entry("GA", "Georgia"),
                        Map.entry("HI", "Hawaii"),
                        Map.entry("ID", "Idaho"), Map.entry("IL", "Illinois"), Map.entry("IN", "Indiana"),
                        Map.entry("IA", "Iowa"),
                        Map.entry("KS", "Kansas"), Map.entry("KY", "Kentucky"), Map.entry("LA", "Louisiana"),
                        Map.entry("ME", "Maine"),
                        Map.entry("MD", "Maryland"), Map.entry("MA", "Massachusetts"), Map.entry("MI", "Michigan"),
                        Map.entry("MN", "Minnesota"), Map.entry("MS", "Mississippi"), Map.entry("MO", "Missouri"),
                        Map.entry("MT", "Montana"), Map.entry("NE", "Nebraska"), Map.entry("NV", "Nevada"),
                        Map.entry("NH", "New Hampshire"), Map.entry("NJ", "New Jersey"), Map.entry("NM", "New Mexico"),
                        Map.entry("NY", "New York"), Map.entry("NC", "North Carolina"), Map.entry("ND", "North Dakota"),
                        Map.entry("OH", "Ohio"), Map.entry("OK", "Oklahoma"), Map.entry("OR", "Oregon"),
                        Map.entry("PA", "Pennsylvania"), Map.entry("RI", "Rhode Island"),
                        Map.entry("SC", "South Carolina"),
                        Map.entry("SD", "South Dakota"), Map.entry("TN", "Tennessee"), Map.entry("TX", "Texas"),
                        Map.entry("UT", "Utah"), Map.entry("VT", "Vermont"), Map.entry("VA", "Virginia"),
                        Map.entry("WA", "Washington"), Map.entry("WV", "West Virginia"), Map.entry("WI", "Wisconsin"),
                        Map.entry("WY", "Wyoming"), Map.entry("DC", "District of Columbia"));

        public int generateStateHubPages(String outputBasePath) throws IOException {
                List<String> metroCodes = new ArrayList<>(verdictEngineService.getMetroMasterData().getData().keySet());
                writeStateHubPages(metroCodes, outputBasePath);
                return getAllStates(metroCodes).size();
        }

        private void writeStateHubPages(List<String> codes, String outputBasePath) throws IOException {
                Map<String, List<String>> byState = new HashMap<>();
                for (String c : codes) {
                        String s = extractStateCode(c);
                        if (s != null)
                                byState.computeIfAbsent(s, k -> new ArrayList<>()).add(c);
                }
                for (var entry : byState.entrySet()) {
                        String stateCode = entry.getKey();
                        boolean indexableStateHub = isIndexableStateHub(stateCode);
                        String fullStateName = STATE_NAMES.getOrDefault(stateCode, stateCode);
                        String url = "https://lifeverdict.com/home-repair/verdicts/states/" + stateCode.toLowerCase()
                                        + ".html";

                        List<StateHubPage.CityData> cities = new ArrayList<>();
                        for (String cityCode : entry.getValue()) {
                                List<InternalLinkBuilder.InternalLink> links = new ArrayList<>();
                                for (String era : ALL_ERAS) {
                                        if (isIndexableVerdictPage(cityCode, era)) {
                                                links.add(new InternalLinkBuilder.InternalLink(
                                                                TextUtil.formatEraText(era),
                                                                buildCanonicalUrl(cityCode, era)
                                                                                .replace("https://lifeverdict.com", "")));
                                        }
                                }
                                if (!links.isEmpty()) {
                                        cities.add(new StateHubPage.CityData(TextUtil.formatMetroName(cityCode), links));
                                }
                        }

                        String breadcrumbSchema = String.format(
                                        "<script type=\"application/ld+json\">{" +
                                                        "\"@context\":\"https://schema.org\"," +
                                                        "\"@type\":\"BreadcrumbList\"," +
                                                        "\"itemListElement\":[" +
                                                        "{\"@type\":\"ListItem\",\"position\":1,\"name\":\"Home\",\"item\":\"https://lifeverdict.com/\"},"
                                                        +
                                                        "{\"@type\":\"ListItem\",\"position\":2,\"name\":\"Inspection Negotiation\",\"item\":\"https://lifeverdict.com/home-repair\"},"
                                                        +
                                                        "{\"@type\":\"ListItem\",\"position\":3,\"name\":\"%s\",\"item\":\"%s\"}"
                                                        +
                                                        "]}</script>",
                                        fullStateName, url);

                        StateHubPage page = new StateHubPage(
                                        stateCode,
                                        fullStateName,
                                        url,
                                        breadcrumbSchema,
                                        indexableStateHub ? "index,follow" : "noindex,follow",
                                        indexableStateHub,
                                        cities);

                        StringOutput output = new StringOutput();
                        templateEngine.render("seo/static-state-hub.jte", Collections.singletonMap("page", page),
                                        output);
                        Path path = Paths.get(outputBasePath.replace("verdicts", "verdicts/states"),
                                        stateCode.toLowerCase() + ".html");
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

        private static String verdictKey(String metroCode, String era) {
                return metroCode + "|" + era;
        }

        private boolean isIndexableVerdictPage(String metroCode, String era) {
                return INDEXABLE_VERDICT_KEYS.contains(verdictKey(metroCode, era));
        }

        private boolean isIndexableStateHub(String stateCode) {
                return INDEXABLE_STATE_CODES.contains(stateCode);
        }

        private List<InternalLink> keepIndexableLinks(List<InternalLink> links) {
                if (links == null || links.isEmpty()) {
                        return List.of();
                }
                return links.stream()
                                .filter(link -> link != null && isIndexableLink(link.getHref()))
                                .toList();
        }

        private boolean isIndexableLink(String href) {
                if (href == null || href.isBlank()) {
                        return false;
                }
                if (href.contains("/home-repair/verdicts/states/")) {
                        String stateSlug = href.substring(href.lastIndexOf('/') + 1)
                                        .replace(".html", "")
                                        .toUpperCase(Locale.ENGLISH);
                        return isIndexableStateHub(stateSlug);
                }

                String[] segments = href.split("/");
                if (segments.length < 5) {
                        return false;
                }
                String metroCode = segments[3].toUpperCase(Locale.ENGLISH).replace("-", "_");
                String eraCode = segments[4].replace(".html", "").toUpperCase(Locale.ENGLISH).replace("-", "_");
                return isIndexableVerdictPage(metroCode, eraCode);
        }
}
