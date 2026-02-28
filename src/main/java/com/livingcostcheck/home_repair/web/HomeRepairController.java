package com.livingcostcheck.home_repair.web;

import com.livingcostcheck.home_repair.domain.EventLog;
import com.livingcostcheck.home_repair.domain.VerdictHistory;
import com.livingcostcheck.home_repair.repository.EventLogRepository;
import com.livingcostcheck.home_repair.repository.HomeRepairRepository;
import com.livingcostcheck.home_repair.service.VerdictEngineService;
import com.livingcostcheck.home_repair.service.dto.verdict.DataMapping;
import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.*;
import com.livingcostcheck.home_repair.seo.VerdictSeoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.servlet.view.RedirectView;
import com.livingcostcheck.home_repair.util.TextUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Main controller for Home Repair Verdict Engine
 */
@Slf4j
@Controller
@RequestMapping("/home-repair")
@RequiredArgsConstructor
public class HomeRepairController {

        private static final Pattern HTML_TAG_PATTERN = Pattern.compile("<[^>]+>");
        private static final Pattern MULTI_SPACE_PATTERN = Pattern.compile("\\s{2,}");
        private static final Pattern NON_ALNUM_PATTERN = Pattern.compile("[^a-z0-9]+");
        private static final Pattern INTERNAL_MARKER_PATTERN = Pattern
                        .compile("\\[[A-Z_ ]+\\]|ERA_RISK:|ERA_LABOR_ADJUSTMENT:");
        private static final Set<String> SEO_STOPWORDS = Set.of(
                        "the", "and", "for", "with", "from", "that", "this", "your", "into", "over",
                        "when", "after", "before", "while", "without", "across", "under", "about",
                        "home", "homes", "house", "houses", "system", "systems", "component", "components",
                        "cost", "costs", "repair", "repairs", "replacement", "replacements", "local", "regional",
                        "market", "estimated", "estimate", "safety", "risk", "risks", "data", "standard",
                        "standards", "common", "critical");

        private final HomeRepairRepository repository;
        private final EventLogRepository eventLogRepository;
        private final VerdictEngineService verdictEngineService;
        private final com.livingcostcheck.home_repair.seo.VerdictSeoService verdictSeoService;

        @GetMapping
        public String index(Model model) {
                // Step 1: Landing Page (Location & Era)

                // Prepare Metros List
                List<String> metros = verdictEngineService.getMetroMasterData().getData().keySet().stream()
                                .sorted()
                                .toList();

                // Prepare Eras List (Ordered Chronologically)
                List<String> eras = java.util.List.of(
                                "PRE_1950",
                                "1950_1970",
                                "1970_1980",
                                "1980_1995",
                                "1995_2010",
                                "2010_PRESENT");

                model.addAttribute("metros", metros);
                model.addAttribute("eras", eras);
                model.addAttribute("title", "Buying a Fixer-Upper? Don't Sign Until You See This Verdict.");

                return "pages/index";
        }

        @PostMapping("/step-2")
        public String step2(@RequestParam("metroCode") String metroCode,
                        @RequestParam("era") String era,
                        @RequestParam("relationship") String relationship,
                        Model model) {
                // Step 2: Context Form
                model.addAttribute("metroCode", metroCode);
                model.addAttribute("era", era);
                model.addAttribute("relationship", relationship);

                // Context Briefing (Trust Anchor)
                model.addAttribute("contextBriefing", verdictEngineService.getPrecalcBriefing(metroCode, era));

                return "pages/context";
        }

