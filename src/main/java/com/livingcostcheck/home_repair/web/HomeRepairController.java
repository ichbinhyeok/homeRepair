package com.livingcostcheck.home_repair.web;

import com.livingcostcheck.home_repair.domain.EventLog;
import com.livingcostcheck.home_repair.domain.VerdictHistory;
import com.livingcostcheck.home_repair.repository.EventLogRepository;
import com.livingcostcheck.home_repair.repository.HomeRepairRepository;
import com.livingcostcheck.home_repair.service.VerdictEngineService;
import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.*;
import com.livingcostcheck.home_repair.seo.StaticPageGeneratorService;
import com.livingcostcheck.home_repair.seo.VerdictSeoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.UUID;
import java.util.*;

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

    // Helper methods for formatting (duplicated from StaticPageGeneratorService for
    // now to avoid cross-service dependency or should be moved to a Util)
    private String formatMetroName(String metroCode) {
        if (metroCode == null)
            return "Unknown";
        String[] parts = metroCode.split("_");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (i == parts.length - 1 && part.length() == 2) {
                result.append(part);
            } else {
                result.append(part.substring(0, 1).toUpperCase())
                        .append(part.substring(1).toLowerCase());
            }
            if (i < parts.length - 1)
                result.append(" ");
        }
        return result.toString();
    }

    private String formatEraName(String era) {
        if (era == null)
            return "Unknown";
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
                    .sqft(sqft)
                    .relationship(relationship)
                    .history(history != null ? history : java.util.Collections.emptyList())
                    .condition(condition)
                    .isFpePanel(isFpePanel)
                    .isPolyB(isPolyB)
                    .isAluminum(isAluminum)
                    .isChineseDrywall(isChineseDrywall)
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
            String city = formatMetroName(history.getZipCode());

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
        model.addAttribute("baseUrl", "https://livingcostcheck.com");
        return "pages/about";
    }

    @GetMapping("/methodology")
    public String methodology(Model model) {
        model.addAttribute("baseUrl", "https://livingcostcheck.com");
        return "pages/methodology";
    }

    @GetMapping("/editorial-policy")
    public String editorialPolicy(Model model) {
        model.addAttribute("baseUrl", "https://livingcostcheck.com");
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
