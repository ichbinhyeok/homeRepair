package com.livingcostcheck.home_repair.web;

import com.livingcostcheck.home_repair.domain.EventLog;
import com.livingcostcheck.home_repair.domain.VerdictHistory;
import com.livingcostcheck.home_repair.repository.EventLogRepository;
import com.livingcostcheck.home_repair.repository.HomeRepairRepository;
import com.livingcostcheck.home_repair.service.VerdictEngineService;
import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.*;
import com.livingcostcheck.home_repair.seo.VerdictSeoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import com.livingcostcheck.home_repair.util.TextUtil;

import java.util.List;
import java.util.UUID;

/**
 * Main controller for Home Repair Verdict Engine
 */
@Slf4j
@Controller
@RequestMapping("/home-repair")
@RequiredArgsConstructor
public class HomeRepairController {

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
                                        .sqft(sqft.intValue())
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
                        String stackTrace = java.util.Arrays.stream(e.getStackTrace())
                                        .limit(5)
                                        .map(StackTraceElement::toString)
                                        .collect(java.util.stream.Collectors.joining("\n"));
                        model.addAttribute("errorMessage", "DEBUG ERROR: " + e.toString() + "\nAT: " + stackTrace);
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
        public String dataSources() {
                return "pages/data-sources";
        }

        @GetMapping("/disclaimer")
        public String disclaimer() {
                return "pages/disclaimer";
        }

        // -------------------------------------------------------------------------
        // DYNAMIC LEVEL 2: RISK DETAIL PSEO
        // -------------------------------------------------------------------------
        @GetMapping("/verdicts/{metro}/{era}/{riskItem}")
        public Object viewRiskDetail(@PathVariable String metro,
                        @PathVariable String era,
                        @PathVariable String riskItem,
                        Model model) {

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

                model.addAttribute("title",
                                String.format("Don't Overpay: %s Cost in %s [2026 Data]",
                                                targetItem.getPrettyName(), metroName));
                model.addAttribute("targetItem", targetItem); // Template expects 'item' or we map it
                model.addAttribute("item", targetItem); // Mapping to 'item' as per template
                model.addAttribute("itemSlug", finalSlug);
                model.addAttribute("verdict", verdict);
                model.addAttribute("metroCode", context.getMetroCode());
                model.addAttribute("metroName", metroName);
                model.addAttribute("era", era);
                model.addAttribute("eraName", eraName);
                model.addAttribute("baseUrl", "https://lifeverdict.com");

                // Injected Data
                model.addAttribute("regionalInsight", regionalInsight);
                model.addAttribute("climateZone", climateZone);
                model.addAttribute("metroRisk", metroRisk);
                model.addAttribute("foundation", foundation);

                // Internal Links (Simplified for Dynamic)
                String parentUrl = "/home-repair/verdicts/" + metro + "/" + era;
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
                } catch (Exception e) {
                        log.error("Failed to write lead to CSV", e);
                }

                return ResponseEntity.ok(
                                "<div class='p-3 bg-emerald-50 border border-emerald-200 text-emerald-800 rounded-lg text-sm text-center font-medium'><strong>Success!</strong> Prepared checklist has been securely linked to "
                                                + email
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
                List<String> states = java.util.Arrays.asList(
                                "AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA",
                                "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD",
                                "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ",
                                "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC",
                                "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY", "DC");
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

                model.addAttribute("riskSlug", riskSlug);
                model.addAttribute("titleH1", titles.get(riskSlug));
                model.addAttribute("l1Links", buildL1LinksForRiskHub());
                model.addAttribute("baseUrl", "https://lifeverdict.com");
                return "seo/risk-hub";
        }

        private List<com.livingcostcheck.home_repair.seo.InternalLinkBuilder.InternalLink> buildL1LinksForRiskHub() {
                return java.util.Arrays.asList(
                                new com.livingcostcheck.home_repair.seo.InternalLinkBuilder.InternalLink(
                                                "Older Homes in Scranton, PA",
                                                "/home-repair/verdicts/scranton-pa/pre-1950"),
                                new com.livingcostcheck.home_repair.seo.InternalLinkBuilder.InternalLink(
                                                "Older Homes in Syracuse, NY",
                                                "/home-repair/verdicts/syracuse-ny/pre-1950"),
                                new com.livingcostcheck.home_repair.seo.InternalLinkBuilder.InternalLink(
                                                "1950s Homes in Cleveland, OH",
                                                "/home-repair/verdicts/cleveland-oh/1950-1970"),
                                new com.livingcostcheck.home_repair.seo.InternalLinkBuilder.InternalLink(
                                                "Mid-Century Homes in Kansas City, MO",
                                                "/home-repair/verdicts/kansas-city-mo/1950-1970"),
                                new com.livingcostcheck.home_repair.seo.InternalLinkBuilder.InternalLink(
                                                "1970s Homes in Albuquerque, NM",
                                                "/home-repair/verdicts/albuquerque-nm/1970-1980"),
                                new com.livingcostcheck.home_repair.seo.InternalLinkBuilder.InternalLink(
                                                "1970s Homes in Omaha, NE",
                                                "/home-repair/verdicts/omaha-ne/1970-1980"),
                                new com.livingcostcheck.home_repair.seo.InternalLinkBuilder.InternalLink(
                                                "1980s Homes in Tulsa, OK",
                                                "/home-repair/verdicts/tulsa-ok/1980-1995"),
                                new com.livingcostcheck.home_repair.seo.InternalLinkBuilder.InternalLink(
                                                "1980s Homes in Wichita, KS",
                                                "/home-repair/verdicts/wichita-ks/1980-1995"),
                                new com.livingcostcheck.home_repair.seo.InternalLinkBuilder.InternalLink(
                                                "1990s Homes in Fresno, CA",
                                                "/home-repair/verdicts/fresno-ca/1980-1995"),
                                new com.livingcostcheck.home_repair.seo.InternalLinkBuilder.InternalLink(
                                                "1990s Homes in Des Moines, IA",
                                                "/home-repair/verdicts/des-moines-ia/1980-1995"),
                                new com.livingcostcheck.home_repair.seo.InternalLinkBuilder.InternalLink(
                                                "Newer Homes in Boise, ID",
                                                "/home-repair/verdicts/boise-city-id/1995-2010"),
                                new com.livingcostcheck.home_repair.seo.InternalLinkBuilder.InternalLink(
                                                "Newer Homes in Provo, UT",
                                                "/home-repair/verdicts/provo-orem-ut/1995-2010"));
        }
}