        @PostMapping("/verdict")
        public String generateVerdict(
                        @RequestParam("metroCode") String metroCode,
                        @RequestParam("era") String era,
                        @RequestParam(value = "budget", defaultValue = "0") Double budget,
                        @RequestParam(value = "sqft", required = false) Double sqft,
                        @RequestParam(value = "relationship", defaultValue = "LIVING") String relationshipStr,
                        @RequestParam(value = "history", required = false) List<String> history,
                        @RequestParam(value = "condition", defaultValue = "UNKNOWN") String condition,
                        @RequestParam(value = "isFpePanel", defaultValue = "false") Boolean isFpePanel,
                        @RequestParam(value = "isPolyB", defaultValue = "false") Boolean isPolyB,
                        @RequestParam(value = "isAluminum", defaultValue = "false") Boolean isAluminum,
                        @RequestParam(value = "isChineseDrywall", defaultValue = "false") Boolean isChineseDrywall,
                        @RequestParam(value = "bathrooms", required = false) Integer bathrooms,
                        @RequestParam(value = "stories", required = false) Integer stories,
                        @RequestParam(value = "roofType", defaultValue = "ASPHALT") String roofType,
                        @RequestParam(value = "userEmail", defaultValue = "anonymous") String userEmail,
                        Model model) {

                try {
                        // Manual Context Construction to prevent mapping errors
                        RelationshipToHouse relationship = RelationshipToHouse.LIVING;
                        try {
                                relationship = RelationshipToHouse.valueOf(relationshipStr);
                        } catch (Exception e) {
                                log.warn("Invalid relationship param: {}", relationshipStr);
                        }

                        UserContext context = UserContext.builder()
                                        .metroCode(metroCode)
                                        .era(era)
                                        .budget(budget)
                                        .sqft(sqft != null ? sqft.intValue() : 1800)
                                        .relationship(relationship)
                                        .history(history != null ? history : java.util.Collections.emptyList())
                                        .condition(condition)
                                        .isFpePanel(isFpePanel)
                                        .isPolyB(isPolyB)
                                        .isAluminum(isAluminum)
                                        .isChineseDrywall(isChineseDrywall)
                                        .bathrooms(bathrooms)
                                        .stories(stories)
                                        .roofType(roofType)
                                        .build();

                        // 1. Generate Verdict
                        Verdict verdict = verdictEngineService.generateVerdict(context);

                        // 2. Persistence (History)
                        VerdictHistory verdictHistory = new VerdictHistory(
                                        context.getMetroCode(),
                                        String.valueOf(context.getBudget()),
                                        context.getRelationship().name(),
                                        context.getEra(),
                                        verdict.getTier(), // Code/Result
                                        "v2026.01",
                                        String.valueOf(context.hashCode()) // Simple hash for context
                        );
                        if (!"anonymous".equals(userEmail)) {
                                verdictHistory.setUserEmail(userEmail);
                        }

                        // Save detailed context for re-generation
                        String historyStr = context.getHistory() != null ? String.join(",", context.getHistory()) : "";
                        verdictHistory.setRepairContext(historyStr, context.getCondition());
                        verdictHistory.setForensicClues(
                                        context.getIsFpePanel(),
                                        context.getIsPolyB(),
                                        context.getIsAluminum(),
                                        context.getIsChineseDrywall());

                        repository.save(verdictHistory);

                        return "redirect:/home-repair/result/" + verdictHistory.getId();
                } catch (Exception e) {
                        log.error("Error generating verdict", e);
                        model.addAttribute("errorMessage",
                                        "An error occurred while generating your estimated repair costs. Please try again.");
                        return "error";
                }
        }

        @GetMapping("/result/{uuid}")
        public String result(@PathVariable("uuid") UUID uuid, Model model,
                        jakarta.servlet.http.HttpServletResponse response) {
                response.setHeader("X-Robots-Tag", "noindex");
                try {
                        VerdictHistory history = repository.findById(uuid)
                                        .orElseThrow(() -> new IllegalArgumentException("Invalid Verdict ID"));

                        if (log.isDebugEnabled()) {
                                log.debug("Loading result for ID: {}", uuid);
                        }

                        // Safe Double Parsing
                        double parsedBudget = 0.0;
                        try {
                                if (history.getBudget() != null && !history.getBudget().equalsIgnoreCase("null")) {
                                        parsedBudget = Double.parseDouble(history.getBudget());
                                }
                        } catch (NumberFormatException e) {
                                log.warn("Failed to parse budget: {}", history.getBudget());
                        }

                        // Re-construct Context from History
                        RelationshipToHouse relationship = RelationshipToHouse.LIVING;
                        try {
                                relationship = RelationshipToHouse.valueOf(history.getPurpose());
                        } catch (Exception e) {
                                log.warn("Failed to parse relationship from history: {}", history.getPurpose());
                        }

                        // Split history string back into lists (Simple parsing for MVP)
                        List<String> combinedHistory = history.getRepairHistory() != null
                                        && !history.getRepairHistory().isEmpty()
                                                        ? java.util.Arrays.asList(history.getRepairHistory().split(","))
                                                        : java.util.Collections.emptyList();

                        // Distribute based on known prefixes or lists
                        List<String> coreHistory = new java.util.ArrayList<>();
                        List<String> livingHistory = new java.util.ArrayList<>();

                        for (String h : combinedHistory) {
                                if (h.contains("ROOF") || h.contains("HVAC") || h.contains("ELEC_PANEL")
                                                || h.contains("PLUMBING")) {
                                        coreHistory.add(h);
                                } else {
                                        livingHistory.add(h);
                                }
                        }

                        UserContext context = UserContext.builder()
                                        .metroCode(history.getZipCode()) // Storing MetroCode in ZipCode field for now
                                        .era(history.getDecade())
                                        .budget(parsedBudget)
                                        .relationship(relationship)
                                        .purpose(history.getPurpose()) // Deprecated but populated
                                        .history(combinedHistory) // Keep deprecated for compat
                                        .coreSystemHistory(coreHistory)
                                        .livingSpaceHistory(livingHistory)
                                        .condition(history.getHouseCondition() != null ? history.getHouseCondition()
                                                        : "UNKNOWN")
                                        .isFpePanel(history.getIsFpePanel())
                                        .isPolyB(history.getIsPolyB())
                                        .isAluminum(history.getIsAluminum())
                                        .isChineseDrywall(history.getIsChineseDrywall())
                                        .build();

                        Verdict verdict = verdictEngineService.generateVerdict(context);

                        // CTR Optimization: Verdict-First Titles & Decision-Oriented H1s
                        String city = TextUtil.formatMetroName(history.getZipCode());

                        // Use VerdictSeoService for "Outlook" headers (Contextual)
                        VerdictSeoService.SeoVariant seoVariant = verdictSeoService.getDynamicResultHeader(verdict,
                                        city);

                        model.addAttribute("title", seoVariant.title());
                        model.addAttribute("verdictH1", seoVariant.h1());
                        model.addAttribute("verdict", verdict);
                        model.addAttribute("history", history);
                        return "pages/result";
                } catch (Exception e) {
                        log.error("Error displaying result page", e);
                        model.addAttribute("errorMessage", "Unable to load result. Please try again.");
                        return "error";
                }
        }

