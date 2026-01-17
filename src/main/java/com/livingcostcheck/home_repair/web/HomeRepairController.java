package com.livingcostcheck.home_repair.web;

import com.livingcostcheck.home_repair.domain.EventLog;
import com.livingcostcheck.home_repair.domain.VerdictHistory;
import com.livingcostcheck.home_repair.repository.EventLogRepository;
import com.livingcostcheck.home_repair.repository.HomeRepairRepository;
import com.livingcostcheck.home_repair.service.VerdictEngineService;
import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/home-repair")
@RequiredArgsConstructor
public class HomeRepairController {

    private final HomeRepairRepository repository;
    private final EventLogRepository eventLogRepository;
    private final VerdictEngineService verdictEngineService;

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
    public String generateVerdict(@ModelAttribute UserContext context,
            @RequestParam(value = "userEmail", defaultValue = "anonymous") String userEmail,
            Model model) {

        try {
            // 1. Generate Verdict
            Verdict verdict = verdictEngineService.generateVerdict(context);

            // 2. Persistence (History)
            VerdictHistory history = new VerdictHistory(
                    context.getMetroCode(),
                    String.valueOf(context.getBudget()),
                    context.getRelationship() != null ? context.getRelationship().name() : "LIVING", // Default to
                                                                                                     // LIVING if null
                    context.getEra(),
                    verdict.getTier(), // Code/Result
                    "v2026.01",
                    String.valueOf(context.hashCode()) // Simple hash for context
            );
            if (!"anonymous".equals(userEmail)) {
                history.setUserEmail(userEmail);
            }

            // Save detailed context for re-generation
            String historyStr = context.getHistory() != null ? String.join(",", context.getHistory()) : "";
            history.setRepairContext(historyStr, context.getCondition());
            history.setForensicClues(
                    context.getIsFpePanel(),
                    context.getIsPolyB(),
                    context.getIsAluminum(),
                    context.getIsChineseDrywall());

            repository.save(history);

            return "redirect:/home-repair/result/" + history.getId();
        } catch (Exception e) {
            log.error("Error generating verdict", e);
            model.addAttribute("errorMessage", "Error generating verdict. Please try again or contact support.");
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
