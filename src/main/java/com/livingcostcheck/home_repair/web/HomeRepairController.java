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
    public String result(@PathVariable("uuid") UUID uuid, Model model) {
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
            List<String> combinedHistory = history.getRepairHistory() != null && !history.getRepairHistory().isEmpty()
                    ? java.util.Arrays.asList(history.getRepairHistory().split(","))
                    : java.util.Collections.emptyList();

            // Distribute based on known prefixes or lists
            List<String> coreHistory = new java.util.ArrayList<>();
            List<String> livingHistory = new java.util.ArrayList<>();

            for (String h : combinedHistory) {
                if (h.contains("ROOF") || h.contains("HVAC") || h.contains("ELEC_PANEL") || h.contains("PLUMBING")) {
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
                    .condition(history.getHouseCondition() != null ? history.getHouseCondition() : "UNKNOWN")
                    .isFpePanel(history.getIsFpePanel())
                    .isPolyB(history.getIsPolyB())
                    .isAluminum(history.getIsAluminum())
                    .isChineseDrywall(history.getIsChineseDrywall())
                    .build();

            Verdict verdict = verdictEngineService.generateVerdict(context);

            // CTR Optimization: Verdict-First Titles & Decision-Oriented H1s
            String city = TextUtil.formatMetroName(history.getZipCode());

            // Use VerdictSeoService for "Outlook" headers (Contextual)
            VerdictSeoService.SeoVariant seoVariant = verdictSeoService.getDynamicResultHeader(verdict, city);

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
    public String viewRiskDetail(@PathVariable String metro,
            @PathVariable String era,
            @PathVariable String riskItem,
            Model model) {

        // 1. Generate core verdict logic
        UserContext context = UserContext.builder()
                .metroCode(metro.replace("-", "_").toUpperCase())
                .era(era.replace("-", "_").toUpperCase())
                .budget(0.0) // Info page assumption
                .relationship(RelationshipToHouse.LIVING)
                .build();

        Verdict verdict = verdictEngineService.generateVerdict(context);

        // 2. Find specific risk item
        RiskAdjustedItem targetItem = verdict.getPlan().getMustDo().stream()
                .filter(item -> item.getItemCode().toLowerCase().replace("_", "-")
                        .equals(riskItem.replace(".html", "")))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Risk item not found: " + riskItem));

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
                String.format("%s in %s: $%,.0f Cost Guide (%s Homes)",
                        targetItem.getPrettyName(), metroName, targetItem.getAdjustedCost(), eraName));
        model.addAttribute("targetItem", targetItem); // Template expects 'item' or we map it
        model.addAttribute("item", targetItem); // Mapping to 'item' as per template
        model.addAttribute("itemSlug", riskItem.replace(".html", ""));
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
        String parentUrl = "/home-repair/verdicts/" + metro + "/" + era + ".html";
        model.addAttribute("parentUrl", parentUrl);
        model.addAttribute("canonicalUrl",
                "https://lifeverdict.com/home-repair/verdicts/" + metro + "/" + era + "/" + riskItem + ".html");

        // Helper Schemas (Empty for now, can be refactored to shared service)
        model.addAttribute("faqSchema", "");
        model.addAttribute("breadcrumbSchema", "");

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

        return ResponseEntity.ok("Report Sent");
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

    private boolean isValidTarget(String target) {
        if (target == null)
            return false;
        // Whitelist allowed domains
        return target.startsWith("https://example.com") ||
                target.startsWith("http://localhost");
    }
}