        // -------------------------------------------------------------------------
        // STATIC INFO PAGES
        // -------------------------------------------------------------------------
        // -------------------------------------------------------------------------
        // STATIC INFO PAGES
        // -------------------------------------------------------------------------
        @GetMapping("/about")
        public String about(Model model) {
                model.addAttribute("baseUrl", "https://lifeverdict.com");
                return "pages/about";
        }

        @GetMapping("/methodology")
        public String methodology(Model model) {
                model.addAttribute("baseUrl", "https://lifeverdict.com");
                return "pages/methodology";
        }

        @GetMapping("/editorial-policy")
        public String editorialPolicy(Model model) {
                model.addAttribute("baseUrl", "https://lifeverdict.com");
                return "pages/editorial-policy";
        }

        @GetMapping("/data-sources")
        public String dataSources(Model model) {
                model.addAttribute("baseUrl", "https://lifeverdict.com");
                return "pages/data-sources";
        }

        @GetMapping("/disclaimer")
        public String disclaimer(Model model) {
                model.addAttribute("baseUrl", "https://lifeverdict.com");
                return "pages/disclaimer";
        }

        // -------------------------------------------------------------------------
        // DYNAMIC LEVEL 2: RISK DETAIL PSEO
        // -------------------------------------------------------------------------
        @GetMapping("/verdicts/{metro}/{era}/{riskItem}")
        public Object viewRiskDetail(@PathVariable String metro,
                        @PathVariable String era,
                        @PathVariable String riskItem,
                        Model model,
                        jakarta.servlet.http.HttpServletResponse response) {

                String canonicalSlug = riskItem.trim().toLowerCase();
                boolean needsRedirect = false;
                while (canonicalSlug.endsWith(".html") || canonicalSlug.endsWith(".htm")) {
                        needsRedirect = true;
                        if (canonicalSlug.endsWith(".html")) {
                                canonicalSlug = canonicalSlug.substring(0, canonicalSlug.length() - 5);
                        } else if (canonicalSlug.endsWith(".htm")) {
                                canonicalSlug = canonicalSlug.substring(0, canonicalSlug.length() - 4);
                        }
                }

                if (needsRedirect) {
                        RedirectView rv = new RedirectView(
                                        "/home-repair/verdicts/" + metro + "/" + era + "/" + canonicalSlug);
                        rv.setStatusCode(org.springframework.http.HttpStatus.MOVED_PERMANENTLY);
                        return rv;
                }

                if (!canonicalSlug.matches("^[a-z0-9-]+$") || canonicalSlug.contains("../")) {
                        throw new org.springframework.web.server.ResponseStatusException(
                                        org.springframework.http.HttpStatus.NOT_FOUND);
                }

                // 1. Generate core verdict logic
                UserContext context = UserContext.builder()
                                .metroCode(metro.replace("-", "_").toUpperCase())
                                .era(era.replace("-", "_").toUpperCase())
                                .budget(0.0) // Info page assumption
                                .relationship(RelationshipToHouse.LIVING)
                                .build();

                Verdict verdict = verdictEngineService.generateVerdict(context);

                // 2. Find specific risk item
                final String finalSlug = canonicalSlug;
                RiskAdjustedItem targetItem = verdict.getPlan().getMustDo().stream()
                                .filter(item -> item.getItemCode().toLowerCase().replace("_", "-")
                                                .equals(finalSlug))
                                .findFirst()
                                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                                                org.springframework.http.HttpStatus.NOT_FOUND));

                // 3. Prepare Model for Template (same as StaticPageGenerator)
                String metroName = TextUtil.formatMetroName(context.getMetroCode());
                String eraName = TextUtil.formatEraText(context.getEra());

                // Phase 2: Add Metro Metadata & Regional Insight
                var metroData = verdictEngineService.getMetroMasterData().getData().get(context.getMetroCode());
                DataMapping.MetroUniqueSignal uniqueSignal = null;
                if (verdictEngineService.getMetroUniqueSignalsData() != null
                                && verdictEngineService.getMetroUniqueSignalsData().getData() != null) {
                        uniqueSignal = verdictEngineService.getMetroUniqueSignalsData().getData()
                                        .get(context.getMetroCode());
                }
                String msaName = TextUtil.formatMsaName(context.getMetroCode());
                if (uniqueSignal != null && uniqueSignal.getMsaName() != null
                                && !uniqueSignal.getMsaName().isBlank()) {
                        msaName = uniqueSignal.getMsaName();
                }

                String climateZone = "US-Standard";
                String metroRisk = "Standard Risks";
                String foundation = "Standard";
                double laborMult = 1.0;

                if (metroData != null) {
                        climateZone = metroData.getClimateZone();
                        metroRisk = metroData.getRisk();
                        foundation = metroData.getFoundation();
                        laborMult = metroData.getLaborMult() != null ? metroData.getLaborMult() : 1.0;
                }

                long seed = (context.getMetroCode() + context.getEra()).hashCode();
                String regionalInsight = com.livingcostcheck.home_repair.seo.FragmentLibrary.generateRegionalInsight(
                                climateZone, context.getEra(), laborMult, metroName, seed);
                if (uniqueSignal != null && uniqueSignal.getFemaMajorDisaster10y() != null
                                && uniqueSignal.getMedianYearBuilt() != null && uniqueSignal.getStateCode() != null) {
                        regionalInsight = regionalInsight + " Public records show "
                                        + uniqueSignal.getFemaMajorDisaster10y()
                                        + " major FEMA disaster declarations since 2016 in "
                                        + uniqueSignal.getStateCode() + ", and statewide median housing stock built in "
                                        + uniqueSignal.getMedianYearBuilt() + ".";
                }

                model.addAttribute("title",
                                String.format("Don't Overpay: %s Cost in %s [2026 Data]",
                                                targetItem.getPrettyName(), metroName));
                model.addAttribute("targetItem", targetItem); // Template expects 'item' or we map it
                model.addAttribute("item", targetItem); // Mapping to 'item' as per template
                model.addAttribute("itemSlug", finalSlug);
                model.addAttribute("verdict", verdict);
                model.addAttribute("metroCode", context.getMetroCode());
                model.addAttribute("metroName", metroName);
                model.addAttribute("msaName", msaName);
                model.addAttribute("era", era);
                model.addAttribute("eraName", eraName);
                model.addAttribute("baseUrl", "https://lifeverdict.com");

                // Injected Data
                model.addAttribute("regionalInsight", regionalInsight);
                model.addAttribute("climateZone", climateZone);
                model.addAttribute("metroRisk", metroRisk);
                model.addAttribute("foundation", foundation);
                model.addAttribute("openDataCsvUrl", "/data/metro_unique_signals_2026.csv");
                if (uniqueSignal != null) {
                        model.addAttribute("femaDisasterCount", uniqueSignal.getFemaMajorDisaster10y());
                        model.addAttribute("ownerOccupancyRate", uniqueSignal.getOwnerOccupancyRatePct());
                        model.addAttribute("medianYearBuilt", uniqueSignal.getMedianYearBuilt());
                        model.addAttribute("repairPressureIndex", uniqueSignal.getRepairPressureIndex());
                }

                // Internal Links (Simplified for Dynamic)
                String parentUrl = "/home-repair/verdicts/" + metro + "/" + era + ".html";
                model.addAttribute("parentUrl", parentUrl);
                model.addAttribute("canonicalUrl",
                                "https://lifeverdict.com/home-repair/verdicts/" + metro + "/" + era + "/" + finalSlug);

                String localDateString = java.time.LocalDate.now()
                                .format(java.time.format.DateTimeFormatter.ofPattern("MMMM yyyy",
                                                java.util.Locale.ENGLISH));
                model.addAttribute("dateString", localDateString);
                model.addAttribute("h1Content",
                                String.format("%s Replacement Cost in %s (%s)", targetItem.getPrettyName(), metroName,
                                                eraName));

                java.util.List<com.livingcostcheck.home_repair.seo.InternalLinkBuilder.InternalLink> otherRisks = verdict
                                .getPlan().getMustDo().stream()
                                .filter(i -> !i.getItemCode().equalsIgnoreCase(targetItem.getItemCode()))
                                .limit(5)
                                .map(i -> new com.livingcostcheck.home_repair.seo.InternalLinkBuilder.InternalLink(
                                                i.getPrettyName(),
                                                "/home-repair/verdicts/" + metro + "/" + era + "/"
                                                                + i.getItemCode().toLowerCase().replace("_", "-")))
                                .collect(java.util.stream.Collectors.toList());
                model.addAttribute("otherRisks", otherRisks);

                // L2 indexing gate: default to noindex unless minimum content-quality
                // thresholds are met.
                boolean shouldIndexRiskDetail = shouldIndexRiskDetail(targetItem, verdict, metroData);
                String robotsDirective = shouldIndexRiskDetail ? "index,follow" : "noindex,follow";
                model.addAttribute("robotsDirective", robotsDirective);
                if (!shouldIndexRiskDetail) {
                        response.setHeader("X-Robots-Tag", "noindex,follow");
                }

                // Helper Schemas (Dynamic)
                String breadcrumbSchema = String.format(
                                "<script type=\"application/ld+json\">{" +
                                                "\"@context\":\"https://schema.org\"," +
                                                "\"@type\":\"BreadcrumbList\"," +
                                                "\"itemListElement\":[" +
                                                "{\"@type\":\"ListItem\",\"position\":1,\"name\":\"Home\",\"item\":\"https://lifeverdict.com/\"},"
                                                +
                                                "{\"@type\":\"ListItem\",\"position\":2,\"name\":\"Market Data\",\"item\":\"https://lifeverdict.com/home-repair\"},"
                                                +
                                                "{\"@type\":\"ListItem\",\"position\":3,\"name\":\"%s %s\",\"item\":\"https://lifeverdict.com"
                                                + parentUrl + "\"},"
                                                +
                                                "{\"@type\":\"ListItem\",\"position\":4,\"name\":\"%s\",\"item\":\"%s\"}"
                                                +
                                                "]}</script>",
                                metroName, eraName, targetItem.getPrettyName(),
                                "https://lifeverdict.com/home-repair/verdicts/" + metro + "/" + era + "/" + finalSlug);

                String faqSchema = String.format(
                                "<script type=\"application/ld+json\">{" +
                                                "\"@context\":\"https://schema.org\"," +
                                                "\"@type\":\"FAQPage\"," +
                                                "\"mainEntity\":[" +
                                                "{\"@type\":\"Question\",\"name\":\"How much does %s cost in %s?\",\"acceptedAnswer\":{\"@type\":\"Answer\",\"text\":\"The estimated cost is around $%,.0f, varying by local labor rates.\"}},"
                                                +
                                                "{\"@type\":\"Question\",\"name\":\"Is replacing %s mandatory?\",\"acceptedAnswer\":{\"@type\":\"Answer\",\"text\":\"%s\"}}"
                                                +
                                                "]}</script>",
                                targetItem.getPrettyName().toLowerCase(), metroName, targetItem.getAdjustedCost(),
                                targetItem.getPrettyName().toLowerCase(),
                                targetItem.isMandatory()
                                                ? "Yes, it is highly recommended and often required for insurance or safety compliance."
                                                : "It is recommended based on standard property longevity.");

                model.addAttribute("faqSchema", faqSchema);
                model.addAttribute("breadcrumbSchema", breadcrumbSchema);

                return "seo/static-risk-detail";
        }

        // -------------------------------------------------------------------------
        // API & TRACKING (AJAX/Redirects)
        // -------------------------------------------------------------------------

        @PostMapping("/api/lead")
        @ResponseBody
        public ResponseEntity<String> captureLead(@RequestParam("verdictId") UUID verdictId,
                        @RequestParam("email") String email) {
                VerdictHistory history = repository.findById(verdictId)
                                .orElseThrow(() -> new IllegalArgumentException("Invalid ID"));

                history.setUserEmail(email);
                repository.save(history);

                eventLogRepository.save(new EventLog(verdictId, EventLog.EventType.SUBMIT_EMAIL, email));

                // Save lead to CSV
                try {
                        java.io.File file = new java.io.File("data/leads.csv");
                        file.getParentFile().mkdirs();
                        synchronized (this) {
                                boolean isNew = !file.exists();
                                try (java.io.FileWriter fw = new java.io.FileWriter(file, true);
                                                java.io.PrintWriter pw = new java.io.PrintWriter(fw)) {
                                        if (isNew) {
                                                pw.println("Timestamp,VerdictId,Email,MetroCode,Era");
                                        }
                                        String timestamp = java.time.LocalDateTime.now()
                                                        .format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                                        pw.printf("%s,%s,%s,%s,%s%n",
                                                        timestamp,
                                                        verdictId.toString(),
                                                        email,
                                                        history.getZipCode(),
                                                        history.getDecade());
                                }
                        }
                } catch (Exception e) {
                        log.error("Failed to write lead to CSV", e);
                }

                return ResponseEntity.ok(
                                "<div class='p-3 bg-emerald-50 border border-emerald-200 text-emerald-800 rounded-lg text-sm text-center font-medium'><strong>Success!</strong> Prepared checklist has been securely linked to "
                                                + HtmlUtils.htmlEscape(email)
                                                + ".<br><span class='font-normal text-xs mt-1 block'>Next step: Use the 'Save as PDF' button below to keep a local copy for your records.</span></div>");
        }

        @GetMapping("/track")
        public RedirectView trackClick(@RequestParam("verdictId") UUID verdictId,
                        @RequestParam("type") String type,
                        @RequestParam("target") String target) {

                // OPEN REDIRECT FIX: Validate target against whitelist
                if (!isValidTarget(target)) {
                        return new RedirectView("/home-repair");
                }

                EventLog.EventType eventType = "AD".equalsIgnoreCase(type) ? EventLog.EventType.CLICK_AD
                                : EventLog.EventType.CLICK_AFFILIATE;
                try {
                        eventLogRepository.save(new EventLog(verdictId, eventType, target));
                } catch (Exception e) {
                        log.error("Error logging tracking event", e);
                }

                return new RedirectView(target);
        }

        private static final List<String> ALLOWED_DOMAINS = List.of(
                        "angi.com", "homeadvisor.com", "thumbtack.com", "networx.com",
                        "shareasale.com", "cj.com", "impact.com",
                        "choicehomewarranty.com", "selecthomewarranty.com",
                        "localhost");

        private boolean isValidTarget(String target) {
                if (target == null)
                        return false;
                // Allow only known affiliate/partner domains (prevents open redirect)
                try {
                        java.net.URI uri = new java.net.URI(target);
                        String host = uri.getHost();
                        if (host == null)
                                return false;
                        return ALLOWED_DOMAINS.stream()
                                        .anyMatch(domain -> host.equals(domain) || host.endsWith("." + domain));
                } catch (Exception e) {
                        return false;
                }
        }
        // -------------------------------------------------------------------------
        // DYNAMIC SEO PAGES: STATE INDEX & RISK HUBS
        // -------------------------------------------------------------------------

        @GetMapping("/verdicts/states")
        public String statesHub(Model model) {
                List<String> states = verdictEngineService.getMetroMasterData().getData().keySet().stream()
                                .map(this::extractStateCode)
                                .filter(Objects::nonNull)
                                .distinct()
                                .sorted()
                                .toList();
                model.addAttribute("states", states);
                model.addAttribute("baseUrl", "https://lifeverdict.com");
                return "seo/state-index";
        }

        @GetMapping("/risks")
        public String riskIndex(Model model) {
                List<String> hubs = java.util.Arrays.asList(
                                "knob-and-tube-wiring",
                                "polybutylene-pipes",
                                "fpe-electrical-panel",
                                "asbestos-risk",
                                "galvanized-pipes");
                model.addAttribute("hubs", hubs);
                return "seo/risk-index";
        }

        @GetMapping("/risks/{riskSlug}")
        public Object riskHub(@PathVariable String riskSlug, Model model) {
                java.util.Map<String, String> titles = java.util.Map.of(
                                "knob-and-tube-wiring", "Knob and Tube Wiring Replacement Guide",
                                "polybutylene-pipes", "Polybutylene Pipe Replacement Guide",
                                "fpe-electrical-panel", "FPE Electrical Panel Upgrade Guide",
                                "asbestos-risk", "Asbestos Detection & Abatement Guide",
                                "galvanized-pipes", "Galvanized Plumbing Replacement Guide");

                if (!titles.containsKey(riskSlug)) {
                        throw new org.springframework.web.server.ResponseStatusException(
                                        org.springframework.http.HttpStatus.NOT_FOUND);
                }

                java.util.List<java.util.Map<String, String>> faqList = new java.util.ArrayList<>();
                if (riskSlug.equals("knob-and-tube-wiring")) {
                        faqList.add(java.util.Map.of("q", "How much does Knob and Tube wiring replacement cost?", "a",
                                        "Rewiring a home with K&T typically ranges from $5,000 to $15,000+, depending on the square footage and ease of access through walls."));
                        faqList.add(java.util.Map.of("q", "Can I negotiate this during home buying?", "a",
                                        "Yes. Since uninsurable K&T wiring is a major fire hazard and limits financing options, buyers should request a full seller credit or require replacement before closing."));
                        faqList.add(java.util.Map.of("q", "How do inspectors check for this?", "a",
                                        "Inspectors look for the characteristic ceramic knobs and tubes holding wires in the basement, attic, or exposed wall cavities."));
                } else if (riskSlug.equals("polybutylene-pipes")) {
                        faqList.add(java.util.Map.of("q", "How much does Polybutylene pipe replacement cost?", "a",
                                        "Repiping a whole house to replace PB pipes generally costs between $4,000 and $10,000. Drywall repairs can add to the final bill."));
                        faqList.add(java.util.Map.of("q", "Can I negotiate this during home buying?", "a",
                                        "Absolutely. PB pipes have a history of catastrophic failure and ruptures. Most home insurers require them replaced, so you must negotiate a seller concession."));
                        faqList.add(java.util.Map.of("q", "How do inspectors check for this?", "a",
                                        "Inspectors look for grey, blue, or black plastic pipes stamped with 'PB2110', often found near the water heater or under sinks."));
                } else if (riskSlug.equals("fpe-electrical-panel")) {
                        faqList.add(java.util.Map.of("q", "How much does an FPE electrical panel upgrade cost?", "a",
                                        "Replacing a Federal Pacific Electric (FPE) panel usually costs between $1,500 and $2,500 for a standard 200-amp service."));
                        faqList.add(java.util.Map.of("q", "Can I negotiate this during home buying?", "a",
                                        "Yes. FPE Stab-Lok panels are a known fire hazard because breakers fail to trip. You should demand a licensed electrician replace it as a condition of sale."));
                        faqList.add(java.util.Map.of("q", "How do inspectors check for this?", "a",
                                        "Inspectors identify the 'Federal Pacific' logo or 'Stab-Lok' breakers, which are easily recognized in the main electrical panel."));
                } else if (riskSlug.equals("asbestos-risk")) {
                        faqList.add(java.util.Map.of("q", "How much does Asbestos abatement cost?", "a",
                                        "Asbestos removal costs between $1,500 and $3,000+ depending on the material (e.g., popcorn ceilings, VAT tiles or pipe insulation) and containment required."));
                        faqList.add(java.util.Map.of("q", "Can I negotiate this during home buying?", "a",
                                        "Yes. If materials are friable (easily crumbled), it is a severe health hazard. Buyers usually negotiate removal credits, though intact asbestos might just be documented."));
                        faqList.add(java.util.Map.of("q", "How do inspectors check for this?", "a",
                                        "Inspectors visually identify suspect materials common in pre-1980s homes, but confirmation requires sending a physical sample to a certified lab."));
                } else {
                        faqList.add(java.util.Map.of("q", "How much does Galvanized plumbing replacement cost?", "a",
                                        "Whole-house repiping to replace galvanized steel pipes typically costs $3,000 to $8,000, depending on accessibility and the number of fixtures."));
                        faqList.add(java.util.Map.of("q", "Can I negotiate this during home buying?", "a",
                                        "Yes. Galvanized pipes corrode from the inside out, causing low water pressure and rust. Due to the impending need for total replacement, buyers should ask for a credit."));
                        faqList.add(java.util.Map.of("q", "How do inspectors check for this?", "a",
                                        "Inspectors use a magnet (which sticks to steel) and check for rust at joints or drop in water pressure when multiple fixtures run."));
                }

                model.addAttribute("faqItems", faqList);

                model.addAttribute("riskSlug", riskSlug);
                model.addAttribute("titleH1", titles.get(riskSlug));
                model.addAttribute("l1Links", buildL1LinksForRiskHub());
                model.addAttribute("baseUrl", "https://lifeverdict.com");
                return "seo/risk-hub";
        }

        private List<com.livingcostcheck.home_repair.seo.InternalLinkBuilder.InternalLink> buildL1LinksForRiskHub() {
                List<String> metroCodes = verdictEngineService.getMetroMasterData().getData().keySet().stream()
                                .sorted()
                                .toList();
                if (metroCodes.isEmpty()) {
                        return java.util.Collections.emptyList();
                }

                List<String> eraCycle = java.util.List.of(
                                "PRE_1950",
                                "1950_1970",
                                "1970_1980",
                                "1980_1995",
                                "1995_2010",
                                "2010_PRESENT");

                int desiredCount = Math.min(12, metroCodes.size());
                List<com.livingcostcheck.home_repair.seo.InternalLinkBuilder.InternalLink> links = new ArrayList<>(
                                desiredCount);

                for (int i = 0; i < desiredCount; i++) {
                        int metroIndex = (int) Math.floor(((double) i * metroCodes.size()) / desiredCount);
                        String metroCode = metroCodes.get(metroIndex);
                        String era = eraCycle.get(i % eraCycle.size());
                        String metroSlug = metroCode.toLowerCase().replace("_", "-");
                        String eraSlug = era.toLowerCase().replace("_", "-");
                        String linkText = TextUtil.formatEraText(era) + " Homes in " + TextUtil.formatMetroName(metroCode);

                        links.add(new com.livingcostcheck.home_repair.seo.InternalLinkBuilder.InternalLink(
                                        linkText,
                                        "/home-repair/verdicts/" + metroSlug + "/" + eraSlug + ".html"));
                }

                return links;
        }

        private boolean shouldIndexRiskDetail(RiskAdjustedItem item, Verdict verdict, Object metroData) {
                if (item == null || verdict == null || verdict.getPlan() == null || verdict.getPlan().getMustDo() == null) {
                        return false;
                }

                String definition = normalizeNarrative(item.getDefinition());
                String scenario = normalizeNarrative(item.getDamageScenario());
                String explanation = normalizeNarrative(item.getExplanation());
                String combinedNarrative = String.join(" ", List.of(definition, scenario, explanation)).trim();
                List<String> itemTokensList = tokenizeForSimilarity(combinedNarrative);
                Set<String> uniqueItemTokens = new HashSet<>(itemTokensList);

                boolean hasStrongDefinition = definition.length() >= 80;
                boolean hasStrongScenario = scenario.length() >= 80;
                boolean hasMeaningfulCost = item.getAdjustedCost() >= 1000.0;
                boolean hasLocalData = metroData != null;
                boolean hasComparisonSet = verdict.getPlan().getMustDo().size() >= 3;
                boolean hasNarrativeDepth = combinedNarrative.length() >= 260 && itemTokensList.size() >= 45;
                boolean hasNoInternalMarkers = !INTERNAL_MARKER_PATTERN.matcher(combinedNarrative).find();
                boolean hasTokenDiversity = false;
                if (!itemTokensList.isEmpty()) {
                        double diversity = (double) uniqueItemTokens.size() / itemTokensList.size();
                        hasTokenDiversity = uniqueItemTokens.size() >= 24 && diversity >= 0.40;
                }

                boolean hasItemAnchoring = hasItemAnchoring(item.getPrettyName(), uniqueItemTokens);
                double maxSimilarity = maxSimilarityWithPeers(item, verdict.getPlan().getMustDo(), uniqueItemTokens);
                boolean isDistinctFromPeers = maxSimilarity < 0.78;

                return hasStrongDefinition && hasStrongScenario && hasMeaningfulCost && hasLocalData
                                && hasComparisonSet && hasNarrativeDepth && hasNoInternalMarkers
                                && hasTokenDiversity && hasItemAnchoring && isDistinctFromPeers;
        }

        private String normalizeNarrative(String raw) {
                if (raw == null || raw.isBlank()) {
                        return "";
                }
                String cleaned = HTML_TAG_PATTERN.matcher(raw).replaceAll(" ");
                cleaned = cleaned.replace('\u00a0', ' ');
                cleaned = MULTI_SPACE_PATTERN.matcher(cleaned).replaceAll(" ").trim();
                return cleaned;
        }

        private List<String> tokenizeForSimilarity(String raw) {
                if (raw == null || raw.isBlank()) {
                        return List.of();
                }
                String normalized = NON_ALNUM_PATTERN.matcher(raw.toLowerCase(Locale.ENGLISH)).replaceAll(" ").trim();
                if (normalized.isBlank()) {
                        return List.of();
                }

                List<String> tokens = new ArrayList<>();
                for (String token : normalized.split("\\s+")) {
                        if (token.length() < 3 || SEO_STOPWORDS.contains(token)) {
                                continue;
                        }
                        tokens.add(token);
                }
                return tokens;
        }

        private boolean hasItemAnchoring(String prettyName, Set<String> contentTokens) {
                List<String> anchors = tokenizeForSimilarity(prettyName);
                if (anchors.isEmpty()) {
                        return false;
                }
                int matches = 0;
                for (String anchor : anchors) {
                        if (contentTokens.contains(anchor)) {
                                matches++;
                        }
                }
                int requiredMatches = anchors.size() >= 3 ? 2 : 1;
                return matches >= requiredMatches;
        }

        private double maxSimilarityWithPeers(RiskAdjustedItem targetItem,
                        List<RiskAdjustedItem> allItems,
                        Set<String> targetTokens) {
                if (allItems == null || allItems.isEmpty() || targetTokens.isEmpty()) {
                        return 0.0;
                }

                double maxSimilarity = 0.0;
                for (RiskAdjustedItem peer : allItems) {
                        if (peer == null || peer.getItemCode() == null || targetItem.getItemCode() == null) {
                                continue;
                        }
                        if (peer.getItemCode().equalsIgnoreCase(targetItem.getItemCode())) {
                                continue;
                        }

                        String peerNarrative = String.join(" ",
                                        List.of(
                                                        normalizeNarrative(peer.getDefinition()),
                                                        normalizeNarrative(peer.getDamageScenario()),
                                                        normalizeNarrative(peer.getExplanation())))
                                        .trim();

                        Set<String> peerTokens = new HashSet<>(tokenizeForSimilarity(peerNarrative));
                        if (peerTokens.isEmpty()) {
                                continue;
                        }

                        double similarity = jaccardSimilarity(targetTokens, peerTokens);
                        if (similarity > maxSimilarity) {
                                maxSimilarity = similarity;
                        }
                }
                return maxSimilarity;
        }

        private double jaccardSimilarity(Set<String> a, Set<String> b) {
                if (a.isEmpty() || b.isEmpty()) {
                        return 0.0;
                }
                Set<String> intersection = new HashSet<>(a);
                intersection.retainAll(b);

                Set<String> union = new HashSet<>(a);
                union.addAll(b);
                if (union.isEmpty()) {
                        return 0.0;
                }
                return (double) intersection.size() / union.size();
        }

        private String extractStateCode(String metroCode) {
                if (metroCode == null || metroCode.isBlank()) {
                        return null;
                }
                String[] parts = metroCode.split("_");
                if (parts.length == 0) {
                        return null;
                }
                String maybeState = parts[parts.length - 1];
                return maybeState.matches("[A-Z]{2}") ? maybeState : null;
        }
}
