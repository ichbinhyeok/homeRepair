package com.livingcostcheck.home_repair.web;

import com.livingcostcheck.home_repair.domain.EventLog;
import com.livingcostcheck.home_repair.domain.VerdictHistory;
import com.livingcostcheck.home_repair.repository.EventLogRepository;
import com.livingcostcheck.home_repair.repository.HomeRepairRepository;
import com.livingcostcheck.home_repair.service.VerdictService;
import com.livingcostcheck.home_repair.service.dto.VerdictResult;
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
    private final VerdictService verdictService;

    @GetMapping
    public String index() {
        return "pages/index";
    }

    // Support both GET (for referral links/boosters) and POST (for main form)
    @RequestMapping(value = "/calculate", method = { RequestMethod.GET, RequestMethod.POST })
    public String calculate(@RequestParam String zipCode,
            @RequestParam String budget,
            @RequestParam String purpose,
            @RequestParam String decade,
            @RequestParam(defaultValue = "anonymous") String userEmail) {

        // 1. Delegate Logic to Service
        VerdictResult result = verdictService.determineVerdict(budget, decade, purpose);

        // 2. Hash Context (MVP)
        String contextHash = Integer.toHexString((zipCode + purpose + decade + budget).hashCode());

        // 3. Persistence
        VerdictHistory history = new VerdictHistory(zipCode, budget, purpose, decade, result.getCode(), "v2.0",
                contextHash);
        if (!"anonymous".equals(userEmail)) {
            history.setUserEmail(userEmail);
        }
        repository.save(history);

        return "redirect:/home-repair/result/" + history.getId();
    }

    @GetMapping("/result/{uuid}")
    public String result(@PathVariable UUID uuid, Model model) {
        VerdictHistory history = repository.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Verdict ID"));

        // Re-calculate view data based on stored inputs
        VerdictResult resultData = verdictService.determineVerdict(history.getBudget(), history.getDecade(),
                history.getPurpose());

        model.addAttribute("history", history);
        model.addAttribute("verdict", resultData);
        model.addAttribute("helper", verdictService);
        return "pages/result";
    }

    // -------------------------------------------------------------------------
    // API & TRACKING (AJAX/Redirects)
    // -------------------------------------------------------------------------

    @PostMapping("/api/lead")
    @ResponseBody
    public ResponseEntity<String> captureLead(@RequestParam UUID verdictId, @RequestParam String email) {
        VerdictHistory history = repository.findById(verdictId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ID"));

        history.setUserEmail(email);
        repository.save(history);

        eventLogRepository.save(new EventLog(verdictId, EventLog.EventType.SUBMIT_EMAIL, email));

        return ResponseEntity.ok("Report Sent");
    }

    @GetMapping("/track")
    public RedirectView trackClick(@RequestParam UUID verdictId,
            @RequestParam String type,
            @RequestParam String target) {

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
