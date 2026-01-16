package com.livingcostcheck.home_repair.web;

import com.livingcostcheck.home_repair.domain.EventLog;
import com.livingcostcheck.home_repair.domain.VerdictHistory;
import com.livingcostcheck.home_repair.repository.EventLogRepository;
import com.livingcostcheck.home_repair.repository.HomeRepairRepository;
import com.livingcostcheck.home_repair.service.VerdictEngineService;
import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.UUID;

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
        model.addAttribute("metroData", verdictEngineService.getMetroMasterData());
        return "pages/index";
    }

    @PostMapping("/step-2")
    public String step2(@RequestParam("metroCode") String metroCode, @RequestParam("era") String era, Model model) {
        // Step 2: Context Form
        model.addAttribute("metroCode", metroCode);
        model.addAttribute("era", era);
        return "pages/context";
    }

    @PostMapping("/verdict")
    public String generateVerdict(@ModelAttribute UserContext context,
            @RequestParam(value = "userEmail", defaultValue = "anonymous") String userEmail) {

        // 1. Generate Verdict
        Verdict verdict = verdictEngineService.generateVerdict(context);

        // 2. Persistence (History)
        VerdictHistory history = new VerdictHistory(
                context.getMetroCode(),
                String.valueOf(context.getBudget()),
                context.getPurpose(),
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
        // Store the detailed plan/verdict as JSON or re-generate on view?
        // For MVP, we'll re-generate on view or store transiently.
        // Standard pattern: Save simplified history, re-run engine on viewing result if
        // stateless.
        // Or serialize Verdict to JSON in history?
        // Current VerdictHistory entity might be simple. Let's assume re-calculation
        // for now or simplistic mapping.

        repository.save(history);

        return "redirect:/home-repair/result/" + history.getId();
    }

    @GetMapping("/result/{uuid}")
    public String result(@PathVariable UUID uuid, Model model) {
        VerdictHistory history = repository.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Verdict ID"));

        // Re-construct Context from History
        UserContext context = UserContext.builder()
                .metroCode(history.getZipCode()) // Storing MetroCode in ZipCode field for now
                .era(history.getDecade())
                .budget(Double.parseDouble(history.getBudget()))
                .purpose(history.getPurpose())
                // Load persisted context
                .history(history.getRepairHistory() != null && !history.getRepairHistory().isEmpty()
                        ? java.util.Arrays.asList(history.getRepairHistory().split(","))
                        : java.util.Collections.emptyList())
                .condition(history.getHouseCondition() != null ? history.getHouseCondition() : "UNKNOWN")
                .build();

        Verdict verdict = verdictEngineService.generateVerdict(context);

        model.addAttribute("verdict", verdict);
        model.addAttribute("history", history);
        return "pages/result";
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
            // Log error but ensure redirect happens
            e.printStackTrace();
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
