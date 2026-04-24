package com.livingcostcheck.home_repair.web;

import com.livingcostcheck.home_repair.domain.EventLog;
import com.livingcostcheck.home_repair.domain.VerdictHistory;
import com.livingcostcheck.home_repair.service.InspectionDocumentService;
import com.livingcostcheck.home_repair.repository.EventLogRepository;
import com.livingcostcheck.home_repair.repository.HomeRepairRepository;
import com.livingcostcheck.home_repair.service.AcquisitionTelemetryService;
import com.livingcostcheck.home_repair.service.InspectionResponseService;
import com.livingcostcheck.home_repair.service.VerdictEngineService;
import com.livingcostcheck.home_repair.service.dto.inspection.InspectionResponseInput;
import com.livingcostcheck.home_repair.service.dto.inspection.InspectionResponsePacket;
import com.livingcostcheck.home_repair.service.dto.inspection.InspectionCaseWorkflowSummary;
import com.livingcostcheck.home_repair.service.dto.inspection.InspectionWorkspaceSummary;
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
import org.springframework.web.multipart.MultipartFile;
import com.livingcostcheck.home_repair.util.TextUtil;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
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
        private static final DateTimeFormatter WORKSPACE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MMM d, h:mm a",
                        Locale.ENGLISH);
        private final HomeRepairRepository repository;
        private final EventLogRepository eventLogRepository;
        private final VerdictEngineService verdictEngineService;
        private final com.livingcostcheck.home_repair.seo.VerdictSeoService verdictSeoService;
        private final InspectionResponseService inspectionResponseService;
        private final InspectionDocumentService inspectionDocumentService;
        private final AcquisitionTelemetryService acquisitionTelemetryService;

        @GetMapping
        public String index(@RequestParam(value = "metroCode", required = false) String selectedMetroCode,
                        @RequestParam(value = "era", required = false) String selectedEra,
                        @RequestParam(value = "relationship", required = false) String selectedRelationship,
                        @RequestParam(value = "entry", required = false) String selectedEntry,
                        @RequestParam(value = "legacy", required = false) String legacy,
                        Model model) {
                // Step 1: Tool landing. Market and era remain optional context; the
                // product surface is the inspection ask pre-send check.
                populateIndexModel(model, selectedMetroCode, selectedEra, selectedRelationship, selectedEntry);
                acquisitionTelemetryService.recordToolOpen(normalizeAcquisitionEntry(selectedEntry), "/home-repair");
                if ("planner".equalsIgnoreCase(legacy)) {
                        model.addAttribute("legacyNotice",
                                        "The old repair-cost planner is archived. Paste the proposed inspection ask and findings here to run a pre-send check.");
                }

                return "pages/index";
        }

        private void populateIndexModel(Model model,
                        String selectedMetroCode,
                        String selectedEra,
                        String selectedRelationship,
                        String selectedEntry) {
                List<String> metros = verdictEngineService.getMetroMasterData().getData().keySet().stream()
                                .sorted()
                                .toList();

                List<String> eras = java.util.List.of(
                                "PRE_1950",
                                "1950_1970",
                                "1970_1980",
                                "1980_1995",
                                "1995_2010",
                                "2010_PRESENT");

                model.addAttribute("metros", metros);
                model.addAttribute("eras", eras);
                model.addAttribute("selectedMetroCode", selectedMetroCode);
                model.addAttribute("selectedEra", selectedEra);
                model.addAttribute("selectedRelationship", selectedRelationship != null ? selectedRelationship : "BUYING");
                String normalizedEntry = normalizeAcquisitionEntry(selectedEntry);
                model.addAttribute("selectedEntry", normalizedEntry);
                model.addAttribute("selectedSurface", resolveSelectedSurface(normalizedEntry));
                model.addAttribute("acquisitionSurfaces", AcquisitionSurface.indexableSurfaces());
                model.addAttribute("title", "Inspection Ask Pre-Send Check");
                model.addAttribute("recentWorkspaces", java.util.List.of());
        }

        private AcquisitionSurface resolveSelectedSurface(String normalizedEntry) {
                if (normalizedEntry == null || normalizedEntry.isBlank() || "direct".equals(normalizedEntry)) {
                        return null;
                }
                return AcquisitionSurface.indexableSurfaces().stream()
                                .filter(surface -> surface.code().equals(normalizedEntry))
                                .findFirst()
                                .orElse(null);
        }

        @PostMapping("/step-2")
        public RedirectView step2(@RequestParam("metroCode") String metroCode,
                        @RequestParam("era") String era,
                        @RequestParam("relationship") String relationship) {
                String location = String.format("/home-repair?metroCode=%s&era=%s&relationship=%s&legacy=planner",
                                urlEncode(metroCode), urlEncode(era), urlEncode(relationship));
                RedirectView redirectView = new RedirectView(location);
                redirectView.setStatusCode(org.springframework.http.HttpStatus.SEE_OTHER);
                return redirectView;
        }

        @PostMapping("/verdict")
        public String generateVerdict(
                        @RequestParam(value = "metroCode", required = false) String metroCode,
                        @RequestParam(value = "era", required = false) String era,
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
                        @RequestParam(value = "inspectionReportText", required = false) String inspectionReportText,
                        @RequestParam(value = "inspectionFinding", required = false) List<String> inspectionFindings,
                        @RequestParam(value = "inspectionReportFile", required = false) MultipartFile inspectionReportFile,
                        @RequestParam(value = "quoteSupport", defaultValue = "NONE") String quoteSupport,
                        @RequestParam(value = "closingWindow", defaultValue = "FLEXIBLE") String closingWindow,
                        @RequestParam(value = "contractWorkflow", defaultValue = "AUTO") String contractWorkflow,
                        @RequestParam(value = "dealStage", defaultValue = "DRAFTING_FIRST_NOTICE") String dealStage,
                        @RequestParam(value = "responseDeadlineAt", required = false) String responseDeadlineAt,
                        @RequestParam(value = "loanType", defaultValue = "CONVENTIONAL") String loanTypeStr,
                        @RequestParam(value = "caseLabel", required = false) String caseLabel,
                        @RequestParam(value = "propertyAddress", required = false) String propertyAddress,
                        @RequestParam(value = "clientName", required = false) String clientName,
                        @RequestParam(value = "agentName", required = false) String agentName,
                        @RequestParam(value = "entry", required = false) String entry,
                        @RequestParam(value = "userEmail", defaultValue = "anonymous") String userEmail,
                        Model model) {

                try {
                        String acquisitionEntry = normalizeAcquisitionEntry(entry);
                        PropertyContext propertyContext = resolvePropertyContext(metroCode, era);
                        // Manual Context Construction to prevent mapping errors
                        RelationshipToHouse relationship = RelationshipToHouse.LIVING;
                        try {
                                relationship = RelationshipToHouse.valueOf(relationshipStr);
                        } catch (Exception e) {
                                log.warn("Invalid relationship param: {}", relationshipStr);
                        }
                        LoanType loanType = LoanType.CONVENTIONAL;
                        try {
                                loanType = LoanType.valueOf(loanTypeStr);
                        } catch (Exception e) {
                                log.warn("Invalid loanType param: {}", loanTypeStr);
                        }
                        List<String> extractedInspectionFindings = inspectionResponseService.extractFindings(
                                        inspectionReportText, inspectionFindings);
                        if (extractedInspectionFindings.isEmpty()) {
                                populateIndexModel(model, metroCode, era, relationshipStr, acquisitionEntry);
                                model.addAttribute("errorMessage",
                                                "Paste at least one proposed ask or inspection finding before running the pre-send check.");
                                return "pages/index";
                        }
                        InspectionDocumentService.DocumentEvidenceResult documentEvidence;
                        try {
                                documentEvidence = inspectionDocumentService.extractEvidence(inspectionReportFile,
                                                extractedInspectionFindings);
                        } catch (IllegalArgumentException e) {
                                populateIndexModel(model, metroCode, era, relationshipStr, acquisitionEntry);
                                model.addAttribute("errorMessage", e.getMessage());
                                return "pages/index";
                        }

                        UserContext context = UserContext.builder()
                                        .metroCode(propertyContext.metroCode())
                                        .era(propertyContext.era())
                                        .budget(budget)
                                        .sqft(sqft != null ? sqft.intValue() : 1800)
                                        .relationship(relationship)
                                        .loanType(loanType)
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
                        String historyStr = inspectionResponseService.buildStoredContext(context.getHistory(),
                                        extractedInspectionFindings,
                                        documentEvidence.evidenceRefs(),
                                        documentEvidence.sourceLabel(),
                                        quoteSupport, closingWindow, contractWorkflow, dealStage, responseDeadlineAt, context.getLoanType(),
                                        caseLabel, propertyAddress, clientName, agentName,
                                        propertyContext.marketLabel(), propertyContext.eraLabel(),
                                        acquisitionEntry);
                        verdictHistory.setRepairContext(historyStr, context.getCondition());
                        verdictHistory.setForensicClues(
                                        context.getIsFpePanel(),
                                        context.getIsPolyB(),
                                        context.getIsAluminum(),
                                        context.getIsChineseDrywall());

                        repository.save(verdictHistory);
                        eventLogRepository.save(new EventLog(
                                        verdictHistory.getId(),
                                        EventLog.EventType.PACKET_GENERATED,
                                        buildVariantEventTarget(acquisitionEntry, "generated")));

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
                        CaseBundle caseBundle = loadCaseBundle(uuid);

                        if (log.isDebugEnabled()) {
                                log.debug("Loading result for ID: {}", uuid);
                        }

                        String city = caseBundle.input().marketContextLabel().isBlank()
                                        ? TextUtil.formatMetroName(caseBundle.history().getZipCode())
                                        : caseBundle.input().marketContextLabel();

                        VerdictSeoService.SeoVariant seoVariant = verdictSeoService
                                        .getDynamicResultHeader(caseBundle.verdict(), city);
                        InspectionCaseWorkflowSummary caseWorkflow = inspectionResponseService
                                        .buildCaseWorkflow(caseBundle.packet(), caseBundle.events());
                        InspectionWorkspaceSummary workspace = buildWorkspaceSummary(caseBundle.history(),
                                        caseBundle.input(), city);

                        model.addAttribute("title", seoVariant.title());
                        model.addAttribute("verdictH1", seoVariant.h1());
                        model.addAttribute("verdict", caseBundle.verdict());
                        model.addAttribute("history", caseBundle.history());
                        model.addAttribute("packet", caseBundle.packet());
                        model.addAttribute("caseWorkflow", caseWorkflow);
                        model.addAttribute("workspace", workspace);
                        model.addAttribute("acquisitionEntry", caseBundle.input().acquisitionEntry());
                        model.addAttribute("sellerCreditSummaryJs", escapeJs(caseBundle.packet().sellerCreditSummary()));
                        model.addAttribute("agentNegotiationScriptJs",
                                        escapeJs(caseBundle.packet().agentNegotiationScript()));
                        model.addAttribute("fullPacketTextJs", escapeJs(caseBundle.packet().fullPacketText()));
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
        // API & TRACKING (AJAX/Redirects)
        // -------------------------------------------------------------------------

        @PostMapping("/api/lead")
        @ResponseBody
        public ResponseEntity<String> captureLead(@RequestParam("verdictId") UUID verdictId,
                        @RequestParam("email") String email,
                        @RequestParam(value = "entry", required = false) String entry) {
                VerdictHistory history = repository.findById(verdictId)
                                .orElseThrow(() -> new IllegalArgumentException("Invalid ID"));

                history.setUserEmail(email);
                repository.save(history);

                eventLogRepository.save(new EventLog(verdictId, EventLog.EventType.SUBMIT_EMAIL,
                                buildVariantEventTarget(normalizeAcquisitionEntry(entry), "optional_email")));

                // Save lead to CSV
                try {
                        java.io.File file = new java.io.File("data/leads.csv");
                        file.getParentFile().mkdirs();
                        synchronized (this) {
                                boolean isNew = !file.exists();
                                try (java.io.FileWriter fw = new java.io.FileWriter(file, true);
                                                java.io.PrintWriter pw = new java.io.PrintWriter(fw)) {
                                        if (isNew) {
                                                pw.println("Timestamp,VerdictId,Entry,Email,MetroCode,Era");
                                        }
                                        String timestamp = java.time.LocalDateTime.now()
                                                        .format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                                        pw.printf("%s,%s,%s,%s,%s,%s%n",
                                                        timestamp,
                                                        verdictId.toString(),
                                                        normalizeAcquisitionEntry(entry),
                                                        email,
                                                        history.getZipCode(),
                                                        history.getDecade());
                                }
                        }
                } catch (Exception e) {
                        log.error("Failed to write lead to CSV", e);
                }

                return ResponseEntity.ok(
                                "<div class='p-3 bg-emerald-50 border border-emerald-200 text-emerald-800 rounded-lg text-sm text-center font-medium'><strong>Saved.</strong> We linked this packet to "
                                                + HtmlUtils.htmlEscape(email)
                                                + ".<br><span class='font-normal text-xs mt-1 block'>No paywall is active during validation. Use the packet freely and tell us if it helped.</span></div>");
        }

        @PostMapping("/api/agent-desk")
        @ResponseBody
        public ResponseEntity<String> captureAgentDeskLead(@RequestParam("verdictId") UUID verdictId,
                        @RequestParam("email") String email,
                        @RequestParam(value = "entry", required = false) String entry,
                        @RequestParam(value = "role", required = false) String role,
                        @RequestParam(value = "teamSize", required = false) String teamSize,
                        @RequestParam(value = "monthlyVolume", required = false) String monthlyVolume,
                        @RequestParam(value = "note", required = false) String note) {
                VerdictHistory history = repository.findById(verdictId)
                                .orElseThrow(() -> new IllegalArgumentException("Invalid ID"));
                String normalizedEntry = normalizeAcquisitionEntry(entry);

                String sanitizedEmail = sanitizeOptionalEventField(email);
                if (sanitizedEmail.isBlank()) {
                        return ResponseEntity.badRequest().body("Email is required.");
                }
                String sanitizedRole = sanitizeOptionalEventField(role);
                String sanitizedTeamSize = sanitizeOptionalEventField(teamSize);
                String sanitizedMonthlyVolume = sanitizeOptionalEventField(monthlyVolume);
                String sanitizedNote = sanitizeOptionalEventField(note);

                String target = "entry=" + normalizedEntry
                                + "|email=" + urlEncode(sanitizedEmail)
                                + "|role=" + urlEncode(sanitizedRole)
                                + "|teamSize=" + urlEncode(sanitizedTeamSize)
                                + "|monthlyVolume=" + urlEncode(sanitizedMonthlyVolume)
                                + "|note=" + urlEncode(sanitizedNote);
                eventLogRepository.save(new EventLog(verdictId, EventLog.EventType.REQUEST_AGENT_DESK, target));

                try {
                        java.io.File file = new java.io.File("data/agent-desk-leads.csv");
                        file.getParentFile().mkdirs();
                        synchronized (this) {
                                boolean isNew = !file.exists();
                                try (java.io.FileWriter fw = new java.io.FileWriter(file, true);
                                                java.io.PrintWriter pw = new java.io.PrintWriter(fw)) {
                                        if (isNew) {
                                                pw.println(
                                                                "Timestamp,VerdictId,Entry,Email,Role,TeamSize,MonthlyVolume,Note,MetroCode,Era");
                                        }
                                        String timestamp = java.time.LocalDateTime.now()
                                                        .format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                                        pw.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
                                                        timestamp,
                                                        verdictId.toString(),
                                                        escapeCsv(normalizedEntry),
                                                        escapeCsv(sanitizedEmail),
                                                        escapeCsv(sanitizedRole),
                                                        escapeCsv(sanitizedTeamSize),
                                                        escapeCsv(sanitizedMonthlyVolume),
                                                        escapeCsv(sanitizedNote),
                                                        escapeCsv(history.getZipCode()),
                                                        escapeCsv(history.getDecade()));
                                }
                        }
                } catch (Exception e) {
                        log.error("Failed to write agent desk lead to CSV", e);
                }

                return ResponseEntity.ok("Agent desk request saved. We will use this packet as the starting point.");
        }

        @PostMapping("/api/event")
        @ResponseBody
        public ResponseEntity<Void> captureEngagementEvent(@RequestParam("verdictId") UUID verdictId,
                        @RequestParam("eventType") String eventType,
                        @RequestParam(value = "target", defaultValue = "buying_packet") String target) {
                EventLog.EventType parsedEventType = parseEngagementEventType(eventType);
                if (parsedEventType == null || !repository.existsById(verdictId)) {
                        return ResponseEntity.badRequest().build();
                }

                eventLogRepository.save(new EventLog(verdictId, parsedEventType, sanitizeEventTarget(target)));
                return ResponseEntity.noContent().build();
        }

        @PostMapping("/api/workflow-state")
        @ResponseBody
        public ResponseEntity<String> captureWorkflowState(@RequestParam("verdictId") UUID verdictId,
                        @RequestParam("workflowAction") String workflowAction,
                        @RequestParam(value = "actor", required = false) String actor,
                        @RequestParam(value = "note", required = false) String note) {
                EventLog.EventType workflowEventType = parseWorkflowEventType(workflowAction);
                if (workflowEventType == null) {
                        return ResponseEntity.badRequest().body("Unknown workflow action.");
                }

                CaseBundle caseBundle;
                try {
                        caseBundle = loadCaseBundle(verdictId);
                } catch (IllegalArgumentException e) {
                        return ResponseEntity.badRequest().body("Unknown case.");
                }
                InspectionCaseWorkflowSummary workflowSummary = inspectionResponseService
                                .buildCaseWorkflow(caseBundle.packet(), caseBundle.events());

                if (workflowEventType == EventLog.EventType.MARK_SENT
                                && !"READY".equals(workflowSummary.currentState())) {
                        return ResponseEntity.badRequest()
                                        .body("This case is not in a ready state yet. Clear gates and record buyer approval first.");
                }
                if (workflowEventType == EventLog.EventType.COUNTER_RECEIVED
                                && !"SENT".equals(workflowSummary.currentState())) {
                        return ResponseEntity.badRequest()
                                        .body("Record a send event before logging a seller counter.");
                }
                if (workflowEventType == EventLog.EventType.RESOLUTION_SIGNED
                                && !List.of("SENT", "COUNTERED").contains(workflowSummary.currentState())) {
                        return ResponseEntity.badRequest()
                                        .body("A signed resolution belongs after send or counter, not before the packet is in the live negotiation.");
                }
                if (workflowEventType == EventLog.EventType.MARK_TERMINATED
                                && List.of("RESOLVED", "TERMINATED").contains(workflowSummary.currentState())) {
                        return ResponseEntity.badRequest()
                                        .body("This case is already closed.");
                }

                String snapshot = "status=" + caseBundle.packet().readinessLabel()
                                + ",gates=" + caseBundle.packet().readinessPassCount() + "/"
                                + caseBundle.packet().readinessGates().size()
                                + ",ask=$" + caseBundle.packet().targetAskLabel();
                String target = "state=" + workflowEventType.name()
                                + "|actor=" + urlEncode(sanitizeOptionalEventField(actor))
                                + "|note=" + urlEncode(sanitizeOptionalEventField(note))
                                + "|snapshot=" + urlEncode(sanitizeOptionalEventField(snapshot));
                eventLogRepository.save(new EventLog(verdictId, workflowEventType, target));

                String responseMessage = switch (workflowEventType) {
                        case REQUEST_REVIEW -> "Review state recorded.";
                        case BUYER_APPROVED -> "Buyer approval recorded.";
                        case MARK_SENT -> "Sent event recorded.";
                        case COUNTER_RECEIVED -> "Seller counter recorded.";
                        case RESOLUTION_SIGNED -> "Signed resolution recorded.";
                        case MARK_TERMINATED -> "Termination state recorded.";
                        case MARK_FILED -> "Filed event recorded.";
                        default -> "Workflow event recorded.";
                };
                return ResponseEntity.ok(responseMessage);
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

        @GetMapping("/workspace")
        public String workspace(Model model) {
                model.addAttribute("title", "Inspection Workspace | LifeVerdict");
                model.addAttribute("recentWorkspaces", java.util.List.of());
                return "pages/workspace";
        }

        private EventLog.EventType parseEngagementEventType(String rawEventType) {
                if (rawEventType == null || rawEventType.isBlank()) {
                        return null;
                }
                String normalized = rawEventType.trim().toUpperCase(Locale.ENGLISH).replace('-', '_');
                try {
                        EventLog.EventType eventType = EventLog.EventType.valueOf(normalized);
                        return switch (eventType) {
                                case PACKET_GENERATED, COPY_PACKET, COPY_AGENT_REQUEST, COPY_ASK_SUMMARY,
                                                PRINT_PACKET, SHARE_PACKET, SAVE_PACKET, SUBMIT_FEEDBACK -> eventType;
                                default -> null;
                        };
                } catch (IllegalArgumentException e) {
                        return null;
                }
        }

        private EventLog.EventType parseWorkflowEventType(String rawWorkflowAction) {
                if (rawWorkflowAction == null || rawWorkflowAction.isBlank()) {
                        return null;
                }
                String normalized = rawWorkflowAction.trim().toUpperCase(Locale.ENGLISH).replace('-', '_');
                return switch (normalized) {
                        case "REQUEST_REVIEW" -> EventLog.EventType.REQUEST_REVIEW;
                        case "BUYER_APPROVED" -> EventLog.EventType.BUYER_APPROVED;
                        case "MARK_SENT" -> EventLog.EventType.MARK_SENT;
                        case "COUNTER_RECEIVED" -> EventLog.EventType.COUNTER_RECEIVED;
                        case "RESOLUTION_SIGNED" -> EventLog.EventType.RESOLUTION_SIGNED;
                        case "MARK_TERMINATED" -> EventLog.EventType.MARK_TERMINATED;
                        case "MARK_FILED" -> EventLog.EventType.MARK_FILED;
                        default -> null;
                };
        }

        private String sanitizeEventTarget(String target) {
                if (target == null || target.isBlank()) {
                        return "buying_packet";
                }
                String sanitized = HTML_TAG_PATTERN.matcher(target).replaceAll(" ");
                sanitized = MULTI_SPACE_PATTERN.matcher(sanitized).replaceAll(" ").trim();
                return sanitized.length() > 160 ? sanitized.substring(0, 160) : sanitized;
        }

        private String sanitizeOptionalEventField(String value) {
                if (value == null || value.isBlank()) {
                        return "";
                }
                String sanitized = HTML_TAG_PATTERN.matcher(value).replaceAll(" ");
                sanitized = MULTI_SPACE_PATTERN.matcher(sanitized).replaceAll(" ").trim();
                return sanitized.length() > 160 ? sanitized.substring(0, 160) : sanitized;
        }

        private String escapeCsv(String value) {
                if (value == null) {
                        return "";
                }
                String escaped = value.replace("\"", "\"\"");
                return "\"" + escaped + "\"";
        }

        private CaseBundle loadCaseBundle(UUID verdictId) {
                VerdictHistory history = repository.findById(verdictId)
                                .orElseThrow(() -> new IllegalArgumentException("Invalid ID"));
                InspectionResponseInput parsedRepairContext = inspectionResponseService
                                .parseStoredContext(history.getRepairHistory());

                return buildCaseBundle(history, parsedRepairContext, verdictId);
        }

        private CaseBundle buildCaseBundle(VerdictHistory history,
                        InspectionResponseInput parsedRepairContext,
                        UUID verdictId) {

                RelationshipToHouse relationship = RelationshipToHouse.LIVING;
                try {
                        relationship = RelationshipToHouse.valueOf(history.getPurpose());
                } catch (Exception e) {
                        log.warn("Failed to parse relationship from history: {}", history.getPurpose());
                }

                double parsedBudget = 0.0;
                try {
                        if (history.getBudget() != null && !history.getBudget().equalsIgnoreCase("null")) {
                                parsedBudget = Double.parseDouble(history.getBudget());
                        }
                } catch (NumberFormatException e) {
                        log.warn("Failed to parse budget: {}", history.getBudget());
                }

                List<String> combinedHistory = parsedRepairContext.historyItems();
                List<String> coreHistory = new java.util.ArrayList<>();
                List<String> livingHistory = new java.util.ArrayList<>();
                for (String historyItem : combinedHistory) {
                        if (historyItem.contains("ROOF") || historyItem.contains("HVAC")
                                        || historyItem.contains("ELEC_PANEL") || historyItem.contains("PLUMBING")) {
                                coreHistory.add(historyItem);
                        } else {
                                livingHistory.add(historyItem);
                        }
                }

                UserContext context = UserContext.builder()
                                .metroCode(history.getZipCode())
                                .era(history.getDecade())
                                .budget(parsedBudget)
                                .relationship(relationship)
                                .loanType(parsedRepairContext.loanType())
                                .purpose(history.getPurpose())
                                .history(combinedHistory)
                                .coreSystemHistory(coreHistory)
                                .livingSpaceHistory(livingHistory)
                                .condition(history.getHouseCondition() != null ? history.getHouseCondition() : "UNKNOWN")
                                .isFpePanel(history.getIsFpePanel())
                                .isPolyB(history.getIsPolyB())
                                .isAluminum(history.getIsAluminum())
                                .isChineseDrywall(history.getIsChineseDrywall())
                                .build();

                Verdict verdict = verdictEngineService.generateVerdict(context);
                String city = parsedRepairContext.marketContextLabel().isBlank()
                                ? TextUtil.formatMetroName(history.getZipCode())
                                : parsedRepairContext.marketContextLabel();
                InspectionResponsePacket packet = inspectionResponseService.buildPacket(verdict, city, parsedRepairContext);
                List<EventLog> events = eventLogRepository.findByVerdictIdOrderByCreatedAtAsc(verdictId);
                return new CaseBundle(history, parsedRepairContext, verdict, packet, events);
        }

        private String normalizeAcquisitionEntry(String rawEntry) {
                if (rawEntry == null || rawEntry.isBlank()) {
                        return "direct";
                }
                String normalized = rawEntry.trim().toLowerCase(Locale.ENGLISH).replace('-', '_');
                if (AcquisitionSurface.isSurfaceCode(normalized)) {
                        return normalized;
                }
                return switch (normalized) {
                        case "agent_team", "sample_packet", "financing", "direct" -> normalized;
                        default -> "direct";
                };
        }

        private String buildVariantEventTarget(String acquisitionEntry, String action) {
                return "entry=" + normalizeAcquisitionEntry(acquisitionEntry) + "|action=" + sanitizeEventTarget(action);
        }

        private List<InspectionWorkspaceSummary> loadRecentWorkspaces() {
                return repository.findTop6ByOrderByCreatedAtDesc().stream()
                                .map(history -> {
                                        InspectionResponseInput input = inspectionResponseService
                                                        .parseStoredContext(history.getRepairHistory());
                                        String city = TextUtil.formatMetroName(history.getZipCode());
                                        return buildWorkspaceSummary(history, input, city);
                                })
                                .toList();
        }

        private InspectionWorkspaceSummary buildWorkspaceSummary(VerdictHistory history,
                        InspectionResponseInput input,
                        String city) {
                String caseLabel = input.caseLabel();
                if (caseLabel == null || caseLabel.isBlank()) {
                        if (input.propertyAddress() != null && !input.propertyAddress().isBlank()) {
                                caseLabel = input.propertyAddress();
                        } else {
                                caseLabel = city + " inspection packet";
                        }
                }

                String propertyAddress = input.propertyAddress();
                if (propertyAddress == null || propertyAddress.isBlank()) {
                        propertyAddress = "Market context: " + city;
                }

                String createdAtLabel = history.getCreatedAt() == null
                                ? "Draft"
                                : history.getCreatedAt().format(WORKSPACE_TIME_FORMATTER);

                return new InspectionWorkspaceSummary(
                                history.getId(),
                                caseLabel,
                                propertyAddress,
                                input.clientName(),
                                input.agentName(),
                                describePropertyContext(input, city),
                                describeClosingWindow(input.closingWindow()),
                                describeLoanType(input.loanType()),
                                createdAtLabel,
                                "/home-repair/result/" + history.getId());
        }

        private String escapeJs(String value) {
                if (value == null) {
                        return "";
                }
                return value
                                .replace("\\", "\\\\")
                                .replace("'", "\\'")
                                .replace("\r", "")
                                .replace("\n", "\\n");
        }

        private String urlEncode(String value) {
                return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
        }

        private String describeLoanType(LoanType loanType) {
                return switch (loanType) {
                        case FHA -> "FHA financing";
                        case VA -> "VA financing";
                        case CASH -> "cash offer";
                        case INVESTOR -> "investor financing";
                        default -> "conventional financing";
                };
        }

        private String describeClosingWindow(String closingWindow) {
                return switch (closingWindow) {
                        case "UNDER_7_DAYS" -> "response window under 7 days";
                        case "SEVEN_TO_TWENTY_ONE_DAYS" -> "response window 1-3 weeks";
                        case "TWENTY_ONE_TO_FORTY_FIVE_DAYS" -> "response window 3-6 weeks";
                        default -> "flexible response window";
                };
        }

        private String describePropertyContext(InspectionResponseInput input, String fallbackCity) {
                String marketLabel = input.marketContextLabel().isBlank() ? fallbackCity : input.marketContextLabel();
                String eraLabel = input.eraContextLabel();
                if (eraLabel == null || eraLabel.isBlank()) {
                        return marketLabel;
                }
                return marketLabel + " · " + eraLabel;
        }

        private PropertyContext resolvePropertyContext(String rawMetroCode, String rawEra) {
                String trimmedMetro = rawMetroCode == null ? "" : rawMetroCode.trim();
                String trimmedEra = rawEra == null ? "" : rawEra.trim();

                String effectiveMetro = trimmedMetro;
                String marketLabel;
                if (effectiveMetro.isBlank()) {
                        effectiveMetro = defaultBaselineMetro();
                        marketLabel = "Broad U.S. baseline";
                } else {
                        marketLabel = TextUtil.formatMetroName(effectiveMetro);
                }

                String effectiveEra = trimmedEra;
                String eraLabel;
                if (effectiveEra.isBlank()) {
                        effectiveEra = "1980_1995";
                        eraLabel = "typical mid-age housing stock";
                } else {
                        eraLabel = TextUtil.formatEraText(effectiveEra);
                }

                return new PropertyContext(effectiveMetro, effectiveEra, marketLabel, eraLabel);
        }

        private String defaultBaselineMetro() {
                return verdictEngineService.getMetroMasterData().getData().entrySet().stream()
                                .min(java.util.Comparator.comparingDouble(entry -> {
                                        Double laborMult = entry.getValue().getLaborMult();
                                        return laborMult == null ? Double.MAX_VALUE : Math.abs(laborMult - 1.0);
                                }))
                                .map(java.util.Map.Entry::getKey)
                                .orElse("ATLANTA_SANDY_SPRINGS_GA");
        }

        private record PropertyContext(String metroCode, String era, String marketLabel, String eraLabel) {
        }

        private record CaseBundle(VerdictHistory history,
                        InspectionResponseInput input,
                        Verdict verdict,
                        InspectionResponsePacket packet,
                        List<EventLog> events) {
        }

}
